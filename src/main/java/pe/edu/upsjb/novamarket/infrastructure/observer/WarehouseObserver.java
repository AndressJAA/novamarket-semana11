package pe.edu.upsjb.novamarket.infrastructure.observer;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pe.edu.upsjb.novamarket.domain.event.ObserverAction;
import pe.edu.upsjb.novamarket.domain.event.ObserverTarget;
import pe.edu.upsjb.novamarket.domain.event.OrderConfirmedEvent;
import pe.edu.upsjb.novamarket.domain.event.OrderEventObserver;

@Component
@Order(2)
public class WarehouseObserver implements OrderEventObserver {

    @Override
    public String observerName() {
        return "WarehouseObserver";
    }

    @Override
    public ObserverTarget target() {
        return ObserverTarget.WAREHOUSE;
    }

    @Override
    public ObserverAction onOrderConfirmed(OrderConfirmedEvent event) {
        return new ObserverAction(
                "Solicitud de preparación registrada para el pedido #"
                        + event.order().id() + ".");
    }
}
