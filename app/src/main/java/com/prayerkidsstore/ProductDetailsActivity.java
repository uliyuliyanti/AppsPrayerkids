package com.prayerkidsstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.prayerkidsstore.keranjang.KeranjangAdapter;
import com.prayerkidsstore.keranjang.KeranjangItem;
import com.prayerkidsstore.products.Detail_Product_IMG_Adapter;
import com.prayerkidsstore.products.ProductAdapter;
import com.prayerkidsstore.products.ProductItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    ImageView mbackbtn;
    String keyId="";
    FirebaseFirestore firebaseFirestore;
    TextView mnama_product_detail,mdiskon_product_detail,mharga_product_detail,mdeskripsi;
    ViewPager mviewPagerMain;
    TabLayout mtab;
    Detail_Product_IMG_Adapter detail_product_img_adapter;
    Spinner msize_sepatu;
    List<String>  msize= new ArrayList<String>();
    String size_sepatu_select="";
    Boolean login=false;
    String userId="";
    TextView madd_to_cart;
    ProgressDialog loading;
    //Tambah product
    Dialog formproduk;
    EditText mquantity,mcatatan;
    TextView mbatal_tambah,mtambah_product,mnama_product;
    LinearLayout mlayout_catatan;
    String vnama_product,vqty,vharga,vdiskon,vkategori,vimg;
    ///set keranjang item
    ArrayList<KeranjangItem> keranjangItems;
    KeranjangItem keranjangItem;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        mbackbtn = findViewById(R.id.backbtn);
        mdiskon_product_detail = findViewById(R.id.diskon_product_detail);
        mharga_product_detail = findViewById(R.id.harga_product_detail);
        mnama_product_detail = findViewById(R.id.nama_product_detail);
        mdeskripsi = findViewById(R.id.deskripsi);
        mviewPagerMain = findViewById(R.id.viewPagerMain);
        msize_sepatu = findViewById(R.id.size_sepatu);
        madd_to_cart = findViewById(R.id.add_to_cart);
        mtab = findViewById(R.id.tab_layout);
        //Mengambil keyId Product Kemudian Load Data Product
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            keyId = extras.getString("keyId");
            Log.d("keyId",keyId);
            loadData();
        }
        mbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Mengambil Value Size Sepatu
        msize_sepatu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                size_sepatu_select=msize.get(position);
