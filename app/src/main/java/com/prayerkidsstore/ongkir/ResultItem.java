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

public class ResultItem {

    public ResultItem() {
    }

    public ResultItem(String code, String name, ArrayList<CostsItem> costs) {
        this.code = code;
        this.name = name;
        this.costs = costs;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<CostsItem> getCosts() {
        return costs;
    }

    public void setCosts(ArrayList<CostsItem> costs) {
        this.costs = costs;
    }

    @PropertyName("code")
    private String code;
    @PropertyName("name")
    private String name;
    @PropertyName("costs")
    private ArrayList<CostsItem> costs = null;
}

