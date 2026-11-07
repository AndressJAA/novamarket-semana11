package pe.edu.upsjb.novamarket.infrastructure.observer;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pe.edu.upsjb.novamarket.domain.event.ObserverAction;
import pe.edu.upsjb.novamarket.domain.event.ObserverTarget;
import pe.edu.upsjb.novamarket.domain.event.OrderConfirmedEvent;
import pe.edu.upsjb.novamarket.domain.event.OrderEventObserver;

@Component
@Order(1)
public class CustomerNotificationObserver implements OrderEventObserver {

    @Override
    public String observerName() {
        return "CustomerNotificationObserver";
    }

    @Override
    public ObserverTarget target() {
        return ObserverTarget.CUSTOMER;
    }

    @Override
    public ObserverAction onOrderConfirmed(OrderConfirmedEvent event) {
        return new ObserverAction(
                "Confirmación registrada para " + event.order().customer().name()
                        + " por el pedido #" + event.order().id() + ".");
    }
}
