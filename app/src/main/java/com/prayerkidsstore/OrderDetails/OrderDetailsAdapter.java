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
package com.prayerkidsstore.OrderDetails;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prayerkidsstore.MyOrderDetails;
import com.prayerkidsstore.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class OrderDetailsAdapter
extends RecyclerView.Adapter<OrderDetailsAdapter.Myviewholder> {
    boolean solved = true;
    boolean showprogress = true;
    Context context;
    ArrayList<OrderDetailsItem> myItem;
    int harga_total_plus = 0;
    int qty_total_plus = 0;
    double harga_total = 0.0;
    double diskon = 0.0;
    public OrderDetailsAdapter(Context c, ArrayList<OrderDetailsItem> p){
        context = c;
        myItem = p;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Myviewholder(LayoutInflater.from(context).inflate(R.layout.item_order_details,
                viewGroup, false));


    }


    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(@NonNull Myviewholder myviewholder, @SuppressLint("RecyclerView") int i) {
        /// jika item lebih dari satu tampilkan item lainnya
        myviewholder.mnama_product_order.setText(myItem.get(i).getNamaProduct());
        myviewholder.msize_order.setText("Size: "+myItem.get(i).getSize());
        myviewholder.mqty_order.setText("Qty: "+myItem.get(i).getQty());
        if (Integer.parseInt(myItem.get(i).getDiskon())==0){
            //jika diskon harga == 0 maka text harga normalt dan text diskon di hide dan text harga di ubah jadi warna Hitam
            myviewholder.mdiskon_order.setVisibility(View.GONE);
            myviewholder.mharga_order.setTextColor(Color.parseColor("#000000"));
        }else {
            //jika diskon harga tidak == 0 maka text harga di coret dan text diskon di show
            myviewholder.mdiskon_order.setVisibility(View.VISIBLE);
            myviewholder.mharga_order.setTextColor(Color.parseColor("#919191"));
            if (! myviewholder.mharga_order.getPaint().isStrikeThruText()) {
                myviewholder.mharga_order.setPaintFlags( myviewholder.mharga_order.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                myviewholder.mharga_order.setPaintFlags( myviewholder.mharga_order.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
            //set Harga Diskon
            double harga_diskon= Double.parseDouble(myItem.get(i).getDiskon());
            String diskon = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(harga_diskon);
            myviewholder.mharga_order.setText(diskon);
        }
        if (myItem.get(i).getCatatan().equals("")){
            myviewholder.mcatatan_orders.setText("");
        }else {
            myviewholder.mcatatan_orders.setText("Catatan: "+myItem.get(i).getCatatan());
        }

        //set Image

        Picasso.get()
                .load(myItem.get(i).getImg())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .into(myviewholder.mimg_order);
    }

    @Override
    public int getItemCount() {
        return
                myItem.size();
    }
    public void filterList(ArrayList<OrderDetailsItem> filteredList) {
        myItem = filteredList;
        notifyDataSetChanged();
    }
    public static class Myviewholder extends RecyclerView.ViewHolder{
        TextView mnama_product_order,msize_order,mharga_order,mdiskon_order,mqty_order,mcatatan_orders;
        ImageView mimg_order;
        public Myviewholder(@NonNull View view) {
            super(view);

            mimg_order = view.findViewById(R.id.img_order);
            mnama_product_order = view.findViewById(R.id.nama_product_order);
            msize_order = view.findViewById(R.id.size_order);
            mharga_order = view.findViewById(R.id.harga_order);
            mdiskon_order = view.findViewById(R.id.diskon_order);
            mqty_order = view.findViewById(R.id.qty_order);
            mcatatan_orders = view.findViewById(R.id.catatan_orders);




        }
    }



}

