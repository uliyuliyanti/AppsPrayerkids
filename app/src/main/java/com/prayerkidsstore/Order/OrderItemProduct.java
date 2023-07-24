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

public class OrderItemProduct {

    public OrderItemProduct() {
    }

    public OrderItemProduct(String productId, String harga, String kategori, String namaProduct, String diskon, String qty, String keyId, String img, String size, String catatan) {
        this.productId = productId;
        Harga = harga;
        Kategori = kategori;
        NamaProduct = namaProduct;
        this.diskon = diskon;
        Qty = qty;
        this.keyId = keyId;
        Img = img;
        this.size = size;
        Catatan = catatan;
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

    public String getNamaProduct() {
        return NamaProduct;
    }

    public void setNamaProduct(String namaProduct) {
        NamaProduct = namaProduct;
    }

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCatatan() {
        return Catatan;
    }

    public void setCatatan(String catatan) {
        Catatan = catatan;
    }

    @PropertyName("productId")
    private String productId;

    @PropertyName("Harga")
    private String Harga;

    @PropertyName("Kategori")
    private String Kategori;


    @PropertyName("NamaProduct")
    private String NamaProduct;

    @PropertyName("Diskon")
    private String diskon;
    @PropertyName("Qty")
    private String Qty;
    @PropertyName("keyId")
    private String keyId;
    @PropertyName("Img")
    private String Img;
    @PropertyName("size")
    private String size;
    @PropertyName("Catatan")
    private String Catatan;
}

