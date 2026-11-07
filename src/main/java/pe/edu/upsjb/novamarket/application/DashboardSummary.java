package pe.edu.upsjb.novamarket.application;

import pe.edu.upsjb.novamarket.domain.model.Order;
import pe.edu.upsjb.novamarket.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record DashboardSummary(
        long orderCount,
        long pendingOrderCount,
        long confirmedOrderCount,
        BigDecimal grossAmount,
        BigDecimal discountAmount,
        BigDecimal netAmount) {

    public static DashboardSummary from(List<Order> orders) {
        BigDecimal gross = BigDecimal.ZERO;
        BigDecimal discounts = BigDecimal.ZERO;
        BigDecimal net = BigDecimal.ZERO;

        for (Order order : orders) {
            gross = gross.add(order.originalAmount());
            discounts = discounts.add(order.discount().discountAmount());
            net = net.add(order.discount().finalAmount());
        }

        long pending = orders.stream()
                .filter(order -> order.status() == OrderStatus.PENDING)
                .count();
        long confirmed = orders.stream()
                .filter(order -> order.status() == OrderStatus.CONFIRMED)
                .count();

        return new DashboardSummary(
                orders.size(), pending, confirmed,
                money(gross), money(discounts), money(net));
    }

    private static BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
