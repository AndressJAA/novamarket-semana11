package pe.edu.upsjb.novamarket.application;

import org.springframework.stereotype.Service;
import pe.edu.upsjb.novamarket.domain.event.ObserverProcessingResult;
import pe.edu.upsjb.novamarket.domain.event.OrderConfirmedEvent;
import pe.edu.upsjb.novamarket.domain.model.OrderConfirmationTransition;
import pe.edu.upsjb.novamarket.domain.repository.OrderRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderConfirmationService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final Clock clock;

    public OrderConfirmationService(OrderRepository orderRepository,
                                    OrderEventPublisher eventPublisher,
                                    Clock clock) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    public OrderConfirmationResult confirm(long orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("El identificador del pedido no es válido");
        }

        OrderConfirmationTransition transition = orderRepository.confirmPending(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!transition.newlyConfirmed()) {
            return new OrderConfirmationResult(
                    transition.order(), null, false, List.of());
        }

        OrderConfirmedEvent event = new OrderConfirmedEvent(
                UUID.randomUUID(),
                transition.order(),
                LocalDateTime.now(clock));
        List<ObserverProcessingResult> results = eventPublisher.publish(event);

        return new OrderConfirmationResult(
                transition.order(), event, true, results);
    }
}
