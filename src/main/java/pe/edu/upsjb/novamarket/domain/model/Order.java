package pe.edu.upsjb.novamarket.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

public record Order(
        Long id,
        Customer customer,
        BigDecimal originalAmount,
        DiscountResult discount,
        OrderStatus status,
        LocalDateTime createdAt) {

    public Order {
        customer = Objects.requireNonNull(customer, "El cliente es obligatorio");
        discount = Objects.requireNonNull(discount, "El resultado del descuento es obligatorio");
        status = Objects.requireNonNull(status, "El estado es obligatorio");
        createdAt = Objects.requireNonNull(createdAt, "La fecha de registro es obligatoria");
        if (originalAmount == null || originalAmount.signum() <= 0) {
            throw new IllegalArgumentException("El importe debe ser mayor que cero");
        }
        originalAmount = originalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Order register(Customer customer, BigDecimal originalAmount,
                                 DiscountResult discount, LocalDateTime createdAt) {
        return new Order(null, customer, originalAmount, discount, OrderStatus.PENDING, createdAt);
    }

    public Order withId(long generatedId) {
        return new Order(generatedId, customer, originalAmount, discount, status, createdAt);
    }

    public Order confirm() {
        if (id == null) {
            throw new IllegalStateException("El pedido debe estar registrado antes de confirmarse");
        }
        if (status == OrderStatus.CONFIRMED) {
            return this;
        }
        if (discount.finalAmount().signum() < 0) {
            throw new IllegalStateException("El total del pedido no puede ser negativo");
        }
        return new Order(id, customer, originalAmount, discount, OrderStatus.CONFIRMED, createdAt);
    }

    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }

    public boolean isConfirmed() {
        return status == OrderStatus.CONFIRMED;
    }
}
