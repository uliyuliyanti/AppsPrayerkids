package com.prayerkidsstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.prayerkidsstore.Kategori.KategoriItem;
import com.prayerkidsstore.keranjang.KeranjangItem;
import com.prayerkidsstore.products.ProductAdapter;
import com.prayerkidsstore.products.ProductItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductActivity extends AppCompatActivity {
    public static String qty_kearnjang="";
    TextView mtotal_produk;
    String total_produk;
    BottomNavigationView bottomNavigationView;
    ImageView mback;
    ProgressDialog loading;
    FirebaseFirestore firebaseFirestore, firebaseFirestore2;
    String userId;
    FirebaseUser user;
    RecyclerView mproductlist;
    ArrayList<ProductItem> productitems;
    ProductItem listprod;
    ProductAdapter productAdapter;
    EditText msearch_product;
    SwipeRefreshLayout mswipe;
    ///filter Kategori
    ArrayList<KategoriItem> kategori_array;
    KategoriItem list_kategori;
    List<String> kategori = new ArrayList();
    Spinner mkategori_spinner;
    String kategori_value="";
    int pos=0;
    //filter Terlaris
    List<String>terlaris_list;
    Spinner mterlaris;
    ArrayList<ProductItem> filteredList;
    String kategori_get="All";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        mswipe = findViewById(R.id.swipe);
        mtotal_produk = findViewById(R.id.total_produk);
        mback = findViewById(R.id.backbtn);
        mkategori_spinner = findViewById(R.id.kategori_spinner);
        mterlaris = findViewById(R.id.terlaris);
        mproductlist=findViewById(R.id.productlist);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            kategori_get = extras.getString("get_kategori");

        }else {
            kategori_get = "All";

        }
        msearch_product = findViewById(R.id.search_product);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_produk);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), HomeAct.class));
                    this.overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.bottom_produk:
                    return true;
                case R.id.bottom_cart:
                    startActivity(new Intent(getApplicationContext(), KeranjangActivity.class));
                    this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                    return true;
                case R.id.bottom_akun:
                    startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                    this.overridePendingTransition(0, 0);
                    finish();
                    return true;
            }
            return false;
        });
        //Back ke Home
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
      ///Filter Terlaris diskon
//        terlaris_list = new ArrayList<>();
//        terlaris_list.add("All");
//        terlaris_list.add("Terlaris");
//        terlaris_list.add("Produk Diskon");
//        ArrayAdapter arrayAdapter2 = new ArrayAdapter(ProductActivity.this, R.layout.spinner_kategori, terlaris_list);
//        arrayAdapter2.notifyDataSetChanged();
//        mterlaris.setAdapter(arrayAdapter2);
//        mterlaris.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position==0){
//
//                }
//                if (position==1){
//                    filterTerlaris();
//                }
//                if (position==2){
//                    filterDiskon();
//                }
////                if (terlaris_list.get(position).equals("All")){
////
////                }
////                if (terlaris_list.get(position).equals("Terlaris")){
////                    filterTerlaris();
////                }
////                if (terlaris_list.get(position).equals("Produk Diskon")){
////                    filterDiskon();
////                }
//                Log.d("filter_terlaris",String.valueOf(position));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(this, HomeAct.class);
        startActivity(backhome);
        this.overridePendingTransition(0, 0);
        finish();
    }
    public void checkLogin(){
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //User Sedang Login
            userId = user.getUid();
            checkKeranjang();
            Log.d("kondisi login","Login userId: "+userId);
        } else {
            // User Sudah Logout
            Log.d("kondisi login","Tidak Login");
//            finish();
        }
    }
    public void loadData(){
        productitems = new ArrayList<>();
        listprod = new ProductItem();
        // adding our array list to our recycler view adapter class.
        mproductlist.setHasFixedSize(true);
        mproductlist.setLayoutManager(new GridLayoutManager(this,2));
        productAdapter = new ProductAdapter(this, productitems);
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
                                 listprod=dc.getDocument().toObject(ProductItem.class);
                                 listprod.setKeyId(dc.getDocument().getId());
                                 productitems.add(listprod);
                                 Log.d("dataproduct",dc.getDocument().getData().toString());
                             }

                             productAdapter.notifyDataSetChanged();
                             loading.dismiss();
                         }
                         mswipe.setRefreshing(false);
                         total_produk = String.valueOf(productitems.size());
                         mtotal_produk.setText("Total Produk: "+total_produk);

                    }

                });
        filter(kategori_get);
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
                    if (kategori_array.get(i).getKategori().equals(kategori_get)){
                        pos = i;
                    }
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(ProductActivity.this, R.layout.spinner_kategori, kategori);
                arrayAdapter.notifyDataSetChanged();
                mkategori_spinner.setAdapter(arrayAdapter);
                mkategori_spinner.setSelection(pos);
            }
        });

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

        }else {
            int menuItemId = bottomNavigationView.getMenu().getItem(3).getItemId();
            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(menuItemId);
            badge.setNumber(qty_order);
        }
    }
    ///fillter
    private void filter(String text) {
        filteredList = new ArrayList<>();
        if (productitems.toString().equals("[]")){
            Log.d("filter",productitems.toString());
        }else {
            for (ProductItem item : productitems) {
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
    public void filterDiskon(){
        ArrayList<ProductItem> filteredList2 = new ArrayList<>();
        if (filteredList.toString().equals("[]")){
            Log.d("filter",productitems.toString());

        }else {
            for (ProductItem item : filteredList) {
                    filteredList2.add(item);
            }
            Log.d("filter",filteredList2.toString());
            productAdapter.filterList(filteredList2);
            /// Harga Diskon
            Collections.sort(filteredList, new Comparator<ProductItem>() {
                @Override
                public int compare(ProductItem lhs, ProductItem rhs) {
                    return lhs.getDiskon().compareTo(rhs.getDiskon());
                }
            });
        }

    }
    public void filterTerlaris(){
        /// Terlaris Filter
        ArrayList<ProductItem> filteredList2 = new ArrayList<>();
        if (filteredList.toString().equals("[]")){
            Log.d("filter",productitems.toString());

        }else {
            for (ProductItem item : filteredList) {
                filteredList2.add(item);
            }
            Log.d("filter",productitems.toString());
            productAdapter.filterList(filteredList2);
            Collections.sort(filteredList, new Comparator<ProductItem>() {
                @Override
                public int compare(ProductItem lhs, ProductItem rhs) {
                    return lhs.getTerjual().compareTo(rhs.getTerjual());
                }
            });
        }

    }
}