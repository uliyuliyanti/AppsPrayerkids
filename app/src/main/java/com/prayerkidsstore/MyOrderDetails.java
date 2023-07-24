package com.prayerkidsstore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.prayerkidsstore.Order.OrderAdapter;
import com.prayerkidsstore.Order.OrderItem;
import com.prayerkidsstore.OrderDetails.OrderDetailsAdapter;
import com.prayerkidsstore.OrderDetails.OrderDetailsItem;
import com.prayerkidsstore.checkout.CheckoutAdapter;
import com.prayerkidsstore.checkout.CheckoutImgAdapter;
import com.prayerkidsstore.checkout.CheckoutItem;
import com.prayerkidsstore.keranjang.KeranjangAdapter;
import com.prayerkidsstore.ongkir.ResultItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyOrderDetails extends AppCompatActivity {
    ImageView mback;
    TextView mstatus_order, morderid, morder_date, mtotal_price_order;
    LinearLayout mbtn_bayar;
    String orderId = "";
    String page = "";
    List<OrderDetailsItem> orderItems;
    ArrayList<OrderDetailsItem> orderItems2;
    OrderDetailsAdapter orderAdapter;
    RecyclerView mitemlist;
    FirebaseFirestore firebaseFirestore;
    String userId="";
    ProgressDialog loading;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_details);
        mback = findViewById(R.id.backbtn);
        mstatus_order = findViewById(R.id.status_order);
        morderid = findViewById(R.id.orderid);
        morder_date = findViewById(R.id.order_date);
        mtotal_price_order = findViewById(R.id.total_price_order);
        mbtn_bayar = findViewById(R.id.btn_bayar);
        mitemlist = findViewById(R.id.itemlist);

        //Mengambil orderId Product Kemudian Load Data Product
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderId = extras.getString("OrderId");
//            statuOrder = extras.getString("StatusOrder");

        }

        checkLogin();
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mbtn_bayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoPembayaran = new Intent(MyOrderDetails.this, PaymentActivity.class);
                gotoPembayaran.putExtra("OrderId",orderId);
                gotoPembayaran.putExtra("pages","order");
                startActivity(gotoPembayaran);
                finish();
            }
        });
    }
    public void loadDataOrder(){
        orderItems = new ArrayList<OrderDetailsItem>();
        orderItems2 = new ArrayList<OrderDetailsItem>();
        mitemlist.setHasFixedSize(true);
        mitemlist.setLayoutManager(new LinearLayoutManager(this));
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference docRef = firebaseFirestore.collection("OrderPk").document(userId).collection("OrderList").document(orderId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    loading.dismiss();
                    Log.w("snpashot: ", "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d("snpashot: ", "Current data: " + snapshot.getData());
                    mstatus_order.setText("Status : "+snapshot.get("StatusOrder").toString());
                    morderid.setText("Order Id: "+ snapshot.get("OrderId").toString());
                    morder_date.setText("Tanggal Order: "+ snapshot.get("OrderDate").toString());
                    mtotal_price_order.setText("Total: "+ snapshot.get("TotalPrice").toString());
                    /// jika customer belum upload bukti pembayaran maka status btn lnjut bayar show
                    if ( snapshot.get("StatusOrder").toString().toUpperCase().equals("MENUNGGU PEMBAYARAN")){
                        mbtn_bayar.setVisibility(View.VISIBLE);
                    }else {
                        mbtn_bayar.setVisibility(View.GONE);
                    }

                    //setItem list
                    orderItems= (ArrayList<OrderDetailsItem>) snapshot.get("orderUser");
                    Gson gson = new GsonBuilder().create();
                    JsonArray myCustomArray = gson.toJsonTree(orderItems).getAsJsonArray();
                    Type listType2 = new TypeToken<ArrayList<OrderDetailsItem>>() {
                    }.getType();
                    orderItems2 = gson.fromJson(myCustomArray.toString(),listType2);
                    Log.d("jsonarray-req",myCustomArray.toString());
                    orderAdapter = new OrderDetailsAdapter(MyOrderDetails.this, orderItems2);
                    mitemlist.setAdapter(orderAdapter);
                    // close item list
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Log.d("snpashot: ", "Current data: null");
                }
            }
        });

    }
    public void checkLogin(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in

            userId = user.getUid();
            loadDataOrder();
            Log.d("kondisi login","Login userId: "+userId);
        } else {
            // No user is signed in
            Intent logoout = new Intent(MyOrderDetails.this, LoginActivity.class);
            startActivity(logoout);
            finish();
            Log.d("kondisi login","Tidak Login");
        }


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(this, MyOrder.class);
        startActivity(backhome);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
}