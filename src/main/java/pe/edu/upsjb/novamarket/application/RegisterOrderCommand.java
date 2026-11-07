package pe.edu.upsjb.novamarket.application;

import pe.edu.upsjb.novamarket.domain.model.ClientCategory;

import java.math.BigDecimal;

public record RegisterOrderCommand(
        String customerName,
        String customerDocument,
        ClientCategory clientCategory,
        BigDecimal amount) {
}
