package pe.edu.upsjb.novamarket.domain.discount;

import pe.edu.upsjb.novamarket.domain.model.ClientCategory;

import java.math.BigDecimal;

public final class UnidentifiedCustomerDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal RATE = BigDecimal.ZERO;

    @Override
    public ClientCategory supportedCategory() {
        return ClientCategory.UNIDENTIFIED;
    }

    @Override
    public BigDecimal rate() {
        return RATE;
    }
}
