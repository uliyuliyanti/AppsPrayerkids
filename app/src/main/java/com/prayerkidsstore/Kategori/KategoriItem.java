/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.Expose
 *  com.google.gson.annotations.SerializedName
 *  java.lang.Object
 *  java.lang.String
 */
package com.prayerkidsstore.Kategori;


import com.google.firebase.firestore.PropertyName;

public class KategoriItem {

    public KategoriItem() {
    }

    public KategoriItem(String kategori, String keyId) {
        this.kategori = kategori;
        this.keyId = keyId;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    @PropertyName("kategori")
    private String kategori;
    @PropertyName("keyId")
    private String keyId;
}

