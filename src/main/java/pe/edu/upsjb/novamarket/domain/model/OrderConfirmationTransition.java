package pe.edu.upsjb.novamarket.domain.model;

import java.util.Objects;

public record OrderConfirmationTransition(Order order, boolean newlyConfirmed) {

    public OrderConfirmationTransition {
        order = Objects.requireNonNull(order, "El pedido de la transición es obligatorio");
    }
}
