package com.prayerkidsstore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.prayerkidsstore.AdminProduct.AdminProductAdapter;
import com.prayerkidsstore.AdminProduct.AdminProductItem;
import com.prayerkidsstore.Kategori.KategoriItem;
import com.prayerkidsstore.products.ProductAdapter;
import com.prayerkidsstore.products.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class AdminProductList extends AppCompatActivity {
    LinearLayout madd_product_btn;
    ImageView mback;
    ProgressDialog loading;
    FirebaseFirestore firebaseFirestore, firebaseFirestore2;
    String userId;
    FirebaseUser user;
    RecyclerView mproductlist;
    ArrayList<AdminProductItem> productitems;
    AdminProductItem listprod;
    AdminProductAdapter productAdapter;
    EditText msearch_product;
    SwipeRefreshLayout mswipe;
    ///filter Kategori
    ArrayList<KategoriItem> kategori_array;
    KategoriItem list_kategori;
    List<String> kategori = new ArrayList();
    Spinner mkategori_spinner;
    String kategori_value="";
    ArrayList<AdminProductItem> filteredList;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_prudcut_list);
        madd_product_btn = findViewById(R.id.add_product_btn);
        mback = findViewById(R.id.backbtn);
        mswipe = findViewById(R.id.swipe);
        mback = findViewById(R.id.backbtn);
        mkategori_spinner = findViewById(R.id.kategori_spinner);
        mproductlist=findViewById(R.id.productlist);
        msearch_product = findViewById(R.id.search_product);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//load Data
        checkLogin();
        loadData();
        ///Pencarian Product dengan search Bar\
        msearch_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        //filter kategori

        mkategori_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kategori_value=kategori_array.get(position).getKategori();
                filter(kategori_value);
                if (kategori_array.size()==0){

                }else {
                    if (kategori_array.get(position).getKategori().equals("All")){
                        kategori_value=" ";
                        filter(kategori_value);
                    }else {
                        kategori_value=kategori_array.get(position).getKategori();
                        filter(kategori_value);
                    }
                }
                Log.d("kategori_value",kategori_array.get(position).getKategori());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //swipe refresh page
        mswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        loadKategori();
        madd_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoadd=new Intent(AdminProductList.this,AdminProductAddActivity.class);
                startActivity(gotoadd);
                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(this, AccountActivity.class);
        startActivity(backhome);
        finish();
    }
    public void checkLogin(){
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //User Sedang Login
            userId = user.getUid();
            Log.d("kondisi login","Login userId: "+userId);
        } else {
            // User Sudah Logout
            Log.d("kondisi login","Tidak Login");
            Intent gologin = new Intent(AdminProductList.this,LoginActivity.class);
            startActivity(gologin);
            finish();
        }
    }
    public void loadData(){
        productitems = new ArrayList<>();
        listprod = new AdminProductItem();
        // adding our array list to our recycler view adapter class.
        mproductlist.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mproductlist.setLayoutManager(mLayoutManager);
        productAdapter = new AdminProductAdapter(this, productitems);
        mproductlist.setAdapter(productAdapter);
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.setCancelable(false);
        loading.show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("products").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error !=null){
                    Log.e("Fire Store Error",error.getMessage());
                    return;

                }
                for (DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        listprod=dc.getDocument().toObject(AdminProductItem.class);
                        listprod.setKeyId(dc.getDocument().getId());
                        productitems.add(listprod);
                        Log.d("dataproduct",dc.getDocument().getData().toString());
                    }

                    productAdapter.notifyDataSetChanged();
                    loading.dismiss();
                }
                mswipe.setRefreshing(false);

            }

        });

    }
    public void loadKategori(){
        kategori_array= new ArrayList<KategoriItem>();
        list_kategori = new KategoriItem();
        firebaseFirestore2 = FirebaseFirestore.getInstance();
        firebaseFirestore2.collection("Kategori_Product").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error !=null){
                    Log.e("Fire Store Error",error.getMessage());
                    return;

                }
                for (DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
//                        if (kategori_array!=null){
//                            kategori_array.clear();
//                        }

                        list_kategori=dc.getDocument().toObject(KategoriItem.class);
                        list_kategori.setKeyId(dc.getDocument().getId());
                        kategori_array.add(list_kategori);
                        Log.d("dataproduct312",dc.getDocument().getData().toString());
                        Log.d("data_kategori_size", String.valueOf(kategori_array.size()));

                    }
                    loading.dismiss();
                }
                for (int i = 0; i < kategori_array.size(); ++i){
                    kategori.add(kategori_array.get(i).getKategori());
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(AdminProductList.this, R.layout.spinner_kategori, kategori);
                arrayAdapter.notifyDataSetChanged();
                mkategori_spinner.setAdapter(arrayAdapter);
            }
        });

    }
    ///fillter
    private void filter(String text) {
        filteredList = new ArrayList<>();
        if (productitems.toString().equals("[]")){
            Log.d("filter",productitems.toString());
        }else {
            for (AdminProductItem item : productitems) {
                if (item.getNamaProduk().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }else if (item.getKategori().toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(item);
                }
            }
            Log.d("filter",productitems.toString());
            productAdapter.filterList(filteredList);
        }
    }

}