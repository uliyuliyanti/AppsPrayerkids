/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.Expose
 *  com.google.gson.annotations.SerializedName
 *  java.lang.Object
 *  java.lang.String
 */
package com.prayerkidsstore.AdminApproveOrder;


import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

public class AdminApproveItem {

    public AdminApproveItem() {
    }

    public AdminApproveItem(String orderDate, String orderId, String statusOrder, String totalPrice, String totalItem, String keyId, ArrayList<AdminApproveItemProduct> orderUser) {
        OrderDate = orderDate;
        OrderId = orderId;
        StatusOrder = statusOrder;
        TotalPrice = totalPrice;
        this.totalItem = totalItem;
        this.keyId = keyId;
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

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public ArrayList<AdminApproveItemProduct> getOrderUser() {
        return orderUser;
    }

    public void setOrderUser(ArrayList<AdminApproveItemProduct> orderUser) {
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
    @PropertyName("keyId")
    private String keyId;
    @PropertyName("orderUser")
    private ArrayList<AdminApproveItemProduct> orderUser = null;
}

