package pe.edu.upsjb.novamarket.domain.event;

public enum ObserverTarget {
    CUSTOMER("Cliente"),
    WAREHOUSE("Almacén"),
    ADMINISTRATION("Administración");

    private final String displayName;

    ObserverTarget(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
