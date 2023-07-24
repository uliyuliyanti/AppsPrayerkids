/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.Expose
 *  com.google.gson.annotations.SerializedName
 *  java.lang.Object
 *  java.lang.String
 */
package com.prayerkidsstore.kategorihome;


import com.google.firebase.firestore.PropertyName;
import com.prayerkidsstore.Order.OrderItemProduct;

import java.util.ArrayList;

public class KategoriHomeItem {

    public KategoriHomeItem() {
    }

    public KategoriHomeItem(String img, String kategori) {
        this.img = img;
        this.kategori = kategori;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    @PropertyName("img")
    private String img;
    @PropertyName("kategori")
    private String kategori;

}

