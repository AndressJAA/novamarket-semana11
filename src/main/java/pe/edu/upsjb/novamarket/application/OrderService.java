package pe.edu.upsjb.novamarket.application;

import org.springframework.stereotype.Service;
import pe.edu.upsjb.novamarket.domain.model.Customer;
import pe.edu.upsjb.novamarket.domain.model.DiscountResult;
import pe.edu.upsjb.novamarket.domain.model.Order;
import pe.edu.upsjb.novamarket.domain.repository.OrderRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final DiscountCalculator discountCalculator;
    private final Clock clock;

    public OrderService(OrderRepository orderRepository,
                        DiscountCalculator discountCalculator,
                        Clock clock) {
        this.orderRepository = orderRepository;
        this.discountCalculator = discountCalculator;
        this.clock = clock;
    }

    public Order register(RegisterOrderCommand command) {
        Objects.requireNonNull(command, "Los datos del pedido son obligatorios");

        Customer customer = new Customer(
                command.customerName(),
                command.customerDocument(),
                command.clientCategory());
        DiscountResult discount = discountCalculator.calculate(
                command.clientCategory(), command.amount());
        Order order = Order.register(
                customer, command.amount(), discount, LocalDateTime.now(clock));
        return orderRepository.save(order);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(long orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> findRecent(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("El límite no puede ser negativo");
        }
        return orderRepository.findAll().stream().limit(limit).toList();
    }

    public DashboardSummary getDashboardSummary() {
        return DashboardSummary.from(orderRepository.findAll());
    }
}
