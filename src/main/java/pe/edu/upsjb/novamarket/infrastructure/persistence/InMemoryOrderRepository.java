package pe.edu.upsjb.novamarket.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import pe.edu.upsjb.novamarket.domain.model.Order;
import pe.edu.upsjb.novamarket.domain.model.OrderConfirmationTransition;
import pe.edu.upsjb.novamarket.domain.repository.OrderRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final AtomicLong sequence = new AtomicLong();
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        long id = order.id() == null ? sequence.incrementAndGet() : order.id();
        Order storedOrder = order.withId(id);
        orders.put(id, storedOrder);
        return storedOrder;
    }

    @Override
    public List<Order> findAll() {
        return orders.values().stream()
                .sorted(Comparator.comparing(Order::createdAt)
                        .thenComparing(Order::id)
                        .reversed())
                .toList();
    }

    @Override
    public Optional<Order> findById(long id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public Optional<OrderConfirmationTransition> confirmPending(long id) {
        AtomicReference<OrderConfirmationTransition> transition = new AtomicReference<>();
        orders.computeIfPresent(id, (key, current) -> {
            if (current.isConfirmed()) {
                transition.set(new OrderConfirmationTransition(current, false));
                return current;
            }

            Order confirmed = current.confirm();
            transition.set(new OrderConfirmationTransition(confirmed, true));
            return confirmed;
        });
        return Optional.ofNullable(transition.get());
    }

    @Override
    public long count() {
        return orders.size();
    }
}
