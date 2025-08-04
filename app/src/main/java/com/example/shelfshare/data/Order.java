package com.example.shelfshare.data;

import java.util.Date;
import java.util.List;

public class Order {
    private String orderId;
    private Date date;
    private double totalAmount;
    private String status;
    private List<BookEntity> items;

    public Order(String orderId, Date date, double totalAmount, String status, List<BookEntity> items) {
        this.orderId = orderId;
        this.date = date;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public Date getDate() {
        return date;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public List<BookEntity> getItems() {
        return items;
    }

    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }
} 