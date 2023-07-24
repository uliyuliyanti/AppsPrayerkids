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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.prayerkidsstore.AdminApproveOrder.AdminApproveAdapter;
import com.prayerkidsstore.AdminApproveOrder.AdminApproveItem;
import com.prayerkidsstore.Order.OrderAdapter;
import com.prayerkidsstore.Order.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class AdminApproveUser extends AppCompatActivity {
    ImageView mback;
    /// LOAD DATA ORDER
    FirebaseFirestore firebaseFirestore, firebaseFirestore2;
    ArrayList<AdminApproveItem> orderItems;
    AdminApproveItem orderItem;
    AdminApproveAdapter orderAdapter;
    RecyclerView morder_list;
    ProgressDialog loading;
    String userId="";
    TextView mtotal_order,mtitle;
    String totalOrder="";
    /// load status
    List<String> statuslist;
    Spinner mstatus_list;
    //filter status
    Spinner mterlaris;
    ArrayList<AdminApproveItem> filteredList;
    String keyId="";
    public static String userName="";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_user);
        mback = findViewById(R.id.backbtn);
        mtotal_order = findViewById(R.id.total_order);
        morder_list = findViewById(R.id.order_list_product);
        mstatus_list = findViewById(R.id.status_list);
        mtitle = findViewById(R.id.title_order);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            keyId = extras.getString("keyId");
            userName = extras.getString("user_name");
            mtitle.setText("List Order "+userName);
            Log.d("Key-user:", keyId+userName);
            checkLogin();
        }


        /// back button
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mstatus_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (statuslist.get(position).equals("Semua")){
                    filter("");
                }else {
                    filter(statuslist.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void loadData(){
        orderItems = new ArrayList<>();
        orderItem = new AdminApproveItem();
        // adding our array list to our recycler view adapter class.
        morder_list.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        morder_list.setLayoutManager(mLayoutManager);

        orderAdapter = new AdminApproveAdapter(this, orderItems);
        morder_list.setAdapter(orderAdapter);
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("OrderPk").document(keyId).collection("OrderList").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error !=null){
                    Log.e("Fire Store Error",error.getMessage());
                    return;

                }else {
                    loading.dismiss();
                }
                for (DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        orderItem=dc.getDocument().toObject(AdminApproveItem.class);
                        orderItem.setOrderId(dc.getDocument().getId());
                        orderItem.setKeyId(keyId);
                        orderItems.add(orderItem);
                        Log.d("dataproduct",dc.getDocument().getData().toString());
                    }
                    totalOrder=String.valueOf(orderItems.size());
                    mtotal_order.setText("Total Order: "+totalOrder);
                    orderAdapter.notifyDataSetChanged();
                    loading.dismiss();
                }


            }

        });

    }
    public void loadStatus(){
        statuslist= new ArrayList<String>();
        firebaseFirestore2 = FirebaseFirestore.getInstance();
        DocumentReference docRef = firebaseFirestore2.collection("StatusOrder").document("ListsSatus");
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
                    ArrayAdapter arrayAdapter = new ArrayAdapter(AdminApproveUser.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, statuslist);
                    arrayAdapter.notifyDataSetChanged();
                    mstatus_list.setAdapter(arrayAdapter);
                    loading.dismiss();

                } else {
                    loading.dismiss();
                    Log.d("snpashot: ", "Current data: null");
                }
            }
        });

    };
    public void checkLogin(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in

            userId = user.getUid();
            loadData();
            loadStatus();
            Log.d("kondisi login","Login userId: "+userId);
        } else {
            // No user is signed in
            Intent logoout = new Intent(AdminApproveUser.this, LoginActivity.class);
            startActivity(logoout);
            finish();
            Log.d("kondisi login","Tidak Login");
        }


    }
    private void filter(String text) {
        filteredList = new ArrayList<>();
        if (orderItems.toString().equals("[]")){
            Log.d("filter",orderItem.toString());
        }else {
            for (AdminApproveItem item : orderItems) {
                if (item.getStatusOrder().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
                Log.d("filter",orderItems.toString());
                orderAdapter.filterList(filteredList);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(this, AdminApproveOrderList.class);
        startActivity(backhome);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
}