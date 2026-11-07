package pe.edu.upsjb.novamarket.application;

import org.junit.jupiter.api.Test;
import pe.edu.upsjb.novamarket.domain.discount.FrequentCustomerDiscountStrategy;
import pe.edu.upsjb.novamarket.domain.discount.NewCustomerDiscountStrategy;
import pe.edu.upsjb.novamarket.domain.discount.UnidentifiedCustomerDiscountStrategy;
import pe.edu.upsjb.novamarket.domain.event.ObserverAction;
import pe.edu.upsjb.novamarket.domain.event.ObserverProcessingStatus;
import pe.edu.upsjb.novamarket.domain.event.ObserverTarget;
import pe.edu.upsjb.novamarket.domain.event.OrderConfirmedEvent;
import pe.edu.upsjb.novamarket.domain.event.OrderEventObserver;
import pe.edu.upsjb.novamarket.domain.model.ClientCategory;
import pe.edu.upsjb.novamarket.domain.model.Order;
import pe.edu.upsjb.novamarket.domain.model.OrderStatus;
import pe.edu.upsjb.novamarket.infrastructure.observer.AdministrationObserver;
import pe.edu.upsjb.novamarket.infrastructure.observer.CustomerNotificationObserver;
import pe.edu.upsjb.novamarket.infrastructure.observer.WarehouseObserver;
import pe.edu.upsjb.novamarket.infrastructure.persistence.InMemoryEventHistoryRepository;
import pe.edu.upsjb.novamarket.infrastructure.persistence.InMemoryOrderRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderConfirmationServiceTest {

    @Test
    void validOrderIsConfirmedAndAllObserversReceiveTheSameIdentifiableEvent() {
        TestFixture fixture = fixture(defaultObservers());
        Order order = fixture.registerFrequentOrder();

        OrderConfirmationResult result = fixture.confirmationService.confirm(order.id());

        assertTrue(result.newlyConfirmed());
        assertEquals(OrderStatus.CONFIRMED, result.order().status());
        assertNotNull(result.publishedEvent());
        assertNotNull(result.publishedEvent().eventId());
        assertEquals(order.id(), result.publishedEvent().order().id());
        assertEquals(3, result.observerResults().size());
        assertTrue(result.observerResults().stream()
                .allMatch(processing -> processing.eventId()
                        .equals(result.publishedEvent().eventId())));
        assertEquals(
                Set.of(ObserverTarget.CUSTOMER, ObserverTarget.WAREHOUSE,
                        ObserverTarget.ADMINISTRATION),
                result.observerResults().stream()
                        .map(processing -> processing.target())
                        .collect(java.util.stream.Collectors.toSet()));
        assertTrue(result.observerResults().stream()
                .allMatch(processing -> processing.status()
                        == ObserverProcessingStatus.SUCCESS));

        List<EventHistoryEntry> history = fixture.historyService.findAll();
        assertEquals(1, history.size());
        assertEquals(result.publishedEvent().eventId(), history.get(0).event().eventId());
        assertEquals(3, history.get(0).processingResults().size());
    }

    @Test
    void confirmingTwiceDoesNotDuplicateEventOrObserverActions() {
        TestFixture fixture = fixture(defaultObservers());
        Order order = fixture.registerFrequentOrder();

        OrderConfirmationResult first = fixture.confirmationService.confirm(order.id());
        OrderConfirmationResult second = fixture.confirmationService.confirm(order.id());

        assertTrue(first.newlyConfirmed());
        assertFalse(second.newlyConfirmed());
        assertTrue(second.event().isEmpty());
        assertTrue(second.observerResults().isEmpty());
        assertEquals(1, fixture.historyService.getSummary().eventCount());
        assertEquals(3, fixture.historyService.getSummary().processingCount());
    }

    @Test
    void newObserverCanBeAddedWithoutChangingConfirmationOrOrderServices() {
        AtomicReference<UUID> receivedEventId = new AtomicReference<>();
        List<OrderEventObserver> observers = new ArrayList<>(defaultObservers());
        observers.add(new OrderEventObserver() {
            @Override
            public String observerName() {
                return "AuditObserver";
            }

            @Override
            public ObserverTarget target() {
                return ObserverTarget.ADMINISTRATION;
            }

            @Override
            public ObserverAction onOrderConfirmed(OrderConfirmedEvent event) {
                receivedEventId.set(event.eventId());
                return new ObserverAction("Auditoría adicional registrada.");
            }
        });
        TestFixture fixture = fixture(observers);

        OrderConfirmationResult result =
                fixture.confirmationService.confirm(fixture.registerFrequentOrder().id());

        assertEquals(result.publishedEvent().eventId(), receivedEventId.get());
        assertEquals(4, result.observerResults().size());
        assertEquals(4, fixture.historyService.getSummary().processingCount());
    }

    @Test
    void observerFailureIsRecordedAndDoesNotInterruptOtherObservers() {
        List<OrderEventObserver> observers = new ArrayList<>();
        observers.add(new OrderEventObserver() {
            @Override
            public String observerName() {
                return "FailingObserver";
            }

            @Override
            public ObserverTarget target() {
                return ObserverTarget.CUSTOMER;
            }

            @Override
            public ObserverAction onOrderConfirmed(OrderConfirmedEvent event) {
                throw new IllegalStateException("Fallo controlado de prueba");
            }
        });
        observers.addAll(defaultObservers());
        TestFixture fixture = fixture(observers);

        OrderConfirmationResult result =
                fixture.confirmationService.confirm(fixture.registerFrequentOrder().id());

        assertEquals(4, result.observerResults().size());
        assertEquals(1, result.observerResults().stream()
                .filter(processing -> processing.status() == ObserverProcessingStatus.FAILED)
                .count());
        assertEquals(3, result.observerResults().stream()
                .filter(processing -> processing.status() == ObserverProcessingStatus.SUCCESS)
                .count());
        assertEquals(1, fixture.historyService.getSummary().failedProcessingCount());
        assertEquals(3, fixture.historyService.getSummary().successfulProcessingCount());
    }

    @Test
    void concurrentConfirmationsProduceOnlyOneEvent() throws Exception {
        TestFixture fixture = fixture(defaultObservers());
        Order order = fixture.registerFrequentOrder();
        ExecutorService executor = Executors.newFixedThreadPool(6);

        try {
            List<Callable<OrderConfirmationResult>> tasks = java.util.stream.IntStream
                    .range(0, 12)
                    .mapToObj(index -> (Callable<OrderConfirmationResult>)
                            () -> fixture.confirmationService.confirm(order.id()))
                    .toList();
            List<Future<OrderConfirmationResult>> futures = executor.invokeAll(tasks);

            long newlyConfirmed = 0;
            for (Future<OrderConfirmationResult> future : futures) {
                if (future.get().newlyConfirmed()) {
                    newlyConfirmed++;
                }
            }

            assertEquals(1, newlyConfirmed);
            assertEquals(1, fixture.historyService.getSummary().eventCount());
            assertEquals(3, fixture.historyService.getSummary().processingCount());
        } finally {
            executor.shutdownNow();
        }
    }

    private static List<OrderEventObserver> defaultObservers() {
        return List.of(
                new CustomerNotificationObserver(),
                new WarehouseObserver(),
                new AdministrationObserver());
    }

    private static TestFixture fixture(List<OrderEventObserver> observers) {
        Clock clock = Clock.fixed(
                Instant.parse("2026-11-07T15:30:00Z"),
                ZoneId.of("America/Lima"));
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryEventHistoryRepository historyRepository =
                new InMemoryEventHistoryRepository();
        DiscountCalculator calculator = new DiscountCalculator(List.of(
                new NewCustomerDiscountStrategy(),
                new FrequentCustomerDiscountStrategy(),
                new UnidentifiedCustomerDiscountStrategy()));
        OrderService orderService = new OrderService(orderRepository, calculator, clock);
        OrderEventPublisher publisher =
                new OrderEventPublisher(observers, historyRepository, clock);
        OrderConfirmationService confirmationService =
                new OrderConfirmationService(orderRepository, publisher, clock);
        EventHistoryService historyService = new EventHistoryService(historyRepository);
        return new TestFixture(orderService, confirmationService, historyService);
    }

    private record TestFixture(
            OrderService orderService,
            OrderConfirmationService confirmationService,
            EventHistoryService historyService) {

        private Order registerFrequentOrder() {
            return orderService.register(new RegisterOrderCommand(
                    "Andrea Salazar",
                    "74285916",
                    ClientCategory.FREQUENT,
                    new BigDecimal("200.00")));
        }
    }
}
