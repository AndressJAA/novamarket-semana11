package pe.edu.upsjb.novamarket.domain.discount;

import pe.edu.upsjb.novamarket.domain.model.ClientCategory;
import pe.edu.upsjb.novamarket.domain.model.DiscountResult;

import java.math.BigDecimal;

public interface DiscountStrategy {

    ClientCategory supportedCategory();

    BigDecimal rate();

    default DiscountResult calculate(BigDecimal amount) {
        return DiscountResult.calculate(amount, rate());
    }
}
