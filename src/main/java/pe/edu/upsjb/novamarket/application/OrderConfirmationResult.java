package pe.edu.upsjb.novamarket.application;

import pe.edu.upsjb.novamarket.domain.event.ObserverProcessingResult;
import pe.edu.upsjb.novamarket.domain.event.OrderConfirmedEvent;
import pe.edu.upsjb.novamarket.domain.model.Order;

import java.util.List;
import java.util.Optional;

public record OrderConfirmationResult(
        Order order,
        OrderConfirmedEvent publishedEvent,
        boolean newlyConfirmed,
        List<ObserverProcessingResult> observerResults) {

    public OrderConfirmationResult {
        observerResults = List.copyOf(observerResults);
    }

    public Optional<OrderConfirmedEvent> event() {
        return Optional.ofNullable(publishedEvent);
    }
}
