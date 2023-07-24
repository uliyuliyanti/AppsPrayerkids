/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.Expose
 *  com.google.gson.annotations.SerializedName
 *  java.lang.Object
 *  java.lang.String
 */
package com.prayerkidsstore.AdminUserOrder;


import com.google.firebase.firestore.PropertyName;
import com.prayerkidsstore.Order.OrderItemProduct;

import java.util.ArrayList;

public class AdminUserOrderItem {

    public AdminUserOrderItem() {
    }

    public AdminUserOrderItem(String orderBadge, String userName, String keyId, String token, boolean admin) {
        OrderBadge = orderBadge;
        UserName = userName;
        this.keyId = keyId;
        this.token = token;
        Admin = admin;
    }

    public String getOrderBadge() {
        return OrderBadge;
    }

    public void setOrderBadge(String orderBadge) {
        OrderBadge = orderBadge;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAdmin() {
        return Admin;
    }

    public void setAdmin(boolean admin) {
        Admin = admin;
    }

    @PropertyName("OrderBadge")
    private String OrderBadge;
    @PropertyName("UserName")
    private String UserName;
    @PropertyName("KeyId")
    private String keyId;
    @PropertyName("token")
    private String token;
    @PropertyName("Admin")
    private boolean Admin;
}

