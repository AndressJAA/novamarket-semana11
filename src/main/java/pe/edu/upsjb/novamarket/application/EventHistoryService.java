package pe.edu.upsjb.novamarket.application;

import org.springframework.stereotype.Service;
import pe.edu.upsjb.novamarket.domain.event.ObserverProcessingResult;
import pe.edu.upsjb.novamarket.domain.event.ObserverProcessingStatus;
import pe.edu.upsjb.novamarket.domain.event.OrderConfirmedEvent;
import pe.edu.upsjb.novamarket.domain.repository.EventHistoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EventHistoryService {

    private final EventHistoryRepository eventHistoryRepository;

    public EventHistoryService(EventHistoryRepository eventHistoryRepository) {
        this.eventHistoryRepository = eventHistoryRepository;
    }

    public List<EventHistoryEntry> findAll() {
        return eventHistoryRepository.findAllEvents().stream()
                .map(this::toEntry)
                .toList();
    }

    public Optional<EventHistoryEntry> findByOrderId(long orderId) {
        return eventHistoryRepository.findEventByOrderId(orderId).map(this::toEntry);
    }

    public EventHistorySummary getSummary() {
        List<OrderConfirmedEvent> events = eventHistoryRepository.findAllEvents();
        List<ObserverProcessingResult> results =
                eventHistoryRepository.findAllProcessingResults();
        long successful = results.stream()
                .filter(result -> result.status() == ObserverProcessingStatus.SUCCESS)
                .count();
        return new EventHistorySummary(
                events.size(),
                results.size(),
                successful,
                results.size() - successful);
    }

    private EventHistoryEntry toEntry(OrderConfirmedEvent event) {
        return new EventHistoryEntry(
                event,
                eventHistoryRepository.findResultsByEventId(event.eventId()));
    }
}
