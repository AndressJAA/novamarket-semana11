package pe.edu.upsjb.novamarket.application;

import pe.edu.upsjb.novamarket.domain.event.ObserverProcessingResult;
import pe.edu.upsjb.novamarket.domain.event.OrderConfirmedEvent;

import java.util.List;

public record EventHistoryEntry(
        OrderConfirmedEvent event,
        List<ObserverProcessingResult> processingResults) {

    public EventHistoryEntry {
        processingResults = List.copyOf(processingResults);
    }
}
