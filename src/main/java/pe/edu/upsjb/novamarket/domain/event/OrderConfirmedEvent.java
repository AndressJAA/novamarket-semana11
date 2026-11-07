package pe.edu.upsjb.novamarket.domain.event;

import pe.edu.upsjb.novamarket.domain.model.Order;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record OrderConfirmedEvent(
        UUID eventId,
        Order order,
        LocalDateTime occurredAt) {

    public OrderConfirmedEvent {
        eventId = Objects.requireNonNull(eventId, "El identificador del evento es obligatorio");
        order = Objects.requireNonNull(order, "El pedido confirmado es obligatorio");
        occurredAt = Objects.requireNonNull(occurredAt, "La fecha del evento es obligatoria");
        if (order.id() == null || !order.isConfirmed()) {
            throw new IllegalArgumentException("El evento requiere un pedido confirmado y registrado");
        }
    }
}
