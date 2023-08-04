package com.prayerkidsstore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prayerkidsstore.AdminProduct.AdminProductAdapter;
import com.prayerkidsstore.AdminProductAdd.AdminProductAddAdapter;
import com.prayerkidsstore.AdminProductAdd.ItemModel;
import com.prayerkidsstore.AdminProductAdd.ViewPagerAdapter;
import com.prayerkidsstore.Kategori.KategoriItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminProductAddActivity extends AppCompatActivity {
    AppCompatButton Uploadbutton;
    EditText madd_namaproduk, madd_hargaproduk, madd_hargadiskon,madd_deskripsi_produk;
    CheckBox msize_36,msize_37,msize_38,msize_39,msize_40,msize_41,msize_42,msize_43,msize_44,msize_45;
    RelativeLayout PickImagebutton;
    Uri ImageUri;
    ArrayList<Uri> ChooseImageList;
    ArrayList<String> UrlsList;
    FirebaseFirestore firestore;
    StorageReference storagereference;
    FirebaseStorage mStorage;
    ProgressDialog progressDialog;

    RecyclerView mlist_add;
    /// Kategori
    ArrayList<KategoriItem> kategori_array;
    KategoriItem list_kategori;
    List<String> kategori = new ArrayList();
    Spinner mkategori_spinner;
    ProgressDialog loading;
    FirebaseFirestore firebaseFirestore, firebaseFirestore2;
    FirebaseUser user;
    String userId;
    String diskon = "";
    String harga="";
    String kategori_produk="";
    List<String> size = new ArrayList<String>();
    ImageView mbackbtn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_add);
        mbackbtn = findViewById(R.id.backbtn);
        PickImagebutton = findViewById(R.id.ChooseImage);
        mkategori_spinner = findViewById(R.id.kategori_spinner);
        madd_namaproduk = findViewById(R.id.add_namaproduk);
        madd_hargaproduk = findViewById(R.id.add_hargaproduk);
        madd_hargadiskon = findViewById(R.id.add_hargadiskon);
        madd_deskripsi_produk = findViewById(R.id.add_deskrpisi_produk);
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
        checkLogin();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Uploading Data");
        progressDialog.setMessage("Please Wait While Uploading Your data...");


        // firebase Instance untuk upload image
        firestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storagereference = mStorage.getReference();
        Uploadbutton = findViewById(R.id.UploadBtn);
        ///// untuk pilih image di gallery
        ChooseImageList = new ArrayList<>();
        UrlsList = new ArrayList<>();
        PickImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckPermission();

            }
        });
        mbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ///upload imag btn
        Uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msize_36.isChecked()){
                    size.add("36");
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
                if (ChooseImageList.size()!=0){
                    if (madd_namaproduk.length()==0){
                        madd_namaproduk.setError("Input Nama Produk");
                    }else {
                        if (madd_hargaproduk.length()==0){
                            madd_hargaproduk.setError("Input Harga");
                        }else {
                            harga= madd_hargaproduk.getText().toString();

                            if (madd_hargadiskon.length()==0){
                                diskon= "0";
                                if (madd_deskripsi_produk.length()<10){
                                    madd_deskripsi_produk.setError("Lengkapi deskripsi");
                                }else {
                                    if (size.size()==0){
                                        Log.d("Size:","kosong");

                                    }else {
                                        Log.d("Size:",String.valueOf(size.size()));
                                        size.add("Mix Size (Tulis di catatan)");
                                        UploadIMages();
                                    }
                                }
                            }else {
                                diskon= madd_hargadiskon.getText().toString();
                                if (madd_deskripsi_produk.length()<10){
                                    madd_deskripsi_produk.setError("Lengkapi deskripsi");
                                }else {
                                    if (size.size()==0){
                                        Log.d("Size:","kosong");
                                        Toast.makeText(AdminProductAddActivity.this, "Input Beberapa Size Produk", Toast.LENGTH_SHORT).show();

                                    }else {
                                        Log.d("Size:",String.valueOf(size.size()));
                                        size.add("Mix Size (Tulis di catatan)");
                                        UploadIMages();
                                    }
                                }
                            }
                        }
                    }
                }else {
                    Toast.makeText(AdminProductAddActivity.this, "Input foto product", Toast.LENGTH_SHORT).show();
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
    }
    public void checkLogin(){
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //User Sedang Login
            userId = user.getUid();
            loadKategori();
            Log.d("kondisi login","Login userId: "+userId);
        } else {
            // User Sudah Logout
            Log.d("kondisi login","Tidak Login");
            Intent gologin = new Intent(AdminProductAddActivity.this,LoginActivity.class);
            startActivity(gologin);
            finish();
        }
    }
    public void loadKategori(){
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.setCancelable(false);
        loading.show();
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
                ArrayAdapter arrayAdapter = new ArrayAdapter(AdminProductAddActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, kategori);
                arrayAdapter.notifyDataSetChanged();
                mkategori_spinner.setAdapter(arrayAdapter);
            }
        });

    }
    private void UploadIMages() {

        // we need list that images urls
        for (int i = 0; i < ChooseImageList.size(); i++) {
            Uri IndividualImage = ChooseImageList.get(i);
            if (IndividualImage != null) {
                progressDialog.show();
                StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ProductImages");
                final StorageReference ImageName = ImageFolder.child("Image" + i + ": " + IndividualImage.getLastPathSegment());
                ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UrlsList.add(String.valueOf(uri));
                                if (UrlsList.size() == ChooseImageList.size()) {
                                    StoreLinks(UrlsList);
                                }
                            }
                        });

                    }
                });
            } else {
                Toast.makeText(this, "Please fill All Field", Toast.LENGTH_SHORT).show();
            }
        }


    }
    private void StoreLinks(ArrayList<String> urlsList) {
        // now we need get text from EditText

        Map<String, Object> dataproduct = new HashMap();
        dataproduct.put("Deskripsi",madd_deskripsi_produk.getText().toString());
        dataproduct.put("Harga",harga);
        dataproduct.put("Img",UrlsList);
        dataproduct.put("Kategori",kategori_produk);
        dataproduct.put("NamaProduk",madd_namaproduk.getText().toString());
        dataproduct.put("diskon",diskon);
        dataproduct.put("size",size);
        dataproduct.put("terjual","0");
            // now we need a model class
//            ItemModel model = new ItemModel(Name, Description, "", UrlsList);
            ItemModel model = new ItemModel();
                    firestore.collection("products").add(dataproduct).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // now here we need Item id and set into model
                    loading.dismiss();
                    Toast.makeText(AdminProductAddActivity.this, "Product Berhasil Di tambahkan", Toast.LENGTH_SHORT).show();
                    onBackPressed();

                }
            });


        // if you want to clear viewpager after uploading Images
        ChooseImageList.clear();


    }
    private void CheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(AdminProductAddActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AdminProductAddActivity.this, new
                        String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            } else {
                PickImageFromgallry();
            }

        } else {
            PickImageFromgallry();
        }
    }
    private void PickImageFromgallry() {
        // here we go to gallery and select Image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                ImageUri = data.getClipData().getItemAt(i).getUri();
                ChooseImageList.add(ImageUri);
// now we need Adapter to show Images in viewpager

            }
            setAdapter();

        }else if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            ChooseImageList.add(ImageUri);
            Log.d("ImageUri",ImageUri.toString());

            setAdapter();
        }
    }
    private void setAdapter() {
        AdminProductAddAdapter adapter = new AdminProductAddAdapter(this, ChooseImageList);
        LinearLayoutManager mLayoutManager =  new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);
        mlist_add.setLayoutManager(mLayoutManager);
        mlist_add.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(AdminProductAddActivity.this, AdminProductList.class);
        startActivity(backhome);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
}