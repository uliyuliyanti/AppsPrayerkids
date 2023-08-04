package com.prayerkidsstore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.prayerkidsstore.AdminUserOrder.AdminUserOrderAdapter;
import com.prayerkidsstore.AdminUserOrder.AdminUserOrderItem;
import com.prayerkidsstore.Order.OrderAdapter;
import com.prayerkidsstore.Order.OrderItem;

import java.util.ArrayList;

public class AdminApproveOrderList extends AppCompatActivity {
    String userId;
    ArrayList<AdminUserOrderItem> adminUserOrderItems;
    AdminUserOrderItem adminUserOrderItem;
    AdminUserOrderAdapter adminUserOrderAdapter;
    RecyclerView morder_list;
    ProgressDialog loading;
    FirebaseFirestore firebaseFirestore;
    public static TextView mtotal_order;
    String total="";
    ImageView mbackbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_order_list);
        morder_list = findViewById(R.id.order_list_reseller);
        mtotal_order=findViewById(R.id.total_order);
        mbackbtn = findViewById(R.id.backbtn);
        mbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        checkLogin();
    }
    public void loadData(){
        adminUserOrderItems = new ArrayList<>();
        adminUserOrderItem = new AdminUserOrderItem();
        // adding our array list to our recycler view adapter class.
        morder_list.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        morder_list.setLayoutManager(mLayoutManager);

        adminUserOrderAdapter = new AdminUserOrderAdapter(this, adminUserOrderItems);
        morder_list.setAdapter(adminUserOrderAdapter);
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("UserPk").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        adminUserOrderItem=dc.getDocument().toObject(AdminUserOrderItem.class);
                        adminUserOrderItem.setKeyId(dc.getDocument().getId());
                        adminUserOrderItems.add(adminUserOrderItem);
                        Log.d("dataproduct",dc.getDocument().getData().toString());
                    }
//                    total=String.valueOf(adminUserOrderItems.size());
//                    mtotal_order.setText("Total Order: "+total);
                    adminUserOrderAdapter.notifyDataSetChanged();
                    loading.dismiss();
                }


            }

        });

    }
//    public void loadStatus(){
//        statuslist= new ArrayList<String>();
//        firebaseFirestore2 = FirebaseFirestore.getInstance();
//        DocumentReference docRef = firebaseFirestore2.collection("StatusOrder").document("ListsSatus");
//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
////                    loading.dismiss();
//                    Log.w("snpashot: ", "Listen failed.", e);
//                    return;
//                }
//
//                if (snapshot != null && snapshot.exists()) {
//
//                    //Set Size Sepatu
//                    statuslist.add("All");
//                    statuslist= (ArrayList<String>) snapshot.get("orderStatus");
//                    ArrayAdapter arrayAdapter = new ArrayAdapter(MyOrder.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, statuslist);
//                    arrayAdapter.notifyDataSetChanged();
//                    mstatus_list.setAdapter(arrayAdapter);
//                    loading.dismiss();
//
//                } else {
//                    loading.dismiss();
//                    Log.d("snpashot: ", "Current data: null");
//                }
//            }
//        });
//
//    };
    public void checkLogin(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in

            userId = user.getUid();
            loadData();
//            loadStatus();
            Log.d("kondisi login","Login userId: "+userId);
        } else {
            // No user is signed in
            Intent logoout = new Intent(AdminApproveOrderList.this, LoginActivity.class);
            startActivity(logoout);
            finish();
            Log.d("kondisi login","Tidak Login");
        }


    }
//    private void filter(String text) {
//        filteredList = new ArrayList<>();
//        if (orderItems.toString().equals("[]")){
//            Log.d("filter",orderItem.toString());
//        }else {
//            for (OrderItem item : orderItems) {
//                if (item.getStatusOrder().toLowerCase().contains(text.toLowerCase())) {
//                    filteredList.add(item);
//                }
//                Log.d("filter",orderItems.toString());
//                orderAdapter.filterList(filteredList);
//            }
//        }
//    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(this, AccountActivity.class);
        startActivity(backhome);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
}