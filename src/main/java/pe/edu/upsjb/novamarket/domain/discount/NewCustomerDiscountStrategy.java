package pe.edu.upsjb.novamarket.domain.discount;

import pe.edu.upsjb.novamarket.domain.model.ClientCategory;

import java.math.BigDecimal;

public final class NewCustomerDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal RATE = new BigDecimal("0.05");

    @Override
    public ClientCategory supportedCategory() {
        return ClientCategory.NEW;
    }

    @Override
    public BigDecimal rate() {
        return RATE;
    }
}
