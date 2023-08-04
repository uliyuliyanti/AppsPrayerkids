package com.prayerkidsstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.prayerkidsstore.keranjang.KeranjangAdapter;
import com.prayerkidsstore.keranjang.KeranjangItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class KeranjangActivity extends AppCompatActivity {
    public static FirebaseFirestore firebaseFirestore2;
    public static Dialog formkeranjang;
    public static String catatan_kirim="";
    public static String qty_kirim="";
    public static TextView mupdate, mdelete; ;
    public static TextView mbatal_update ;
    public static String keyId="" ;
    public static int item_count=0;
    TextView mlanjut_checkout;
    BottomNavigationView bottomNavigationView;
    ImageView mback;
    public static LinearLayout mlayout_btn_lanjut;
    public static KeranjangAdapter keranjangAdapter;
    public static ArrayList<KeranjangItem>keranjangItems;
    public static KeranjangItem keranjangItem_list;
    public static RecyclerView mkeranjang_list_item;
    public static ProgressDialog loading;
    public static TextView mtotal_harga_keranjang,mtotal_items_keranjang;
    public static FirebaseFirestore firebaseFirestore;
    public static String userId = "";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);
        mlanjut_checkout = findViewById(R.id.lanjut_checkout);
        mtotal_items_keranjang = findViewById(R.id.total_items_keranjang);
        mtotal_harga_keranjang = findViewById(R.id.total_harga_keranjang);
        mlayout_btn_lanjut = findViewById(R.id.layout_btn_lanjut);
        mback = findViewById(R.id.backbtn);
        mkeranjang_list_item = findViewById(R.id.keranjang_list_item);
        checkLogin();
        checkKeranjang();
//        //Back ke Home
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //lanjut checkout

        mlanjut_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("itemcountd","Item count: "+String.valueOf(item_count));
                if (item_count<6){
                    Toast.makeText(KeranjangActivity.this, "Minimal 6 produk", Toast.LENGTH_SHORT).show();
                }else {
                    Intent backhome = new Intent(KeranjangActivity.this, PaymentCheckout.class);
                    startActivity(backhome);
                    finish();
                }

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(KeranjangActivity.this, HomeAct.class);
        startActivity(backhome);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
    public static void loadData(Context context){
        loading = new ProgressDialog(context);
        loading.setMessage("loading");
        loading.setCancelable(false);
        loading.show();
        keranjangItems = new ArrayList<>();
        keranjangItem_list = new KeranjangItem();
        // adding our array list to our recycler view adapter class.
        mkeranjang_list_item.setHasFixedSize(true);
        mkeranjang_list_item.setLayoutManager(new LinearLayoutManager(context));
        keranjangAdapter = new KeranjangAdapter(context, keranjangItems);
        mkeranjang_list_item.setAdapter(keranjangAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("KeranjangUser").document(userId).collection("ProductAdd").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        keranjangItem_list=dc.getDocument().toObject(KeranjangItem.class);
                        keranjangItem_list.setKeyId(dc.getDocument().getId());
                        keranjangItems.add(keranjangItem_list);
                        Log.d("dataproduct",dc.getDocument().getData().toString());
                    }

                    keranjangAdapter.notifyDataSetChanged();
//                    loading.dismiss();
                    if (keranjangItems!=null){
                        mlayout_btn_lanjut.setVisibility(View.VISIBLE);
                    }else {
                        mlayout_btn_lanjut.setVisibility(View.GONE);
                    }
                }
                loading.dismiss();
//                mswipe.setRefreshing(false);

            }

        });

    }
    public void checkLogin(){
        FirebaseUser user;
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //User Sedang Login
            userId = user.getUid();
            Log.d("kondisi login","Login userId: "+userId);
            loadData(this);
        } else {
            // User Sudah Logout
            Log.d("kondisi login","Tidak Login");
            Intent logoout = new Intent(KeranjangActivity.this, LoginActivity.class);
            startActivity(logoout);
            finish();
            Log.d("kondisi login","Tidak Login");

        }
    }
    public static void updateKeranjang(Context context, String skeyId, String nama_product, String qty, String Catatan){
        keyId=skeyId;
        formkeranjang = new Dialog(context);
        formkeranjang.setContentView(R.layout.form_update_to_cart);
        TextView mnama_product = formkeranjang.findViewById(R.id.nama_product);
        EditText mquantity = formkeranjang.findViewById(R.id.quantity);
        EditText mcatatan = formkeranjang.findViewById(R.id.catatan);
        mupdate = formkeranjang.findViewById(R.id.tambah_product);
        mdelete = formkeranjang.findViewById(R.id.hapus_item);
        mbatal_update = formkeranjang.findViewById(R.id.batal_tambah);
        LinearLayout mlayout_catatan = formkeranjang.findViewById(R.id.layout_catatan);
        //ubah tombol tambah produk jadi update product
        mupdate.setText("Update Produk");
        //set Default Value nama product
        mnama_product.setText(nama_product);
        //set Default Value qty
        qty_kirim=qty;
        mquantity.setText(qty);
        //set Default Value catatan
        mcatatan.setText(Catatan);
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
                        qty_kirim=mquantity.getText().toString();
                    }
