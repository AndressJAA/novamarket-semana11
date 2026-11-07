package pe.edu.upsjb.novamarket.domain.event;

public enum ObserverProcessingStatus {
    SUCCESS("Procesado"),
    FAILED("Fallido");

    private final String displayName;

    ObserverProcessingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
