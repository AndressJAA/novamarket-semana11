package pe.edu.upsjb.novamarket.infrastructure.observer;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pe.edu.upsjb.novamarket.domain.event.ObserverAction;
import pe.edu.upsjb.novamarket.domain.event.ObserverTarget;
import pe.edu.upsjb.novamarket.domain.event.OrderConfirmedEvent;
import pe.edu.upsjb.novamarket.domain.event.OrderEventObserver;

@Component
@Order(3)
public class AdministrationObserver implements OrderEventObserver {

    @Override
    public String observerName() {
        return "AdministrationObserver";
    }

    @Override
    public ObserverTarget target() {
        return ObserverTarget.ADMINISTRATION;
    }

    @Override
    public ObserverAction onOrderConfirmed(OrderConfirmedEvent event) {
        return new ObserverAction(
                "Seguimiento administrativo registrado para el pedido #"
                        + event.order().id() + ".");
    }
}
