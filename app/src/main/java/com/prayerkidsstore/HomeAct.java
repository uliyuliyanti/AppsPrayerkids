package com.prayerkidsstore;



import static android.view.View.GONE;

import static com.prayerkidsstore.apihelper.ServiceGenerator.fcmbase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.prayerkidsstore.Kategori.KategoriItem;
import com.prayerkidsstore.Order.OrderItem;
import com.prayerkidsstore.apihelper.IRetrofit;
import com.prayerkidsstore.apihelper.ServiceGenerator;
import com.prayerkidsstore.kategorihome.KategoriHomeAdapter;
import com.prayerkidsstore.kategorihome.KategoriHomeItem;
import com.prayerkidsstore.keranjang.KeranjangItem;
import com.prayerkidsstore.productHome.Banner_Adapter;
import com.prayerkidsstore.productHome.ProductHomeAdapter;
import com.prayerkidsstore.productHome.ProductHomeItem;
import com.prayerkidsstore.producthome_diskon.ProductHomeAdapterDiskon;
import com.prayerkidsstore.producthome_diskon.ProductHomeItemDiskon;
import com.prayerkidsstore.products.Detail_Product_IMG_Adapter;
import com.prayerkidsstore.products.ProductAdapter;
import com.prayerkidsstore.products.ProductItem;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeAct extends AppCompatActivity {
    Boolean exit = false;
    SwipeRefreshLayout mswipe;
    LinearLayout msearch_home,msee_product,msee_kategori_1;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Boolean login=false;
    String userId="";
    int qty_keranjang=0;
    int qty_order=0;
    ProgressDialog loading;
    ///Load Product
    ArrayList<ProductHomeItem>productitems;
    ArrayList<ProductHomeItemDiskon>productitems2;
    ArrayList<ProductHomeItemDiskon>filteredList;
    ProductHomeItem productItem;
    ProductHomeItemDiskon productItem2;
    ProductHomeAdapter productAdapter;
    ProductHomeAdapterDiskon productAdapter2;
    RecyclerView mall_product_home,mdiskon_product_home,mkategori_home;
    ///banner
    Banner_Adapter banner_adapter;
    ViewPager mmbanner;
    TabLayout mtab;
    ///load kategori
    ArrayList<KategoriHomeItem> kategoriItems;
    KategoriHomeItem kategoriItem;
    KategoriHomeAdapter kategoriHomeAdapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        msee_kategori_1 = findViewById(R.id.see_kategori_1);
        msearch_home = findViewById(R.id.search_home);
        msee_product = findViewById(R.id.see_product);
        mmbanner = findViewById(R.id.banner_slide);
        mtab = findViewById(R.id.tab_layout);
        mswipe = findViewById(R.id.swipe);
        mkategori_home = findViewById(R.id.kategori_home);
        mdiskon_product_home = findViewById(R.id.diskon_product_home);
        mall_product_home = findViewById(R.id.all_product_home);
        //check Session Login
        checkLogin();
        productLoad();
        loadKategori();
        //setting menu bottom
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    return true;
                case R.id.bottom_produk:
                    startActivity(new Intent(getApplicationContext(), ProductActivity.class));
                    this.overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.bottom_cart:
                    startActivity(new Intent(getApplicationContext(), KeranjangActivity.class));
                    this. overridePendingTransition(R.anim.right_in, R.anim.left_out);
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

        ////search btnvnvnvn
        msearch_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProductActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkLogin();
                productLoad();
                loadKategori();
            }
        });
        msee_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProductActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });
        msee_kategori_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goproduk = new Intent(HomeAct.this,ProductActivity.class);
                goproduk.putExtra("get_kategori","Kecot Series");
                startActivity(goproduk);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    public void checkLogin(){
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.setCancelable(false);
        loading.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            login=true;
            userId = user.getUid();
            checkKeranjang();

            Log.d("kondisi login","Login userId: "+userId);
        } else {
            // No user is signed in
            login=false;
            Log.d("kondisi login","Tidak Login");
        }


    }
    public void checkKeranjang(){
        //RESEST jUMLAH kERANJANG iTEM
        SharedPreferences sharedPreferences = getSharedPreferences("CHEK_KERANJANG", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("keranjang_list", 0);
        editor.apply();
        ArrayList<KeranjangItem>keranjangItems;
        keranjangItems=new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("KeranjangUser").document(userId).collection("ProductAdd").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error !=null){
                    Log.e("Fire Store Error",error.getMessage());
                    return;

                }
                KeranjangItem listp = new KeranjangItem();
                for (DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        listp=dc.getDocument().toObject(KeranjangItem.class);
                        keranjangItems.add(listp);
                        qty_keranjang = keranjangItems.size();
                        SharedPreferences sharedPreferences = getSharedPreferences("CHEK_KERANJANG", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("keranjang_list", qty_keranjang);
                        editor.apply();
                        int menuItemId = bottomNavigationView.getMenu().getItem(2).getItemId();
                        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(menuItemId);
                        badge.setNumber(qty_keranjang);
                    }

//                    Log.d("qty_keranjng",qty_keranjang);
                }
                loadOrderQty();
            }
        });
