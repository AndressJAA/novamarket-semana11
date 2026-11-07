package pe.edu.upsjb.novamarket.presentation.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pe.edu.upsjb.novamarket.application.RegisterOrderCommand;
import pe.edu.upsjb.novamarket.domain.model.ClientCategory;

import java.math.BigDecimal;

public class OrderForm {

    @NotBlank(message = "Ingresa el nombre del cliente.")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
    private String customerName;

    @NotBlank(message = "Ingresa el documento del cliente.")
    @Pattern(
            regexp = "[A-Za-z0-9-]{8,15}",
            message = "Usa entre 8 y 15 letras, números o guiones.")
    private String customerDocument;

    @NotNull(message = "Selecciona una categoría de cliente.")
    private ClientCategory clientCategory = ClientCategory.NEW;

    @NotNull(message = "Ingresa el importe del pedido.")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor que S/ 0.00.")
    @Digits(integer = 10, fraction = 2, message = "Usa como máximo 10 enteros y 2 decimales.")
    private BigDecimal amount;

    public RegisterOrderCommand toCommand() {
        return new RegisterOrderCommand(
                customerName, customerDocument, clientCategory, amount);
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerDocument() {
        return customerDocument;
    }

    public void setCustomerDocument(String customerDocument) {
        this.customerDocument = customerDocument;
    }

    public ClientCategory getClientCategory() {
        return clientCategory;
    }

    public void setClientCategory(ClientCategory clientCategory) {
        this.clientCategory = clientCategory;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
