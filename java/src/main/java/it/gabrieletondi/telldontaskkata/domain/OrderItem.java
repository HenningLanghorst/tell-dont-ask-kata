package it.gabrieletondi.telldontaskkata.domain;

import java.math.BigDecimal;

public record OrderItem(
        Product product,
        int quantity,
        BigDecimal taxedAmount,
        BigDecimal tax
) {

}
