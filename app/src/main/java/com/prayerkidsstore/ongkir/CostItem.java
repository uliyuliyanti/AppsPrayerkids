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

public class CostItem {

    public CostItem() {
    }


    public CostItem(int value, String etd, String note) {
        this.value = value;
        this.etd = etd;
        this.note = note;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getEtd() {
        return etd;
    }

    public void setEtd(String etd) {
        this.etd = etd;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @PropertyName("value")
    private int value;
    @PropertyName("etd")
    private String etd;
    @PropertyName("note")
    private String note;
}

