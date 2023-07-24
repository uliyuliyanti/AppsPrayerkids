package com.prayerkidsstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.prayerkidsstore.keranjang.KeranjangItem;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageView  mback, mlogout;
    String userId="";
    FirebaseUser user;
    ProgressDialog loading;
    LinearLayout mdetailakun,mgantipassword,mpesanansaya,mproductaddlist,mapprove_list;
    TextView mbadge_myorder;
    int qty_keranjang=0;
    FirebaseFirestore firebaseFirestore;
    SwipeRefreshLayout mswipe;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mback = findViewById(R.id.backbtn);
        mlogout = findViewById(R.id.logout);
        mdetailakun=findViewById(R.id.detailakun);
        mgantipassword = findViewById(R.id.gantipassword);
        mpesanansaya = findViewById(R.id.pesanansaya);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        mbadge_myorder = findViewById(R.id.badge_myorder);
        mproductaddlist = findViewById(R.id.productaddlist);
        mapprove_list = findViewById(R.id.approve_list);
        mswipe = findViewById(R.id.swipe);
        //check Login
        checkLogin();
        mswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkLogin();
            }
        });
        //Seting Menu Bottom
        bottomNavigationView.setSelectedItemId(R.id.bottom_akun);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), HomeAct.class));
                    this.overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.bottom_produk:
                    startActivity(new Intent(getApplicationContext(), ProductActivity.class));
                    this.overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.bottom_cart:
                    startActivity(new Intent(getApplicationContext(), KeranjangActivity.class));
                    this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                    return true;
                case R.id.bottom_akun:

                    return true;
            }
            return false;
        });
        //// pesanan saya
        mpesanansaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailakun = new Intent(AccountActivity.this, MyOrder.class);
                startActivity(detailakun);
                finish();
            }
        });
        //Proses Logout
        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //panggil LogOutAkun
                LogOutAkun();
            }
        });
        //Back ke Home
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Btn Detail Akun
        mdetailakun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailakun = new Intent(AccountActivity.this, AccountDetails.class);
                startActivity(detailakun);
                finish();
            }
        });
        //Btn Change Password
        mgantipassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gantipassword = new Intent(AccountActivity.this, ChangePassword.class);
                startActivity(gantipassword);
                finish();
            }
        });
        /////ADMIN APRODUCT LIST BTN
        mproductaddlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adminproductlist = new Intent(AccountActivity.this, AdminProductList.class);
                startActivity(adminproductlist);
                finish();
            }
        });
        /////ADMIN Approve LIST BTN
        mapprove_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adminproductlist = new Intent(AccountActivity.this, AdminApproveOrderList.class);
                startActivity(adminproductlist);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(this, HomeAct.class);
        startActivity(backhome);
        this.overridePendingTransition(0, 0);
        finish();
    }
    public void LogOutAkun(){
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.show();
        FirebaseAuth AuthUI = FirebaseAuth.getInstance();
        AuthUI.signOut();
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            userId = user.getUid();
            Log.d("kondisi login","Login userId: "+userId);
            loading.dismiss();

        } else {
            loading.dismiss();
            // No user is signed in
            Log.d("kondisi login","Tidak Login");
            Intent logoout = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(logoout);
            finish();
        }

    }
    public void checkLogin(){
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            userId = user.getUid();
            checkKeranjang();
            loadUser();
            Log.d("kondisi login","Login userId: "+userId);

            mswipe.setRefreshing(false);
        } else {
            mswipe.setRefreshing(false);
            // No user is signed in
            Log.d("kondisi login","Tidak Login");
            Intent logoout = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(logoout);
            finish();
        }


    }
    public void checkKeranjang(){
        SharedPreferences sharedPreferences = getSharedPreferences("CHEK_KERANJANG",MODE_PRIVATE);
        int qty_keranjang = sharedPreferences.getInt("keranjang_list", 0);
        int qty_order = sharedPreferences.getInt("order_qty", 0);
        if (qty_keranjang==0){

        }else {
            int menuItemId = bottomNavigationView.getMenu().getItem(2).getItemId();
            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(menuItemId);
            badge.setNumber(qty_keranjang);
        }
        if (qty_order==0){
            mbadge_myorder.setVisibility(View.GONE);
        }else {
            mbadge_myorder.setVisibility(View.VISIBLE);
            mbadge_myorder.setText(String.valueOf(qty_order));

        }

    }
    public void loadUser(){
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.setCancelable(false);
        loading.show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference docRef = firebaseFirestore.collection("UserPk").document(userId);
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
                    boolean admin =Boolean.parseBoolean(snapshot.get("Admin").toString());
                    if (admin){
                        mapprove_list.setVisibility(View.VISIBLE);
                        mproductaddlist.setVisibility(View.VISIBLE);
                    }else {
                        mapprove_list.setVisibility(View.GONE);
                        mproductaddlist.setVisibility(View.GONE);
                    }
                    loading.dismiss();

                } else {
                    loading.dismiss();
                    Log.d("snpashot: ", "Current data: null");
                }
            }
        });
    }


}