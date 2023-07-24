package com.prayerkidsstore;

import static com.prayerkidsstore.apihelper.ServiceGenerator.fcmbase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.prayerkidsstore.Order.OrderItem;
import com.prayerkidsstore.OrderDetails.OrderDetailsAdapter;
import com.prayerkidsstore.OrderDetails.OrderDetailsItem;
import com.prayerkidsstore.apihelper.IRetrofit;
import com.prayerkidsstore.apihelper.ServiceGenerator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminApproveUserDetails extends AppCompatActivity {
    ImageView mback;
    TextView mstatus_order, morderid, morder_date, mtotal_price_order,mupdate_status;
    LinearLayout mbtn_bayar,mupdate_lay;
    String orderId = "";
    String page = "";
    List<OrderDetailsItem> orderItems;
    ArrayList<OrderDetailsItem> orderItems2;
    OrderDetailsAdapter orderAdapter;
    RecyclerView mitemlist;
    FirebaseFirestore firebaseFirestore;
    String userId="";
    ProgressDialog loading;
    String keyId = "";
    String username = "";
    /// load status
    List<String>  statuslist;
    Spinner mstatus_list;
    String status_order="";
    String status_update="";
    int  pos = 0;
    ////send notif
    List<String>token_add;
    JsonArray token_user;
    String token="";
    String orderbadge="";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_user_details);
        mupdate_lay = findViewById(R.id.update_lay);
        mback = findViewById(R.id.backbtn);
        mstatus_order = findViewById(R.id.status_order);
        morderid = findViewById(R.id.orderid);
        morder_date = findViewById(R.id.order_date);
        mtotal_price_order = findViewById(R.id.total_price_order);
        mbtn_bayar = findViewById(R.id.btn_bayar);
        mitemlist = findViewById(R.id.itemlist);
        mstatus_list = findViewById(R.id.status_spinner);
        mupdate_status = findViewById(R.id.update_status);
        //Mengambil orderId Product Kemudian Load Data Product
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderId = extras.getString("OrderId");
            keyId = extras.getString("keyId");
            username = extras.getString("user_name");
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
                Intent gotoPembayaran = new Intent(AdminApproveUserDetails.this, PaymentActivity.class);
                gotoPembayaran.putExtra("OrderId",orderId);
                gotoPembayaran.putExtra("pages","order");
                startActivity(gotoPembayaran);
                finish();
            }
        });
        mstatus_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status_update = statuslist.get(position);
                Log.d("status_update",status_update);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mupdate_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatus();
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
        final DocumentReference docRef = firebaseFirestore.collection("OrderPk").document(keyId).collection("OrderList").document(orderId);
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
                    status_order=snapshot.get("StatusOrder").toString();
                    mstatus_order.setText("Status : "+snapshot.get("StatusOrder").toString());
                    morderid.setText("Order Id: "+ snapshot.get("OrderId").toString());
                    morder_date.setText("Tanggal Order: "+ snapshot.get("OrderDate").toString());
                    mtotal_price_order.setText("Total: "+ snapshot.get("TotalPrice").toString());
                    /// jika customer belum upload bukti pembayaran maka status btn lnjut bayar show
                    if ( snapshot.get("StatusOrder").toString().toUpperCase().equals("MENUNGGU PEMBAYARAN")){
                        mbtn_bayar.setVisibility(View.GONE);
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
                    orderAdapter = new OrderDetailsAdapter(AdminApproveUserDetails.this, orderItems2);
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
            loadStatus();
//            getTokenUser();
            Log.d("kondisi login","Login userId: "+userId);
        } else {
            // No user is signed in
            Intent logoout = new Intent(AdminApproveUserDetails.this, LoginActivity.class);
            startActivity(logoout);
            finish();
            Log.d("kondisi login","Tidak Login");
        }


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(this, AdminApproveUser.class);
        backhome.putExtra("keyId",keyId);
        backhome.putExtra("user_name",username);
        startActivity(backhome);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
    public void loadStatus(){
        statuslist= new ArrayList<String>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firebaseFirestore.collection("StatusOrder").document("ListsSatus");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
//                    loading.dismiss();
                    Log.w("snpashot: ", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    //Set Size Sepatu
                    statuslist.add("All");
                    statuslist= (ArrayList<String>) snapshot.get("orderStatus");
                    ArrayAdapter arrayAdapter = new ArrayAdapter(AdminApproveUserDetails.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, statuslist);
                    arrayAdapter.notifyDataSetChanged();
                    if (status_order.equals("Selesai")){
                        mupdate_lay.setVisibility(View.GONE);
                    }else if (status_order.equals("Batal")){
                        mupdate_lay.setVisibility(View.GONE);
                    }else {
                        mupdate_lay.setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < statuslist.size(); ++i){
                        if (statuslist.get(i).equals(status_order)){
                            pos=i;
                        }
                    }
                    mstatus_list.setAdapter(arrayAdapter);
                    mstatus_list.setSelection(pos);

                    getBadgeorder();
                } else {
                    loading.dismiss();
                    Log.d("snpashot: ", "Current data: null");
                }
            }
        });

    };
    public void updateStatus(){
        mupdate_status.setEnabled(false);
        mupdate_status.setText("Loading...");
        Map<String, Object> dataEmailNama = new HashMap();
        dataEmailNama.put("StatusOrder",status_update);
        DocumentReference documentReference = firebaseFirestore.collection("OrderPk").document(keyId).collection("OrderList").document(orderId);
        documentReference.update(dataEmailNama).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AdminApproveUserDetails.this, "Update Berhasil", Toast.LENGTH_SHORT).show();
                Log.d("UpdateRespon: ","Update Berhasil");

                mupdate_status.setText("Update Status");
                mupdate_status.setEnabled(true);
                sendnotifchat();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminApproveUserDetails.this, "Update Gagal", Toast.LENGTH_SHORT).show();
                Log.d("UpdateRespon: ",e.toString());

                mupdate_status.setText("Update Status");
                mupdate_status.setEnabled(true);
            }
        });
    }
    public void sendnotifchat(){
        token_add = new ArrayList<>();
        token_add.add(token);
        Gson gson = new GsonBuilder().create();
        token_user = gson.toJsonTree(token_add).getAsJsonArray();
        JsonObject dataid = new JsonObject();
        dataid.addProperty("id", userId+"/"+orderId);

        JsonObject notifikasidata = new JsonObject();
        notifikasidata.addProperty("title",orderId);
        notifikasidata.addProperty("body","Oder: "+orderId+" "+status_update);
        notifikasidata.addProperty("click_action","ordermasuk");

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("registration_ids", token_user);
        jsonObject.add("data",dataid);
        jsonObject.add("notification",notifikasidata);
//        Toast.makeText(DetailsFormActivity.this,jsonObject.toString(), Toast.LENGTH_SHORT).show();
        IRetrofit jsonPostService = ServiceGenerator.createService(IRetrofit.class, fcmbase);
        Call<JsonObject> panggilkomplek = jsonPostService.sendNotifcation(jsonObject);
        panggilkomplek.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String errornya = "";
                JsonObject homedata = response.body();
                Log.d("responnotif",homedata.toString());
                int statusnya = homedata.get("success").getAsInt();
                if (statusnya == 1) {
//                    onBackPressed();
                    if (status_update.equals("Selesai")){
                        updateOrderBade();
                    }else if(status_update.equals("Batal")){
                        updateOrderBade();
                    }else {
                        onBackPressed();
                    }

                    Log.d("responnotif",homedata.toString());
                } else {

                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                loading.dismiss();


            }
        });
        Log.d("requestnotif",jsonObject.toString());




    }
    public void updateOrderBade(){
        int orderbadges = Integer.parseInt(orderbadge)-1;
        orderbadge = String.valueOf(orderbadges);
        Map<String, Object> dataEmailNama = new HashMap();
        dataEmailNama.put("OrderBadge",orderbadge);
        DocumentReference documentReference = firebaseFirestore.collection("UserPk").document(keyId);
        documentReference.update(dataEmailNama).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                Toast.makeText(PaymentCheckout.this, "Update Berhasil", Toast.LENGTH_SHORT).show();
                Log.d("UpdateRespon: ","Update Berhasil");
                loading.dismiss();
                ////update badge order
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading.dismiss();
                Toast.makeText(AdminApproveUserDetails.this, "Update Gagal", Toast.LENGTH_SHORT).show();
                Log.d("UpdateRespon: ",e.toString());

            }
        });
    }
    public void getBadgeorder(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference docRef = firebaseFirestore.collection("UserPk").document(keyId);
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
                    orderbadge = snapshot.get("OrderBadge").toString();
                    token =  snapshot.get("token").toString();
                    Log.d("token_get",token);
                    ////load data item
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Log.d("snpashot: ", "Current data: null");
                }
            }
        });
    }
    public void getTokenUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN_NOTIF",MODE_PRIVATE);
        token = sharedPreferences.getString("token_user", "");
        Log.d("token_get",token);

    }
}