//                Toast.makeText(ProductDetailsActivity.this, size_sepatu_select, Toast.LENGTH_SHORT).show();
                Log.d("Select_Size",size_sepatu_select);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Masukan Produk ke Keranjang, Hanya dalam Ke adaan Login,  Jika belum Login Lemapr ke page Login
        madd_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(this, ProductActivity.class);
        startActivity(backhome);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
    public void loadData(){
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.setCancelable(false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firebaseFirestore.collection("products").document(keyId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
//                    loading.dismiss();
                    Log.w("snpashot: ", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("snpashot: ", "Current data: " + snapshot.getData());
                    snapshot.get("UserName");
                    mnama_product_detail.setText(snapshot.get("NamaProduk").toString());
                    //set Harga Produk
                    double harga_normal= Double.parseDouble(snapshot.get("Harga").toString());
                    String harga = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(harga_normal);
                    mharga_product_detail.setText(harga);
                    if (Integer.parseInt(snapshot.get("diskon").toString())==0){
                        //jika diskon harga == 0 maka text harga normalt dan text diskon di hide dan text harga di ubah jadi warna Hitam
                       mdiskon_product_detail.setVisibility(View.GONE);
                       mharga_product_detail.setTextColor(Color.parseColor("#000000"));
                    }else {
                        //jika diskon harga tidak == 0 maka text harga di coret dan text diskon di show
                        mdiskon_product_detail.setVisibility(View.VISIBLE);
                        mharga_product_detail.setTextColor(Color.parseColor("#919191"));
                        if (!  mharga_product_detail.getPaint().isStrikeThruText()) {
                            mharga_product_detail.setPaintFlags(  mharga_product_detail.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else {
                            mharga_product_detail.setPaintFlags(  mharga_product_detail.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        //set Harga Diskon
                        double harga_diskon= Double.parseDouble((snapshot.get("diskon").toString()));
                        String diskon = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(harga_diskon);
                       mdiskon_product_detail.setText(diskon);
                    }
                    //set Deskripsi
                    if (snapshot.get("Deskripsi").toString()!=null){
                        mdeskripsi.setText(snapshot.get("Deskripsi").toString());
                    }
                    //set Slide Image
                    ArrayList<String>  mStringList= new ArrayList<String>();
                    mStringList= (ArrayList<String>) snapshot.get("Img");
                    String[] mStringArray = new String[mStringList.size()];
                    mStringArray = mStringList.toArray(mStringArray);
                    detail_product_img_adapter = new Detail_Product_IMG_Adapter(ProductDetailsActivity.this, mStringArray);
                    mviewPagerMain.setAdapter(detail_product_img_adapter);
                    //set Indicator image
                    mtab.setupWithViewPager(mviewPagerMain, true);
                    //Set Size Sepatu
                    msize= (ArrayList<String>) snapshot.get("size");
                    ArrayAdapter arrayAdapter = new ArrayAdapter(ProductDetailsActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, msize);
                    arrayAdapter.notifyDataSetChanged();
                    msize_sepatu.setAdapter(arrayAdapter);
                    loading.dismiss();
                    ///set value untuk keranjang
                    vdiskon = snapshot.get("diskon").toString();
                    vharga = snapshot.get("Harga").toString();
                    vimg = mStringList.get(0);
                    vnama_product =  snapshot.get("NamaProduk").toString();
                    vkategori = snapshot.get("Kategori").toString();
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
            login=true;
            userId = user.getUid();
            Log.d("kondisi login ","Login userId: "+userId);
            addToCart();
        } else {
            // No user is signed in
            login=false;
            Intent login_page=new Intent(ProductDetailsActivity.this,LoginActivity.class);
            startActivity(login_page);
            finish();
            Log.d("kondisi login :","Tidak Login");
            Toast.makeText(this, "Mohon maaf untuk transaksi login terlebih  dahulu", Toast.LENGTH_LONG).show();
        }


    }
    public void addToCart(){
//        Toast.makeText(this, "Add To Cart", Toast.LENGTH_SHORT).show();
        formproduk = new Dialog(this);
        formproduk.setContentView(R.layout.form_add_to_cart);
        mnama_product = formproduk.findViewById(R.id.nama_product);
        mquantity = formproduk.findViewById(R.id.quantity);
        mcatatan = formproduk.findViewById(R.id.catatan);
        mtambah_product = formproduk.findViewById(R.id.tambah_product);
        mbatal_tambah = formproduk.findViewById(R.id.batal_tambah);
        mlayout_catatan = formproduk.findViewById(R.id.layout_catatan);
        //qty minimal 6
        mquantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mquantity.length()==0){

                }else {
                    if (Integer.parseInt(mquantity.getText().toString())<3){
                        mquantity.setText("3");
                    }else {

                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // show title product
        mnama_product.setText(vnama_product);
        //batal_update
        mbatal_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formproduk.dismiss();
            }
        });
        //submit_Update
        mtambah_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtambah_product.setText("Loading...");
                if (mquantity.length()==0){
                    mquantity.setError("Input quantity");
                    mtambah_product.setText("Tambah Produk");
                }else {

                    if (size_sepatu_select.length()>2){
                        if (mcatatan.length()<5){
                            mcatatan.setError("Input Catatan Lengkap");
                            mtambah_product.setText("Tambah Produk");
                        }else {
                            updateFirestore_Keranjang();
                        }
                    }else {
                        updateFirestore_Keranjang();
                    }

                }


            }
        });

        formproduk.show();


    }
    public void updateFirestore_Keranjang(){

        Map<String, Object> dataproduct = new HashMap();
        dataproduct.put("NamaProduct",vnama_product);
        dataproduct.put("Kategori",vkategori);
        dataproduct.put("Harga",vharga);
        dataproduct.put("Diskon",vdiskon);
        dataproduct.put("Qty",mquantity.getText().toString());
        dataproduct.put("Img",vimg);
        dataproduct.put("size",size_sepatu_select);
        dataproduct.put("Catatan",mcatatan.getText().toString());
        DocumentReference documentReference = firebaseFirestore.collection("KeranjangUser").document(userId).collection("ProductAdd").document(keyId);
        documentReference.set(dataproduct).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ProductDetailsActivity.this, vnama_product+" Berhasil di tambahkan", Toast.LENGTH_SHORT).show();
                Log.d("UpdateRespon: ","Product Berhasil di tambahkan");
                formproduk.dismiss();
                mtambah_product.setText("Tambah Produk");
                mtambah_product.setEnabled(true);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductDetailsActivity.this, "Product gagl di tambahkan", Toast.LENGTH_SHORT).show();
                Log.d("UpdateRespon: ",e.toString());
                formproduk.dismiss();
                mtambah_product.setText("Tambah Produk");
                mtambah_product.setEnabled(true);
            }
        });
    }
}
