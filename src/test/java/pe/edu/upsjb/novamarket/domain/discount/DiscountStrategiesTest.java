package pe.edu.upsjb.novamarket.domain.discount;

import org.junit.jupiter.api.Test;
import pe.edu.upsjb.novamarket.domain.model.DiscountResult;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountStrategiesTest {

    private static final BigDecimal TWO_HUNDRED = new BigDecimal("200.00");

    @Test
    void newCustomerReceivesFivePercentDiscount() {
        DiscountResult result = new NewCustomerDiscountStrategy().calculate(TWO_HUNDRED);

        assertEquals(new BigDecimal("0.05"), result.rate());
        assertEquals(new BigDecimal("10.00"), result.discountAmount());
        assertEquals(new BigDecimal("190.00"), result.finalAmount());
        assertEquals("5", result.percentage().toPlainString());
    }

    @Test
    void frequentCustomerReceivesTenPercentDiscount() {
        DiscountResult result = new FrequentCustomerDiscountStrategy().calculate(TWO_HUNDRED);

        assertEquals(new BigDecimal("0.1"), result.rate());
        assertEquals(new BigDecimal("20.00"), result.discountAmount());
        assertEquals(new BigDecimal("180.00"), result.finalAmount());
        assertEquals("10", result.percentage().toPlainString());
    }

    @Test
    void unidentifiedCustomerReceivesNoDiscount() {
        DiscountResult result = new UnidentifiedCustomerDiscountStrategy().calculate(TWO_HUNDRED);

        assertEquals(BigDecimal.ZERO, result.rate());
        assertEquals(new BigDecimal("0.00"), result.discountAmount());
        assertEquals(new BigDecimal("200.00"), result.finalAmount());
        assertEquals("0", result.percentage().toPlainString());
    }

    @Test
    void monetaryResultUsesTwoDecimalsAndHalfUpRounding() {
        DiscountResult result = new NewCustomerDiscountStrategy()
                .calculate(new BigDecimal("10.10"));

        assertEquals(new BigDecimal("0.51"), result.discountAmount());
        assertEquals(new BigDecimal("9.59"), result.finalAmount());
    }
}
