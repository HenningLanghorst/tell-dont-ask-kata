package it.gabrieletondi.telldontaskkata.domain;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;

public record OrderItem(Product product, int quantity, BigDecimal taxedAmount, BigDecimal tax) {

    public static OrderItem from(Product product, int quantity) {
        final BigDecimal unitaryTax = product.getPrice().divide(valueOf(100)).multiply(product.getCategory().taxPercentage()).setScale(2, HALF_UP);
        final BigDecimal unitaryTaxedAmount = product.getPrice().add(unitaryTax).setScale(2, HALF_UP);
        final BigDecimal taxedAmount = unitaryTaxedAmount.multiply(BigDecimal.valueOf(quantity)).setScale(2, HALF_UP);
        final BigDecimal taxAmount = unitaryTax.multiply(BigDecimal.valueOf(quantity));
        return new OrderItem(product, quantity, taxedAmount, taxAmount);
    }

}
