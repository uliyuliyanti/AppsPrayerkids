package com.prayerkidsstore.AdminProductAdd;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class ItemModel {

    // we need empty constructor
    public ItemModel() {
    }

    public ItemModel(String itemName, String itemDesc, String itemId, ArrayList<String> imageUrls, String productId, String harga, String kategori, String namaProduk, String diskon, String terjual,  List<String> img, List<String> size) {

        this.productId = productId;
        Harga = harga;
        Kategori = kategori;
        NamaProduk = namaProduk;
        this.diskon = diskon;
        this.terjual = terjual;
        Img = img;
        this.size = size;
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


    public List<String> getImg() {
        return Img;
    }

    public void setImg(List<String> img) {
        Img = img;
    }

    public List<String> getSize() {
        return size;
    }

    public void setSize(List<String> size) {
        this.size = size;
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
    @PropertyName("Img")
    private List<String> Img=null;
    @PropertyName("size")
    private List<String> size=null;
}
