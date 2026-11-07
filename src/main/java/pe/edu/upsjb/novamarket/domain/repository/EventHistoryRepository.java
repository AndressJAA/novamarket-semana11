package pe.edu.upsjb.novamarket.domain.repository;

import pe.edu.upsjb.novamarket.domain.event.ObserverProcessingResult;
import pe.edu.upsjb.novamarket.domain.event.OrderConfirmedEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventHistoryRepository {

    void saveEvent(OrderConfirmedEvent event);

    void saveProcessingResult(ObserverProcessingResult result);

    List<OrderConfirmedEvent> findAllEvents();

    Optional<OrderConfirmedEvent> findEventByOrderId(long orderId);

    List<ObserverProcessingResult> findResultsByEventId(UUID eventId);

    List<ObserverProcessingResult> findAllProcessingResults();
}
