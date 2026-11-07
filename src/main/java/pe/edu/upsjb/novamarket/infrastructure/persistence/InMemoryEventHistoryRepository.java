package pe.edu.upsjb.novamarket.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import pe.edu.upsjb.novamarket.domain.event.ObserverProcessingResult;
import pe.edu.upsjb.novamarket.domain.event.OrderConfirmedEvent;
import pe.edu.upsjb.novamarket.domain.repository.EventHistoryRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryEventHistoryRepository implements EventHistoryRepository {

    private final Map<UUID, OrderConfirmedEvent> events = new ConcurrentHashMap<>();
    private final Map<Long, UUID> eventIdsByOrder = new ConcurrentHashMap<>();
    private final Map<UUID, ObserverProcessingResult> processingResults = new ConcurrentHashMap<>();

    @Override
    public void saveEvent(OrderConfirmedEvent event) {
        UUID existingEventId = eventIdsByOrder.putIfAbsent(event.order().id(), event.eventId());
        if (existingEventId != null && !existingEventId.equals(event.eventId())) {
            throw new IllegalStateException(
                    "El pedido #" + event.order().id() + " ya tiene un evento de confirmación");
        }
        events.putIfAbsent(event.eventId(), event);
    }

    @Override
    public void saveProcessingResult(ObserverProcessingResult result) {
        if (!events.containsKey(result.eventId())) {
            throw new IllegalArgumentException("No existe el evento asociado al procesamiento");
        }
        processingResults.putIfAbsent(result.processingId(), result);
    }

    @Override
    public List<OrderConfirmedEvent> findAllEvents() {
        return events.values().stream()
                .sorted(Comparator.comparing(OrderConfirmedEvent::occurredAt)
                        .thenComparing(event -> event.eventId().toString())
                        .reversed())
                .toList();
    }

    @Override
    public Optional<OrderConfirmedEvent> findEventByOrderId(long orderId) {
        UUID eventId = eventIdsByOrder.get(orderId);
        return eventId == null ? Optional.empty() : Optional.ofNullable(events.get(eventId));
    }

    @Override
    public List<ObserverProcessingResult> findResultsByEventId(UUID eventId) {
        return processingResults.values().stream()
                .filter(result -> result.eventId().equals(eventId))
                .sorted(Comparator.comparing(ObserverProcessingResult::processedAt)
                        .thenComparing(result -> result.target().ordinal()))
                .toList();
    }

    @Override
    public List<ObserverProcessingResult> findAllProcessingResults() {
        return processingResults.values().stream()
                .sorted(Comparator.comparing(ObserverProcessingResult::processedAt).reversed())
                .toList();
    }
}
