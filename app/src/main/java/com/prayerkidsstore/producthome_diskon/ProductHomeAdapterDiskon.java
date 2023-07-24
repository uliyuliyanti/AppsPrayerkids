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
package com.prayerkidsstore.producthome_diskon;


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

import com.prayerkidsstore.ProductDetailsActivity;
import com.prayerkidsstore.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ProductHomeAdapterDiskon
extends RecyclerView.Adapter<ProductHomeAdapterDiskon.Myviewholder> {
    boolean solved = true;
    boolean showprogress = true;
    Context context;
    ArrayList<ProductHomeItemDiskon> myItem;
    int limit=10;
//    ArrayList<ProductItem> addPolistitem;
    public ProductHomeAdapterDiskon(Context c, ArrayList<ProductHomeItemDiskon> p){
        context = c;
        myItem = p;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Myviewholder(LayoutInflater.from(context).inflate(R.layout.item_product2,
                viewGroup, false));


    }


    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(@NonNull Myviewholder myviewholder, @SuppressLint("RecyclerView") int i) {
        double harga_normal= Double.parseDouble(myItem.get(i).getHarga());
        String harga = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(harga_normal);
        myviewholder.mnama_produk.setText(myItem.get(i).getNamaProduk());
        myviewholder.mharga.setText(harga);
        if (Integer.parseInt(myItem.get(i).getDiskon())==0){
            //jika diskon harga == 0 maka text harga normalt dan text diskon di hide dan text harga di ubah jadi warna Hitam
            myviewholder.mdiskon_harga.setVisibility(View.GONE);
            myviewholder.mharga.setTextColor(Color.parseColor("#000000"));
            myviewholder.mharga.setPaintFlags( myviewholder.mharga.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            //jika diskon harga tidak == 0 maka text harga di coret dan text diskon di show
            myviewholder.mdiskon_harga.setVisibility(View.VISIBLE);
            myviewholder.mharga.setTextColor(Color.parseColor("#919191"));
            if (! myviewholder.mharga.getPaint().isStrikeThruText()) {
                myviewholder.mharga.setPaintFlags( myviewholder.mharga.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            //set Harga Diskon
            double harga_diskon= Double.parseDouble(myItem.get(i).getDiskon());
            String diskon = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(harga_diskon);
            myviewholder.mdiskon_harga.setText(diskon);
        }
        Log.d("get_dataproduct: ",myItem.get(i).getNamaProduk());
        //clik item menuju ke details product
        myviewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent product_details = new Intent(context, ProductDetailsActivity.class);
                product_details.putExtra("keyId",myItem.get(i).getKeyId());
                context.startActivity(product_details);
                ((Activity)context).finish();
                ((Activity)context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
        //set Image
        Picasso.get()
                .load(myItem.get(i).getImg().get(0))
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .into(myviewholder.mimg_product);
    }

    @Override
    public int getItemCount() {
        if(myItem.size() > limit){
            return limit;
        }
        else
        {
            return myItem.size();
        }
    }
    public void filterList(ArrayList<ProductHomeItemDiskon> filteredList) {
        myItem = filteredList;
        notifyDataSetChanged();
    }
    public static class Myviewholder extends RecyclerView.ViewHolder{
        TextView mnama_produk,mharga,mdiskon_harga;
        ImageView mimg_product;
        public Myviewholder(@NonNull View view) {
            super(view);
            mnama_produk = view.findViewById(R.id.namaproduct);
            mharga = view.findViewById(R.id.harga);
            mimg_product = view.findViewById(R.id.imgproduct);
            mdiskon_harga = view.findViewById(R.id.diskonharga);


        }
    }

}