//                    qty_kirim=mquantity.getText().toString();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // show title product
        mnama_product.setText(nama_product);
        //batal_update
        mbatal_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formkeranjang.dismiss();
            }
        });
        //submit_Update
        mupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mupdate.setText("Loading...");
                if (mquantity.length()==0){
                    mquantity.setError("Input quantity");
                    mupdate.setText("Update Produk");
                }else {
                        if (Catatan.equals("")){
                            updateFirestore_Keranjang(context);
                        }else {
                            if (mcatatan.length()<5){
                                mcatatan.setError("Input Catatan Lengkap");
                                mupdate.setText("Tambah Produk");
                            }else {
                                catatan_kirim=mcatatan.getText().toString();
//                            Toast.makeText(context, qty_kirim+catatan_kirim, Toast.LENGTH_SHORT).show();
                                updateFirestore_Keranjang(context);
                            }
                        }



                }

            }
        });
        //mhapus item
        mdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdelete.setEnabled(false);
                firebaseFirestore2=FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore2.collection("KeranjangUser").document(userId).collection("ProductAdd").document(keyId);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loadData(context);
//                Toast.makeText(context, "Update Berhasil", Toast.LENGTH_SHORT).show();
                        Log.d("UpdateRespon: ","Product Dihapus");
                        Log.d("hapus","KeranjangUser/"+userId+"/ProducsAdd/"+keyId);
                        mdelete.setEnabled(true);
                        formkeranjang.dismiss();
                        mupdate.setText("Update Produk");
                        mupdate.setEnabled(true);
//                        Log.d("keranjang-item",String.valueOf(keranjangItem_list.getSize()));
                        if (keranjangItem_list.getSize()!=null){
                            mlayout_btn_lanjut.setVisibility(View.VISIBLE);
                        }else {
                            mlayout_btn_lanjut.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mdelete.setEnabled(true);
                        Toast.makeText(context, "Product gagl di Hapus", Toast.LENGTH_SHORT).show();
                        Log.d("UpdateRespon: ",e.toString());
                        formkeranjang.dismiss();
                        mupdate.setText("Update Produk");
                        mupdate.setEnabled(true);
                    }
                });
            }

        });
        formkeranjang.show();

    }
    public static void updateFirestore_Keranjang(Context context){
        item_count=0;
        firebaseFirestore2=FirebaseFirestore.getInstance();
        Map<String, Object> dataproduct = new HashMap();
        dataproduct.put("Qty",qty_kirim);
        dataproduct.put("Catatan",catatan_kirim);
        DocumentReference documentReference = firebaseFirestore2.collection("KeranjangUser").document(userId).collection("ProductAdd").document(keyId);
        documentReference.update(dataproduct).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                loadData(context);
//                Toast.makeText(context, "Update Berhasil", Toast.LENGTH_SHORT).show();
                Log.d("UpdateRespon: ","Product Berhasil di tambahkan");
                formkeranjang.dismiss();
                mupdate.setText("Update Produk");
                mupdate.setEnabled(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Product gagl di tambahkan", Toast.LENGTH_SHORT).show();
                Log.d("UpdateRespon: ",e.toString());
                formkeranjang.dismiss();
                mupdate.setText("Update Produk");
                mupdate.setEnabled(true);
            }
        });
    }
    public void checkKeranjang(){
        SharedPreferences sharedPreferences = getSharedPreferences("CHEK_KERANJANG",MODE_PRIVATE);
        int qty_keranjang = sharedPreferences.getInt("keranjang_list", 0);
        if (qty_keranjang==0){
            mlayout_btn_lanjut.setVisibility(View.GONE);
        }else {
            mlayout_btn_lanjut.setVisibility(View.VISIBLE);
        }

    }
}