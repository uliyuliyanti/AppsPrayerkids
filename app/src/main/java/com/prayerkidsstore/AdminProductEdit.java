package com.prayerkidsstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.prayerkidsstore.AdminProductAdd.AdminProductAddAdapter;
import com.prayerkidsstore.AdminProductAdd.ItemModel;
import com.prayerkidsstore.Kategori.KategoriItem;
import com.prayerkidsstore.products.Detail_Product_IMG_Adapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminProductEdit extends AppCompatActivity {
    ProgressDialog loading;
    FirebaseFirestore firebaseFirestore;
    String keyId = "";
    String userId="";
    EditText medit_namaproduk, medit_hargaproduk, medit_hargadiskon,medit_deskripsi_produk;
    Spinner mkategori_spinner;
    RecyclerView mlist_add;
    List<String> msize = new ArrayList<>();
    List<String> size = new ArrayList<>();
    String kategori_value="";
    /// Kategori
    ArrayList<KategoriItem> kategori_array;
    KategoriItem list_kategori;
    List<String> kategori = new ArrayList();
    //// check box size
    CheckBox msize_36,msize_37,msize_38,msize_39,msize_40,msize_41,msize_42,msize_43,msize_44,msize_45;
    AppCompatButton UpdateButton;
    String harga="";
    String diskon="";
    String kategori_produk="";
    int pos=0;
    Dialog hapus_dialog;
    TextView mbatal_hapus, mhapus;
    ImageView mdelete_btn,mbackbtn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_edit);
        mdelete_btn = findViewById(R.id.delete_btn);
        medit_namaproduk = findViewById(R.id.edit_namaproduk);
        mbackbtn = findViewById(R.id.backbtn);
        medit_deskripsi_produk = findViewById(R.id.edit_deskrpisi_produk);
        medit_hargadiskon = findViewById(R.id.edit_hargadiskon);
        medit_hargaproduk = findViewById(R.id.edit_hargaproduk);
        mkategori_spinner = findViewById(R.id.kategori_spinner);
        msize_36 = findViewById(R.id.size_36);
        msize_37 = findViewById(R.id.size_37);
        msize_38 = findViewById(R.id.size_38);
        msize_39 = findViewById(R.id.size_39);
        msize_40 = findViewById(R.id.size_40);
        msize_41 = findViewById(R.id.size_41);
        msize_42 = findViewById(R.id.size_42);
        msize_43 = findViewById(R.id.size_43);
        msize_44 = findViewById(R.id.size_44);
        msize_45 = findViewById(R.id.size_45);
        mlist_add = findViewById(R.id.list_add);
        UpdateButton = findViewById(R.id.Update_data);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            keyId = extras.getString("keyId");
            Log.d("keyId",keyId);
            loadData();
        }
        checkLogin();
        ///upload imag btn
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msize_36.isChecked()){
                    size.add("36");
                }
                if (msize_37.isChecked()){
                    size.add("37");
                }
                if (msize_38.isChecked()){
                    size.add("38");
                }
                if (msize_39.isChecked()){
                    size.add("39");
                }
                if (msize_40.isChecked()){
                    size.add("40");
                }
                if (msize_41.isChecked()){
                    size.add("41");
                }
                if (msize_42.isChecked()){
                    size.add("42");
                }
                if (msize_43.isChecked()){
                    size.add("43");
                }
                if (msize_44.isChecked()){
                    size.add("44");
                }
                if (msize_45.isChecked()){
                    size.add("45");
                }
                if (medit_namaproduk.length()==0){
                    medit_namaproduk.setError("Input Nama Produk");
                }else {
                    if (medit_hargaproduk.length()==0){
                        medit_hargaproduk.setError("Input Harga");
                    }else {
                        harga= medit_hargaproduk.getText().toString();

                        if (medit_hargadiskon.length()==0){
                            diskon= "0";
                            if (medit_deskripsi_produk.length()<10){
                                medit_deskripsi_produk.setError("Lengkapi deskripsi");
                            }else {
                                if (size.size()==0){
                                    Log.d("Size:","kosong");

                                }else {
                                    Log.d("Size:",String.valueOf(size.size()));
                                    size.add("Mix Size (Tulis di catatan)");
                                    UpdateData();
                                }
                            }
                        }else {
                            diskon= medit_hargadiskon.getText().toString();
                            if (medit_deskripsi_produk.length()<10){
                                medit_deskripsi_produk.setError("Lengkapi deskripsi");
                            }else {
                                if (size.size()==0){
                                    Log.d("Size:","kosong");
                                    Toast.makeText(AdminProductEdit.this, "Input Beberapa Size Produk", Toast.LENGTH_SHORT).show();

                                }else {
                                    Log.d("Size:",String.valueOf(size.size()));
                                    size.add("Mix Size (Tulis di catatan)");
                                    UpdateData();
                                }
                            }
                        }
                    }
                }


