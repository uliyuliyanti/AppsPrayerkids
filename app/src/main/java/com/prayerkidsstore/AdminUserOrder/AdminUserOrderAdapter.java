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
package com.prayerkidsstore.AdminUserOrder;


import static com.prayerkidsstore.AdminApproveOrderList.mtotal_order;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.prayerkidsstore.AdminApproveUser;
import com.prayerkidsstore.MyOrderDetails;
import com.prayerkidsstore.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class AdminUserOrderAdapter
extends RecyclerView.Adapter<AdminUserOrderAdapter.Myviewholder> {
    boolean solved = true;
    boolean showprogress = true;
    Context context;
    ArrayList<AdminUserOrderItem> myItem;
    int total_pending = 0;
    int qty_total_plus = 0;
    double harga_total = 0.0;
    double diskon = 0.0;
    public static List<String> token_add;
    public AdminUserOrderAdapter(Context c, ArrayList<AdminUserOrderItem> p){
        context = c;
        myItem = p;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Myviewholder(LayoutInflater.from(context).inflate(R.layout.item_order_user,
                viewGroup, false));


    }


    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(@NonNull Myviewholder myviewholder, @SuppressLint("RecyclerView") int i) {
        ///set bg status
        myviewholder.muser_name.setText("Nama Reseller: "+myItem.get(i).getUserName());
        myviewholder.morderbadge.setText("Order belum selesai: "+myItem.get(i).getOrderBadge()+" Order");
        if (myItem.get(i).getOrderBadge().equals("0")){
            myviewholder.itemView.setVisibility(View.GONE);
            myviewholder.itemView.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = myviewholder.itemView.getLayoutParams();
            params.height = 0;
            params.width = 0;
            myviewholder.itemView.setLayoutParams(params);
        }else {
            myviewholder.itemView.setVisibility(View.VISIBLE);
            total_pending+=1;
            mtotal_order.setText(String.valueOf("Total Order Proses: "+total_pending));

        }
        myviewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent product_details = new Intent(context, AdminApproveUser.class);
                product_details.putExtra("keyId",myItem.get(i).getKeyId());
                product_details.putExtra("user_name",myItem.get(i).getUserName());
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
    public void filterList(ArrayList<AdminUserOrderItem> filteredList) {
        myItem = filteredList;
        notifyDataSetChanged();
    }
    public static class Myviewholder extends RecyclerView.ViewHolder{
        TextView muser_name,morderbadge;
        ImageView mimg_order;
        public Myviewholder(@NonNull View view) {
            super(view);
            morderbadge = view.findViewById(R.id.order_badge);
            muser_name = view.findViewById(R.id.user_name);





        }
    }



}

