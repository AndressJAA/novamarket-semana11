package pe.edu.upsjb.novamarket.domain.model;

import java.util.Objects;

public record Customer(String name, String document, ClientCategory category) {

    public Customer {
        name = requireText(name, "El nombre del cliente es obligatorio");
        document = requireText(document, "El documento del cliente es obligatorio");
        category = Objects.requireNonNull(category, "La categoría del cliente es obligatoria");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
