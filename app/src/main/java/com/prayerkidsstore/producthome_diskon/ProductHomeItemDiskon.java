/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.Expose
 *  com.google.gson.annotations.SerializedName
 *  java.lang.Object
 *  java.lang.String
 */
package com.prayerkidsstore.producthome_diskon;


import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class ProductHomeItemDiskon {

    public ProductHomeItemDiskon() {
    }
    public ProductHomeItemDiskon(String productId, String harga, String kategori, String namaProduk, String diskon, String terjual, String keyId, List<String> img) {
        this.productId = productId;
        Harga = harga;
        Kategori = kategori;
        NamaProduk = namaProduk;
        this.diskon = diskon;
        this.terjual = terjual;
        this.keyId = keyId;
        Img = img;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getHarga() {
        return Harga;
    }

    public void setHarga(String harga) {
        Harga = harga;
    }

    public String getKategori() {
        return Kategori;
    }

    public void setKategori(String kategori) {
        Kategori = kategori;
    }

    public String getNamaProduk() {
        return NamaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        NamaProduk = namaProduk;
    }

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    public String getTerjual() {
        return terjual;
    }

    public void setTerjual(String terjual) {
        this.terjual = terjual;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public List<String> getImg() {
        return Img;
    }

    public void setImg(List<String> img) {
        Img = img;
    }

    @PropertyName("productId")
    private String productId;

    @PropertyName("Harga")
    private String Harga;

    @PropertyName("Kategori")
    private String Kategori;


    @PropertyName("NamaProduk")
    private String NamaProduk;

    @PropertyName("diskon")
    private String diskon;
    @PropertyName("terjual")
    private String terjual;
    @PropertyName("keyId")
    private String keyId;
    @PropertyName("Img")
    private List<String> Img=null;
}

