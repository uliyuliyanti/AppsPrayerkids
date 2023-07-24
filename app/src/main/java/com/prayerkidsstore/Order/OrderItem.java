/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.Expose
 *  com.google.gson.annotations.SerializedName
 *  java.lang.Object
 *  java.lang.String
 */
package com.prayerkidsstore.Order;


import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

public class OrderItem {

    public OrderItem() {
    }

    public OrderItem(String orderDate, String orderId, String statusOrder, String totalPrice, String totalItem, ArrayList<OrderItemProduct> orderUser) {
        OrderDate = orderDate;
        OrderId = orderId;
        StatusOrder = statusOrder;
        TotalPrice = totalPrice;
        this.totalItem = totalItem;
        this.orderUser = orderUser;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getStatusOrder() {
        return StatusOrder;
    }

    public void setStatusOrder(String statusOrder) {
        StatusOrder = statusOrder;
    }

    public String getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        TotalPrice = totalPrice;
    }

    public String getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(String totalItem) {
        this.totalItem = totalItem;
    }

    public ArrayList<OrderItemProduct> getOrderUser() {
        return orderUser;
    }

    public void setOrderUser(ArrayList<OrderItemProduct> orderUser) {
        this.orderUser = orderUser;
    }

    @PropertyName("OrderDate")
    private String OrderDate;
    @PropertyName("OrderId")
    private String OrderId;
    @PropertyName("StatusOrder")
    private String StatusOrder;
    @PropertyName("TotalPrice")
    private String TotalPrice;
    @PropertyName("totalItem")
    private String totalItem;
    @PropertyName("orderUser")
    private ArrayList<OrderItemProduct> orderUser = null;
}

