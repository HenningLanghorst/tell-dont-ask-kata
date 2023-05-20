package it.gabrieletondi.telldontaskkata.domain;

import java.math.BigDecimal;

public record Product(
        String name,
        BigDecimal price,
        Category category
) {

}
