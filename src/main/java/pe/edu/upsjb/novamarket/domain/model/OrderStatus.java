package pe.edu.upsjb.novamarket.domain.model;

public enum OrderStatus {
    PENDING("Pendiente"),
    CONFIRMED("Confirmado");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
