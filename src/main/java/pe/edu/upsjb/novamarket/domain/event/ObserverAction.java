package pe.edu.upsjb.novamarket.domain.event;

public record ObserverAction(String message) {

    public ObserverAction {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("La acción del observador debe describir su resultado");
        }
        message = message.trim();
    }
}
