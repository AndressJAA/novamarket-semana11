package pe.edu.upsjb.novamarket.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record ObserverProcessingResult(
        UUID processingId,
        UUID eventId,
        String observerName,
        ObserverTarget target,
        ObserverProcessingStatus status,
        String message,
        LocalDateTime processedAt) {

    public ObserverProcessingResult {
        processingId = Objects.requireNonNull(processingId, "El identificador del procesamiento es obligatorio");
        eventId = Objects.requireNonNull(eventId, "El identificador del evento es obligatorio");
        observerName = requireText(observerName, "El nombre del observador es obligatorio");
        target = Objects.requireNonNull(target, "El destinatario del observador es obligatorio");
        status = Objects.requireNonNull(status, "El estado del procesamiento es obligatorio");
        message = requireText(message, "El detalle del procesamiento es obligatorio");
        processedAt = Objects.requireNonNull(processedAt, "La fecha del procesamiento es obligatoria");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
