package pe.edu.upsjb.novamarket.application;

import org.springframework.stereotype.Service;
import pe.edu.upsjb.novamarket.domain.event.ObserverAction;
import pe.edu.upsjb.novamarket.domain.event.ObserverProcessingResult;
import pe.edu.upsjb.novamarket.domain.event.ObserverProcessingStatus;
import pe.edu.upsjb.novamarket.domain.event.OrderConfirmedEvent;
import pe.edu.upsjb.novamarket.domain.event.OrderEventObserver;
import pe.edu.upsjb.novamarket.domain.repository.EventHistoryRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderEventPublisher {

    private final List<OrderEventObserver> observers;
    private final EventHistoryRepository eventHistoryRepository;
    private final Clock clock;

    public OrderEventPublisher(List<OrderEventObserver> observers,
                               EventHistoryRepository eventHistoryRepository,
                               Clock clock) {
        this.observers = List.copyOf(observers);
        this.eventHistoryRepository = eventHistoryRepository;
        this.clock = clock;
    }

    public List<ObserverProcessingResult> publish(OrderConfirmedEvent event) {
        eventHistoryRepository.saveEvent(event);
        List<ObserverProcessingResult> results = new ArrayList<>();

        for (OrderEventObserver observer : observers) {
            ObserverProcessingResult result = processObserver(observer, event);
            eventHistoryRepository.saveProcessingResult(result);
            results.add(result);
        }

        return List.copyOf(results);
    }

    private ObserverProcessingResult processObserver(
            OrderEventObserver observer, OrderConfirmedEvent event) {
        ObserverProcessingStatus status;
        String message;

        try {
            ObserverAction action = observer.onOrderConfirmed(event);
            if (action == null) {
                throw new IllegalStateException("El observador no devolvió una acción");
            }
            status = ObserverProcessingStatus.SUCCESS;
            message = action.message();
        } catch (Exception exception) {
            status = ObserverProcessingStatus.FAILED;
            String reason = exception.getMessage();
            message = "El observador no pudo completar la acción: "
                    + (reason == null || reason.isBlank()
                    ? exception.getClass().getSimpleName()
                    : reason);
        }

        return new ObserverProcessingResult(
                UUID.randomUUID(),
                event.eventId(),
                observer.observerName(),
                observer.target(),
                status,
                message,
                LocalDateTime.now(clock));
    }
}
