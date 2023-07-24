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
package com.prayerkidsstore.Order;


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
import com.prayerkidsstore.MyOrderDetails;
import com.prayerkidsstore.ProductDetailsActivity;
import com.prayerkidsstore.R;
import com.prayerkidsstore.products.ProductItem;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class OrderAdapter
extends RecyclerView.Adapter<OrderAdapter.Myviewholder> {
    boolean solved = true;
    boolean showprogress = true;
    Context context;
    ArrayList<OrderItem> myItem;
    int harga_total_plus = 0;
    int qty_total_plus = 0;
    double harga_total = 0.0;
    double diskon = 0.0;
    public OrderAdapter(Context c, ArrayList<OrderItem> p){
        context = c;
        myItem = p;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Myviewholder(LayoutInflater.from(context).inflate(R.layout.item_order_product,
                viewGroup, false));


    }


    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(@NonNull Myviewholder myviewholder, @SuppressLint("RecyclerView") int i) {
        ///set bg status
        if (myItem.get(i).getStatusOrder().toUpperCase().equals("MENUNGGU PEMBAYARAN")){
            myviewholder.mstatus.setBackground(context.getDrawable(R.drawable.bg_sts_unpaid));
        }
        if (myItem.get(i).getStatusOrder().toUpperCase().equals("SELESAI")){
            myviewholder.mstatus.setBackground(context.getDrawable(R.drawable.bg_sts_selesai));
        }
        if (myItem.get(i).getStatusOrder().toUpperCase().equals("BATAL")){
            myviewholder.mstatus.setBackground(context.getDrawable(R.drawable.bg_sts_batal));
        }
        if (myItem.get(i).getStatusOrder().toUpperCase().equals("MENUNGGU KONFIRMASI")){
            myviewholder.mstatus.setBackground(context.getDrawable(R.drawable.bg_sts_waiting));
        }
        if (myItem.get(i).getStatusOrder().toUpperCase().equals("DALAM PROSES")){
            myviewholder.mstatus.setBackground(context.getDrawable(R.drawable.bg_sts_proses));
        }
        if (myItem.get(i).getStatusOrder().toUpperCase().equals("DALAM PENGIRIMAN")){
            myviewholder.mstatus.setBackground(context.getDrawable(R.drawable.bg_sts_pengirimani));
        }
        /// jika item lebih dari satu tampilkan item lainnya
        if (myItem.get(i).getOrderUser().size()>1){
            myviewholder.mitem_lainnya.setVisibility(View.VISIBLE);
        }else {
            myviewholder.mitem_lainnya.setVisibility(View.GONE);
        }
        //// set list
        myviewholder.morderId.setText(myItem.get(i).getOrderId());
        myviewholder.morderDate.setText(myItem.get(i).getOrderDate());
        myviewholder.mstatus.setText(myItem.get(i).getStatusOrder());
        myviewholder.mtotal_harga_order.setText(myItem.get(i).getTotalPrice());
//        qty_total_plus += Integer.parseInt(myItem.get(i).getOrderUser().get(i).getQty());
        myviewholder.mtotal_item_order.setText(myItem.get(i).getTotalItem());
        myviewholder.mqty_order.setText("Qty: "+myItem.get(i).getOrderUser().get(0).getQty());
        myviewholder.mnama_order_product.setText(myItem.get(i).getOrderUser().get(0).getNamaProduct());
        myviewholder.msize_order_product.setText("size: "+myItem.get(i).getOrderUser().get(0).getSize());
        ///setHarga
        harga_total= Double.parseDouble(String.valueOf(myItem.get(i).getOrderUser().get(0).getHarga()));
        String harga_total_text = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(harga_total);
        myviewholder.mharga_order.setText(harga_total_text);
        if (Integer.parseInt(myItem.get(i).getOrderUser().get(0).getDiskon())==0){
            //jika diskon harga == 0 maka text harga normalt dan text diskon di hide dan text harga di ubah jadi warna Hitam
            myviewholder.mdiskon_order.setVisibility(View.GONE);
            myviewholder.mharga_order.setTextColor(Color.parseColor("#000000"));
            myviewholder.mharga_order.setPaintFlags( myviewholder.mharga_order.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            //jika diskon harga tidak == 0 maka text harga di coret dan text diskon di show
            myviewholder.mdiskon_order.setVisibility(View.VISIBLE);
            myviewholder.mharga_order.setTextColor(Color.parseColor("#919191"));
            myviewholder.mharga_order.setPaintFlags( myviewholder.mharga_order.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            if (! myviewholder.mharga_order.getPaint().isStrikeThruText()) {
//                myviewholder.mharga_order.setPaintFlags( myviewholder.mharga_order.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            } else {
//                myviewholder.mharga_order.setPaintFlags( myviewholder.mharga_order.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
//            }
            //set Harga Diskon
            diskon= Double.parseDouble(String.valueOf(myItem.get(i).getOrderUser().get(0).getDiskon()));
            String diskontext = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(diskon);
            myviewholder.mdiskon_order.setText(diskontext);
        }
        ///set IMG
        Picasso.get()
                .load(myItem.get(i).getOrderUser().get(0).getImg())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .into(myviewholder.mimg_order);
        ////set Click Item
        myviewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent product_details = new Intent(context, MyOrderDetails.class);
                product_details.putExtra("OrderId",myItem.get(i).getOrderId());
                product_details.putExtra("StatusOrder",myItem.get(i).getStatusOrder());
                context.startActivity(product_details);
                ((Activity)context).finish();
                ((Activity)context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return
                myItem.size();
    }
    public void filterList(ArrayList<OrderItem> filteredList) {
        myItem = filteredList;
        notifyDataSetChanged();
    }
    public static class Myviewholder extends RecyclerView.ViewHolder{
        TextView morderId,morderDate,mstatus,mtotal_item_order,mtotal_harga_order, mitem_lainnya,mnama_order_product,mdiskon_order,mharga_order,mqty_order,msize_order_product;
        ImageView mimg_order;
        public Myviewholder(@NonNull View view) {
            super(view);
            morderId = view.findViewById(R.id.orderId);
            morderDate = view.findViewById(R.id.orderDate);
            mstatus = view.findViewById(R.id.status);
            mtotal_item_order = view.findViewById(R.id.total_item_order);
            mtotal_harga_order = view.findViewById(R.id.total_harga_order);
            mitem_lainnya = view.findViewById(R.id.item_lainnya);
            mnama_order_product = view.findViewById(R.id.nama_order_product);
            mdiskon_order = view.findViewById(R.id.diskon_order);
            mharga_order = view.findViewById(R.id.harga_order);
            mqty_order = view.findViewById(R.id.qty_order);
            mimg_order = view.findViewById(R.id.img_order);
            msize_order_product = view.findViewById(R.id.size_order_product);



        }
    }



}

