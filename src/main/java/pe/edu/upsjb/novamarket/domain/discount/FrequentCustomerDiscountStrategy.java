package pe.edu.upsjb.novamarket.domain.discount;

import pe.edu.upsjb.novamarket.domain.model.ClientCategory;

import java.math.BigDecimal;

public final class FrequentCustomerDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal RATE = new BigDecimal("0.10");

    @Override
    public ClientCategory supportedCategory() {
        return ClientCategory.FREQUENT;
    }

    @Override
    public BigDecimal rate() {
        return RATE;
    }
}
