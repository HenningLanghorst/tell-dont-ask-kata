package it.gabrieletondi.telldontaskkata.domain;

import it.gabrieletondi.telldontaskkata.useCase.OrderCannotBeShippedException;
import it.gabrieletondi.telldontaskkata.useCase.OrderCannotBeShippedTwiceException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.*;
import static java.math.RoundingMode.HALF_UP;

public class Order {
    private BigDecimal total;
    private final String currency;
    private final List<OrderItem> items = new ArrayList<>();
    private BigDecimal tax;
    private OrderStatus status;
    private int id;

    public Order() {
        status = OrderStatus.CREATED;
        currency = "EUR";
        total = new BigDecimal("0.00");
        tax = new BigDecimal("0.00");
    }

    public Order(int id, OrderStatus status) {
        this.id = id;
        this.status = status;
        currency = "EUR";
    }

    public static Order empty() {
        return new Order();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getCurrency() {
        return currency;
    }


    public List<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public void approve() {
        if (status == OrderStatus.SHIPPED) {
            throw new ShippedOrdersCannotBeChangedException();
        }

        if (status == OrderStatus.REJECTED) {
            throw new RejectedOrderCannotBeApprovedException();
        }

        status = OrderStatus.APPROVED;
    }

    public void reject() {
        if (status == OrderStatus.SHIPPED) {
            throw new ShippedOrdersCannotBeChangedException();
        }

        if (status == OrderStatus.APPROVED) {
            throw new ApprovedOrderCannotBeRejectedException();
        }

        this.status = OrderStatus.REJECTED;
    }

    public void addItem(Product product, int quantity) {
        final BigDecimal unitaryTax = product.price().movePointLeft(2).multiply(product.category().taxPercentage()).setScale(2, HALF_UP);
        final BigDecimal unitaryTaxedAmount = product.price().add(unitaryTax).setScale(2, HALF_UP);
        final BigDecimal taxedAmount = unitaryTaxedAmount.multiply(BigDecimal.valueOf(quantity)).setScale(2, HALF_UP);
        final BigDecimal taxAmount = unitaryTax.multiply(BigDecimal.valueOf(quantity));
        final OrderItem orderItem = new OrderItem(product, quantity, taxedAmount, taxAmount);
        items.add(orderItem);

        this.total = total.add(taxedAmount);
        this.tax = tax.add(taxAmount);
    }

    public void verifyIsShippable() {
        if (getStatus().equals(CREATED) || getStatus().equals(REJECTED)) {
            throw new OrderCannotBeShippedException();
        }

        if (getStatus().equals(SHIPPED)) {
            throw new OrderCannotBeShippedTwiceException();
        }
    }

    public void markAsShipped() {
        status = SHIPPED;
    }
}
