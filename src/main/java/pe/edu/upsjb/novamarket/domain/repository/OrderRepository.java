package pe.edu.upsjb.novamarket.domain.repository;

import pe.edu.upsjb.novamarket.domain.model.Order;
import pe.edu.upsjb.novamarket.domain.model.OrderConfirmationTransition;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    List<Order> findAll();

    Optional<Order> findById(long id);

    Optional<OrderConfirmationTransition> confirmPending(long id);

    long count();
}