//
            }
        });
        mkategori_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kategori_produk= parent.getItemAtPosition(position).toString();
                Log.d("kategori:",kategori_produk);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mdelete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapusProduk();
            }
        });
        mbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void hapusProduk(){
//        Toast.makeText(this, "Add To Cart", Toast.LENGTH_SHORT).show();
        hapus_dialog = new Dialog(this);
        hapus_dialog.setContentView(R.layout.delete_produk);
        mbatal_hapus = hapus_dialog.findViewById(R.id.batalhapus);
        mhapus = hapus_dialog.findViewById(R.id.hapusproduk);
        mhapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading = new ProgressDialog(AdminProductEdit.this);
                loading.setMessage("loading");
                loading.setCancelable(false);
                mdelete_btn.setEnabled(false);
                firebaseFirestore=FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore.collection("products").document(keyId);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loading.dismiss();
                        Toast.makeText(AdminProductEdit.this, "Hapus Berhasil", Toast.LENGTH_SHORT).show();
                        Log.d("UpdateRespon: ","Product Dihapus");
                        Log.d("hapus","KeranjangUser/"+userId+"/ProducsAdd/"+keyId);
                        mdelete_btn.setEnabled(true);
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismiss();
                        mdelete_btn.setEnabled(true);
                        Toast.makeText(AdminProductEdit.this, "Product gagl di Hapus", Toast.LENGTH_SHORT).show();
                        Log.d("UpdateRespon: ",e.toString());

                    }
                });
            }
        });
        mbatal_hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapus_dialog.dismiss();
            }
        });
        //qty minimal 6

        hapus_dialog.show();


    }
    public void UpdateData(){
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.setCancelable(false);
        Map<String, Object> dataproduct = new HashMap();
        dataproduct.put("Deskripsi",medit_deskripsi_produk.getText().toString());
        dataproduct.put("Harga",harga);
        dataproduct.put("Kategori",kategori_produk);
        dataproduct.put("NamaProduk",medit_namaproduk.getText().toString());
        dataproduct.put("diskon",diskon);
        dataproduct.put("size",size);

            // now we need a model class
//            ItemModel model = new ItemModel(Name, Description, "", UrlsList);
            ItemModel model = new ItemModel();
            firebaseFirestore.collection("products").document(keyId).update(dataproduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    loading.dismiss();
                    Toast.makeText(AdminProductEdit.this, "Update Berhasil", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loading.dismiss();
                    Toast.makeText(AdminProductEdit.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });

    }
    public void checkLogin(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            userId = user.getUid();
            Log.d("kondisi login ","Login userId: "+userId);
            loadData();
            loadKategori();
        } else {
            // No user is signed in
            Intent login_page=new Intent(AdminProductEdit.this,LoginActivity.class);
            startActivity(login_page);
            finish();
            Log.d("kondisi login :","Tidak Login");
//            Toast.makeText(this, "Mohon maaf untuk transaksi login terlebih  dahulu", Toast.LENGTH_LONG).show();
        }
    }
    public void loadKategori(){
        kategori_array= new ArrayList<KategoriItem>();
        list_kategori = new KategoriItem();
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
                    if (kategori_array.get(i).getKategori().equals(kategori_value)){
                        pos=i;
                    }
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(AdminProductEdit.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, kategori);
                arrayAdapter.notifyDataSetChanged();
                mkategori_spinner.setAdapter(arrayAdapter);
                mkategori_spinner.setSelection(pos);
            }
        });

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
                    medit_namaproduk.setText(snapshot.get("NamaProduk").toString());
                    medit_hargadiskon.setText(snapshot.get("diskon").toString());
                    medit_hargaproduk.setText(snapshot.get("Harga").toString());
                    medit_deskripsi_produk.setText(snapshot.get("Deskripsi").toString());
                    msize= (ArrayList<String>) snapshot.get("size");
                    kategori_value = snapshot.get("Kategori").toString();
                    for (int i = 0; i < msize.size(); ++i){
                        if (msize.get(i).equals("36")){
                            msize_36.setChecked(true);
                        }
                        if (msize.get(i).equals("37")){
                            msize_37.setChecked(true);
                        }
                        if (msize.get(i).equals("38")){
                            msize_38.setChecked(true);
                        }
                        if (msize.get(i).equals("39")){
                            msize_39.setChecked(true);
                        }
                        if (msize.get(i).equals("40")){
                            msize_40.setChecked(true);
                        }
                        if (msize.get(i).equals("41")){
                            msize_41.setChecked(true);
                        }
                        if (msize.get(i).equals("42")){
                            msize_42.setChecked(true);
                        }
                        if (msize.get(i).equals("43")){
                            msize_43.setChecked(true);
                        }
                        if (msize.get(i).equals("44")){
                            msize_44.setChecked(true);
                        }
                        if (msize.get(i).equals("45")){
                            msize_45.setChecked(true);
                        }
                    }

                } else {
                    loading.dismiss();
                    Log.d("snpashot: ", "Current data: null");
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(this, AdminProductList.class);
        startActivity(backhome);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
}