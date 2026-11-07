package pe.edu.upsjb.novamarket.application;

import org.junit.jupiter.api.Test;
import pe.edu.upsjb.novamarket.domain.discount.FrequentCustomerDiscountStrategy;
import pe.edu.upsjb.novamarket.domain.discount.NewCustomerDiscountStrategy;
import pe.edu.upsjb.novamarket.domain.discount.UnidentifiedCustomerDiscountStrategy;
import pe.edu.upsjb.novamarket.domain.model.ClientCategory;
import pe.edu.upsjb.novamarket.domain.model.Order;
import pe.edu.upsjb.novamarket.domain.model.OrderStatus;
import pe.edu.upsjb.novamarket.infrastructure.persistence.InMemoryOrderRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderServiceTest {

    @Test
    void registersOrderWithCalculatedDiscountAndStoresIt() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        DiscountCalculator calculator = new DiscountCalculator(List.of(
                new NewCustomerDiscountStrategy(),
                new FrequentCustomerDiscountStrategy(),
                new UnidentifiedCustomerDiscountStrategy()));
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-11-07T15:30:00Z"),
                ZoneId.of("America/Lima"));
        OrderService service = new OrderService(repository, calculator, fixedClock);

        Order order = service.register(new RegisterOrderCommand(
                "Andrea Salazar",
                "74285916",
                ClientCategory.FREQUENT,
                new BigDecimal("200.00")));

        assertNotNull(order.id());
        assertEquals("Andrea Salazar", order.customer().name());
        assertEquals(OrderStatus.PENDING, order.status());
        assertEquals(new BigDecimal("20.00"), order.discount().discountAmount());
        assertEquals(new BigDecimal("180.00"), order.discount().finalAmount());
        assertEquals(LocalDateTime.of(2026, 11, 7, 10, 30), order.createdAt());
        assertEquals(List.of(order), service.findAll());
    }

    @Test
    void dashboardSummaryAggregatesRegisteredOrders() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        DiscountCalculator calculator = new DiscountCalculator(List.of(
                new NewCustomerDiscountStrategy(),
                new FrequentCustomerDiscountStrategy(),
                new UnidentifiedCustomerDiscountStrategy()));
        OrderService service = new OrderService(repository, calculator, Clock.systemUTC());

        service.register(new RegisterOrderCommand(
                "Cliente Uno", "12345678", ClientCategory.NEW, new BigDecimal("200.00")));
        service.register(new RegisterOrderCommand(
                "Cliente Dos", "87654321", ClientCategory.FREQUENT, new BigDecimal("200.00")));

        DashboardSummary summary = service.getDashboardSummary();

        assertEquals(2, summary.orderCount());
        assertEquals(2, summary.pendingOrderCount());
        assertEquals(0, summary.confirmedOrderCount());
        assertEquals(new BigDecimal("400.00"), summary.grossAmount());
        assertEquals(new BigDecimal("30.00"), summary.discountAmount());
        assertEquals(new BigDecimal("370.00"), summary.netAmount());
    }
}
