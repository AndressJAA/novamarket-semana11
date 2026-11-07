package pe.edu.upsjb.novamarket.domain.model;

public enum ClientCategory {
    NEW("Cliente nuevo"),
    FREQUENT("Cliente frecuente"),
    UNIDENTIFIED("Cliente no identificado");

    private final String displayName;

    ClientCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
