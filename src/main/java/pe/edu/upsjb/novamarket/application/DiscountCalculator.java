package pe.edu.upsjb.novamarket.application;

import org.springframework.stereotype.Service;
import pe.edu.upsjb.novamarket.domain.discount.DiscountStrategy;
import pe.edu.upsjb.novamarket.domain.model.ClientCategory;
import pe.edu.upsjb.novamarket.domain.model.DiscountResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DiscountCalculator {

    private final Map<ClientCategory, DiscountStrategy> strategies;

    public DiscountCalculator(List<DiscountStrategy> registeredStrategies) {
        if (registeredStrategies == null || registeredStrategies.isEmpty()) {
            throw new IllegalArgumentException("Debe registrarse al menos una estrategia de descuento");
        }

        Map<ClientCategory, DiscountStrategy> strategyMap = new EnumMap<>(ClientCategory.class);
        for (DiscountStrategy strategy : registeredStrategies) {
            Objects.requireNonNull(strategy, "Una estrategia registrada no puede ser nula");
            DiscountStrategy duplicate = strategyMap.putIfAbsent(strategy.supportedCategory(), strategy);
            if (duplicate != null) {
                throw new IllegalStateException(
                        "Existe más de una estrategia para " + strategy.supportedCategory());
            }
        }
        this.strategies = Collections.unmodifiableMap(strategyMap);
    }

    public DiscountResult calculate(ClientCategory category, BigDecimal amount) {
        DiscountStrategy strategy = strategies.get(category);
        if (strategy == null) {
            throw new IllegalArgumentException("No existe una estrategia registrada para " + category);
        }
        return strategy.calculate(amount);
    }

    public Map<ClientCategory, BigDecimal> availableRates() {
        Map<ClientCategory, BigDecimal> rates = new EnumMap<>(ClientCategory.class);
        strategies.forEach((category, strategy) -> rates.put(category, strategy.rate()));
        return Collections.unmodifiableMap(rates);
    }
}
