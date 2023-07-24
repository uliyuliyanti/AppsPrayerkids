/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.Expose
 *  com.google.gson.annotations.SerializedName
 *  java.lang.Object
 *  java.lang.String
 */
package com.prayerkidsstore.ongkir;


import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

public class CostsItem {

    public CostsItem() {
    }

    public CostsItem(String service, String description, ArrayList<CostItem> cost) {
        this.service = service;
        this.description = description;
        this.cost = cost;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<CostItem> getCost() {
        return cost;
    }

    public void setCost(ArrayList<CostItem> cost) {
        this.cost = cost;
    }

    @PropertyName("service")
    private String service;
    @PropertyName("description")
    private String description;
    @PropertyName("cost")
    private ArrayList<CostItem> cost = null;
}

