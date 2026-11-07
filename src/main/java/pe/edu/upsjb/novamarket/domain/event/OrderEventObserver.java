package pe.edu.upsjb.novamarket.domain.event;

public interface OrderEventObserver {

    String observerName();

    ObserverTarget target();

    ObserverAction onOrderConfirmed(OrderConfirmedEvent event);
}
