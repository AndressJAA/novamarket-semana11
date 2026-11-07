package pe.edu.upsjb.novamarket.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record DiscountResult(
        BigDecimal rate,
        BigDecimal discountAmount,
        BigDecimal finalAmount) {

    private static final int MONEY_SCALE = 2;

    public DiscountResult {
        if (rate == null || rate.signum() < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("La tasa debe estar entre 0 y 1");
        }
        if (discountAmount == null || finalAmount == null) {
            throw new IllegalArgumentException("Los importes del descuento son obligatorios");
        }
        rate = rate.stripTrailingZeros();
        discountAmount = discountAmount.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        finalAmount = finalAmount.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public static DiscountResult calculate(BigDecimal amount, BigDecimal rate) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("El importe debe ser mayor que cero");
        }
        BigDecimal normalizedAmount = amount.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal discount = normalizedAmount.multiply(rate)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal total = normalizedAmount.subtract(discount)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        return new DiscountResult(rate, discount, total);
    }

    public BigDecimal percentage() {
        BigDecimal normalizedPercentage = rate.movePointRight(2).stripTrailingZeros();
        return normalizedPercentage.scale() < 0
                ? normalizedPercentage.setScale(0)
                : normalizedPercentage;
    }
}