//        Log.d("qty_keranjng",qty_keranjang);

    }
    ///fillter diskon
    private void filter(String text) {
        filteredList = new ArrayList<>();
        if (productitems2.toString().equals("[]")){
            Log.d("filter",productitems2.toString());
        }else {
            for (ProductHomeItemDiskon item : productitems2) {
                if (item.getNamaProduk().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }else if (item.getKategori().toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(item);
                }
            }
            Log.d("filter",productitems.toString());
            productAdapter2.filterList(filteredList);
        }
    }
    public void productLoad(){
        productitems = new ArrayList<>();
        productItem = new ProductHomeItem();
        // adding our array list to our recycler view adapter class.
        mall_product_home.setHasFixedSize(true);
        mall_product_home.setLayoutManager(new GridLayoutManager(this,2));
        productAdapter = new ProductHomeAdapter(this, productitems);
        mall_product_home.setAdapter(productAdapter);
        ///diskon product
        productitems2 = new ArrayList<>();
        mdiskon_product_home.setHasFixedSize(true);
        mdiskon_product_home.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        productAdapter2 = new ProductHomeAdapterDiskon(this, productitems2);
        mdiskon_product_home.setAdapter(productAdapter2);
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
                        productItem=dc.getDocument().toObject(ProductHomeItem.class);
                        productItem.setKeyId(dc.getDocument().getId());
                        productItem2=dc.getDocument().toObject(ProductHomeItemDiskon.class);
                        productItem2.setKeyId(dc.getDocument().getId());
                        productitems.add(productItem);
                        productitems2.add(productItem2);
                        Log.d("dataproduct",dc.getDocument().getData().toString());
                    }
                    productAdapter.notifyDataSetChanged();
                    filter("kecot");

                }
                loadBanners();
                mswipe.setRefreshing(false);

            }

        });
    }
    public void loadBanners(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firebaseFirestore.collection("bannerhome").document("bannerlist");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<String>  mStringList= new ArrayList<String>();
                mStringList= (ArrayList<String>) value.get("listbanner");
                String[] mStringArray = new String[mStringList.size()];
                mStringArray = mStringList.toArray(mStringArray);
                banner_adapter = new Banner_Adapter(HomeAct.this, mStringArray);
                mmbanner.setAdapter(banner_adapter);
                mtab.setupWithViewPager(mmbanner, true);
                Log.d("banner_list",mStringList.get(0).toString());

            }
        });

    }
    public void loadOrderQty(){
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
                    qty_order=Integer.parseInt(snapshot.get("OrderBadge").toString());
                    SharedPreferences sharedPreferences = getSharedPreferences("CHEK_KERANJANG", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("order_qty", qty_order);
                    editor.apply();
                    if (qty_order==0){

                    }else {
                        int menuItemId = bottomNavigationView.getMenu().getItem(3).getItemId();
                        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(menuItemId);
                        badge.setNumber(qty_order);
                    }

                } else {
                    loading.dismiss();
                    Log.d("snpashot: ", "Current data: null");
                }
            }
        });
    }
    public void loadKategori(){
        kategoriItems = new ArrayList<>();
        kategoriItem = new KategoriHomeItem();
        kategoriHomeAdapter = new KategoriHomeAdapter(HomeAct.this,kategoriItems);
        mkategori_home.setHasFixedSize(true);
        mkategori_home.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Kategori_Product").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error !=null){
                    Log.e("Fire Store Error",error.getMessage());
                    return;

                }
                for (DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        kategoriItem=dc.getDocument().toObject(KategoriHomeItem.class);
                        kategoriItems.add(kategoriItem);
                        Log.d("dataproduct",dc.getDocument().getData().toString());
                    }

                    kategoriHomeAdapter = new KategoriHomeAdapter(HomeAct.this, kategoriItems);
                    mkategori_home.setAdapter(kategoriHomeAdapter);
                    loading.dismiss();
                }
                mswipe.setRefreshing(false);

            }

        });

    }
    public void onBackPressed(){
        if (exit) {
            this.finish();
        } else {
            Toast.makeText(this, "Click Sekali Lagi Untuk Keluar",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }



    }
//    public void CheckOrder(){
//        //RESEST jUMLAH Order
//        SharedPreferences sharedPreferences = getSharedPreferences("ORDER_LIST", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt("order_list", 0);
//        editor.apply();
//        ArrayList<OrderItem>orderItems;
//        orderItems=new ArrayList<>();
//        firebaseFirestore = FirebaseFirestore.getInstance();
//        firebaseFirestore.collection("OrderPk").document(userId).collection("OrderList").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error !=null){
//                    Log.e("Fire Store Error",error.getMessage());
//                    return;
//
//                }
//                OrderItem listp = new OrderItem();
//                for (DocumentChange dc : value.getDocumentChanges()){
//                    if(dc.getType() == DocumentChange.Type.ADDED){
//                        listp=dc.getDocument().toObject(OrderItem.class);
//                        orderItems.add(listp);
//
//                        qty_order = orderItems.size();
//                        SharedPreferences sharedPreferences = getSharedPreferences("ORDER_LIST", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putInt("order_list", qty_order);
//                        editor.apply();
//                        int menuItemId = bottomNavigationView.getMenu().getItem(3).getItemId();
//                        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(menuItemId);
//                        badge.setNumber(qty_order);
//                    }
//
////                    Log.d("qty_keranjng",qty_keranjang);
//                }
//            }
//        });
////        Log.d("qty_keranjng",qty_keranjang);
//
//    }
}