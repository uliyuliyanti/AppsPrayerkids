/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.Expose
 *  com.google.gson.annotations.SerializedName
 *  java.lang.Object
 *  java.lang.String
 */
package com.prayerkidsstore.OrderDetails;


import com.google.firebase.firestore.PropertyName;
import com.prayerkidsstore.Order.OrderItemProduct;

import java.util.ArrayList;

public class OrderDetailsItem {

    public OrderDetailsItem() {
    }

    public OrderDetailsItem(String catatan, String diskon, String harga, String img, String namaProduct, String qty, String size) {
        this.catatan = catatan;
        this.diskon = diskon;
        this.harga = harga;
        this.img = img;
        this.namaProduct = namaProduct;
        this.qty = qty;
        this.size = size;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getNamaProduct() {
        return namaProduct;
    }

    public void setNamaProduct(String namaProduct) {
        this.namaProduct = namaProduct;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @PropertyName("catatan")
    private String catatan;
    @PropertyName("diskon")
    private String diskon;
    @PropertyName("harga")
    private String harga;
    @PropertyName("img")
    private String img;
    @PropertyName("namaProduct")
    private String namaProduct;
    @PropertyName("qty")
    private String qty;
    @PropertyName("size")
    private String size;

}

