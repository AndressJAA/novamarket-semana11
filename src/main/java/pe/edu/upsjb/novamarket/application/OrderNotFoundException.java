package pe.edu.upsjb.novamarket.application;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(long orderId) {
        super("No se encontró el pedido #" + orderId + ".");
    }
}
