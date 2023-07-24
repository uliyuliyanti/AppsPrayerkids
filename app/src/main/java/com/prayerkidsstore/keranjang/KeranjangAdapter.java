/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.res.Resources
 *  android.os.Build
 *  android.os.Build$VERSION
 *  android.text.Html
 *  android.util.Log
 *  android.view.LayoutInflater
 *  android.view.View
 *  android.view.ViewGroup
 *  android.widget.ImageView
 *  android.widget.RatingBar
 *  android.widget.TextView
 *  androidx.recyclerview.widget.RecyclerView
 *  androidx.recyclerview.widget.RecyclerView$Adapter
 *  androidx.recyclerview.widget.RecyclerView$ViewHolder
 *  java.io.PrintStream
 *  java.lang.CharSequence
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.text.ParseException
 *  java.text.SimpleDateFormat
 *  java.util.ArrayList
 *  java.util.Date
 *  java.util.Locale
 */
package com.prayerkidsstore.keranjang;


import static com.prayerkidsstore.KeranjangActivity.mtotal_harga_keranjang;
import static com.prayerkidsstore.KeranjangActivity.mtotal_items_keranjang;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prayerkidsstore.KeranjangActivity;
import com.prayerkidsstore.ProductDetailsActivity;
import com.prayerkidsstore.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class KeranjangAdapter
extends RecyclerView.Adapter<KeranjangAdapter.Myviewholder> {
    boolean solved = true;
    boolean showprogress = true;
    Context context;
    ArrayList<KeranjangItem> myItem;
    int harga_total_plus = 0;
    int qty_total_plus = 0;
    double harga_total = 0.0;
    public KeranjangAdapter(Context c, ArrayList<KeranjangItem> p){
        context = c;
        myItem = p;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Myviewholder(LayoutInflater.from(context).inflate(R.layout.item_keranjang,
                viewGroup, false));


    }


    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(@NonNull Myviewholder myviewholder, @SuppressLint("RecyclerView") int i) {
        double harga_normal= Double.parseDouble(myItem.get(i).getHarga());
        String harga = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(harga_normal);
        myviewholder.mnama_produk.setText(myItem.get(i).getNamaProduct());
        myviewholder.mharga.setText(harga);
        if (Integer.parseInt(myItem.get(i).getDiskon())==0){
            //jika diskon harga == 0 maka text harga normalt dan text diskon di hide dan text harga di ubah jadi warna Hitam
            myviewholder.mdiskon_harga.setVisibility(View.GONE);
            myviewholder.mharga.setTextColor(Color.parseColor("#000000"));
        }else {
            //jika diskon harga tidak == 0 maka text harga di coret dan text diskon di show
            myviewholder.mdiskon_harga.setVisibility(View.VISIBLE);
            myviewholder.mharga.setTextColor(Color.parseColor("#919191"));
            if (! myviewholder.mharga.getPaint().isStrikeThruText()) {
                myviewholder.mharga.setPaintFlags( myviewholder.mharga.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                myviewholder.mharga.setPaintFlags( myviewholder.mharga.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
            //set Harga Diskon
            double harga_diskon= Double.parseDouble(myItem.get(i).getDiskon());
            String diskon = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(harga_diskon);
            myviewholder.mdiskon_harga.setText(diskon);
        }
        Log.d("get_dataproduct: ",myItem.get(i).getNamaProduct());
        //clik item menuju ke details product
        myviewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeranjangActivity.updateKeranjang(context,myItem.get(i).getKeyId(), myItem.get(i).getNamaProduct(), myItem.get(i).getQty(), myItem.get(i).getCatatan());
            }
        });
        //set Image
        Picasso.get()
                .load(myItem.get(i).getImg())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .into(myviewholder.mimg_product);
        //SET SIZE
        myviewholder.msize.setText("Size: "+myItem.get(i).getSize());
        //SET QTY
        myviewholder.mqty.setText("Qty: "+myItem.get(i).getQty());
        //CATATAN KERANJANG
        if (myItem.get(i).getCatatan().equals("")){

        }else {
            myviewholder.mcatatan_keranjang.setText("Catatan: "+myItem.get(i).getCatatan());
        }
        ///set Total harga ITEM
        if (myItem.get(i).getDiskon().equals("0")){
            //jika diskon 0 harga item normal di kali (*) qty
            harga_total_plus += Integer.parseInt(myItem.get(i).getHarga())*Integer.parseInt(myItem.get(i).getQty());
        }else {
            //jika diskon >0 harga item diskon di kali (*) qty
            harga_total_plus += Integer.parseInt(myItem.get(i).getDiskon())*Integer.parseInt(myItem.get(i).getQty());
        }
        harga_total= Double.parseDouble(String.valueOf(harga_total_plus));
        String harga_total_text = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(harga_total);
        mtotal_harga_keranjang.setText(harga_total_text);
        //Set total Qty Item Keranjang
        qty_total_plus += Integer.parseInt(myItem.get(i).getQty());
        mtotal_items_keranjang.setText("Total Items: "+String.valueOf(qty_total_plus)+" Pcs");
    }

    @Override
    public int getItemCount() {
        return
                myItem.size();
    }

    public static class Myviewholder extends RecyclerView.ViewHolder{
        TextView mnama_produk,mharga,mdiskon_harga,mqty,msize, mcatatan_keranjang;
        ImageView mimg_product;
        public Myviewholder(@NonNull View view) {
            super(view);
            mnama_produk = view.findViewById(R.id.nama_product_keranjang);
            mqty = view.findViewById(R.id.qty_keranjang);
            msize = view.findViewById(R.id.size_keranjang);
            mharga = view.findViewById(R.id.harga_keranjang);
            mimg_product = view.findViewById(R.id.img_keranjang);
            mdiskon_harga = view.findViewById(R.id.diskon_keranjang);
            mcatatan_keranjang = view.findViewById(R.id.catatan_keranjang);


        }
    }



}

