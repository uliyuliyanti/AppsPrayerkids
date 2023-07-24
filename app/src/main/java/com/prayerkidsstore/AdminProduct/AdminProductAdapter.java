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
package com.prayerkidsstore.AdminProduct;


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

import com.prayerkidsstore.AdminProductEdit;
import com.prayerkidsstore.ProductDetailsActivity;
import com.prayerkidsstore.R;
import com.prayerkidsstore.products.ProductItem;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class AdminProductAdapter
extends RecyclerView.Adapter<AdminProductAdapter.Myviewholder> {
    boolean solved = true;
    boolean showprogress = true;
    Context context;
    ArrayList<AdminProductItem> myItem;
//    ArrayList<ProductItem> addPolistitem;
    public AdminProductAdapter(Context c, ArrayList<AdminProductItem> p){
        context = c;
        myItem = p;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Myviewholder(LayoutInflater.from(context).inflate(R.layout.item_admin_product_list,
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

                Intent product_details = new Intent(context, AdminProductEdit.class);
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
        myviewholder.mkategori.setText(myItem.get(i).getKategori());
    }

    @Override
    public int getItemCount() {
        return
                myItem.size();
    }
    public void filterList(ArrayList<AdminProductItem> filteredList) {
        myItem = filteredList;
        notifyDataSetChanged();
    }
    public static class Myviewholder extends RecyclerView.ViewHolder{
        TextView mnama_produk,mharga,mdiskon_harga,mkategori;
        ImageView mimg_product;
        public Myviewholder(@NonNull View view) {
            super(view);
            mnama_produk = view.findViewById(R.id.nama_admin_product);
            mharga = view.findViewById(R.id.harga_admin_product);
            mimg_product = view.findViewById(R.id.img_admin_product);
            mdiskon_harga = view.findViewById(R.id.diskon_admin_product);
            mkategori = view.findViewById(R.id.kategori_admin_product);


        }
    }

}

