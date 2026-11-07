package pe.edu.upsjb.novamarket.application;

import org.junit.jupiter.api.Test;
import pe.edu.upsjb.novamarket.domain.discount.DiscountStrategy;
import pe.edu.upsjb.novamarket.domain.discount.FrequentCustomerDiscountStrategy;
import pe.edu.upsjb.novamarket.domain.discount.NewCustomerDiscountStrategy;
import pe.edu.upsjb.novamarket.domain.discount.UnidentifiedCustomerDiscountStrategy;
import pe.edu.upsjb.novamarket.domain.model.ClientCategory;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiscountCalculatorTest {

    @Test
    void selectsTheRegisteredStrategyByClientCategory() {
        DiscountCalculator calculator = calculator();

        assertEquals(
                new BigDecimal("20.00"),
                calculator.calculate(ClientCategory.FREQUENT, new BigDecimal("200.00"))
                        .discountAmount());
    }

    @Test
    void rejectsDuplicateStrategiesForTheSameCategory() {
        List<DiscountStrategy> duplicateStrategies = List.of(
                new NewCustomerDiscountStrategy(),
                new NewCustomerDiscountStrategy());

        assertThrows(IllegalStateException.class,
                () -> new DiscountCalculator(duplicateStrategies));
    }

    private DiscountCalculator calculator() {
        return new DiscountCalculator(List.of(
                new NewCustomerDiscountStrategy(),
                new FrequentCustomerDiscountStrategy(),
                new UnidentifiedCustomerDiscountStrategy()));
    }
}
