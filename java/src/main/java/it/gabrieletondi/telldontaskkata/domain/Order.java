package it.gabrieletondi.telldontaskkata.domain;

import it.gabrieletondi.telldontaskkata.useCase.OrderCannotBeShippedException;
import it.gabrieletondi.telldontaskkata.useCase.OrderCannotBeShippedTwiceException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.*;

public class Order {
    private BigDecimal total;
    private final String currency;
    private final List<OrderItem> items;
    private BigDecimal tax;
    private OrderStatus status;
    private int id;

    public Order() {
        status = OrderStatus.CREATED;
        items = new ArrayList<>();
        currency = "EUR";
        total = new BigDecimal("0.00");
        tax = new BigDecimal("0.00");
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

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        final OrderItem orderItem = OrderItem.from(product, quantity);
        items.add(orderItem);

        this.total = total.add(orderItem.getTaxedAmount());
        this.tax = tax.add(orderItem.getTax());
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
