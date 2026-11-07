package pe.edu.upsjb.novamarket.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.edu.upsjb.novamarket.domain.discount.DiscountStrategy;
import pe.edu.upsjb.novamarket.domain.discount.FrequentCustomerDiscountStrategy;
import pe.edu.upsjb.novamarket.domain.discount.NewCustomerDiscountStrategy;
import pe.edu.upsjb.novamarket.domain.discount.UnidentifiedCustomerDiscountStrategy;

import java.time.Clock;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public DiscountStrategy newCustomerDiscountStrategy() {
        return new NewCustomerDiscountStrategy();
    }

    @Bean
    public DiscountStrategy frequentCustomerDiscountStrategy() {
        return new FrequentCustomerDiscountStrategy();
    }

    @Bean
    public DiscountStrategy unidentifiedCustomerDiscountStrategy() {
        return new UnidentifiedCustomerDiscountStrategy();
    }

    @Bean
    public Clock applicationClock() {
        return Clock.systemDefaultZone();
    }
}
