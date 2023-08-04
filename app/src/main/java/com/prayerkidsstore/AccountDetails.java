package com.prayerkidsstore;

import static com.prayerkidsstore.apihelper.ServiceGenerator.baseurl;
import static com.prayerkidsstore.checkout.CheckoutAdapter.harga_total_plus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.prayerkidsstore.apihelper.IRetrofit;
import com.prayerkidsstore.apihelper.ServiceGenerator;
import com.prayerkidsstore.ongkir.ResultItem;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountDetails extends AppCompatActivity {
    ImageView mback;
    FirebaseUser user;
    String userId;
    FirebaseFirestore firebaseFirestore,firebaseFirestore3;
    TextView mkecamatan,mnamalengkap,memailuser,malamatlengkap,mkabupaten,mprovinsi,mkodepos,mnohp;
    EditText meditalamatlengkap,meditkodepos,meditnohp;
    ProgressDialog loading;
    LinearLayout meditNamaEmail,meditAlamatKirim;
    //Edit nama dan email
    Dialog formEmail;
    EditText mnamaedit,memailedit,mpasswordemail;
    TextView mbatal_email_nama,msimpan_email_nama;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    //edit alamat Kirim
    Dialog formAlamat;
    Spinner meditkabupaten,meditprovinsi,meditkecamatan;
    TextView mbatal_alamat,msimpan_alamat;
    String Kecamatan="";
    String Kabupaten="";
    String Provinsi="";

    ////provinsi data
    List<String> provinsiList= new ArrayList<>();
    List<String> provinsiListId= new ArrayList<>();
    JsonArray provinsi_list;
    String provinsiId="";
    ////Kabupaten data
    List<String> kabupatenList= new ArrayList<>();
    List<String> kabupatenListId= new ArrayList<>();
    JsonArray kabupaten_list;
    String addkabupatenId="";
    String addKabupatenname="";
    String kabupatenId="";
    ////Kecamatan data
    List<String> kecamatanList= new ArrayList<>();
    List<String> kecamatanListId= new ArrayList<>();
    JsonArray kecamatan_list;
    String kecamatanId="";
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        mback = findViewById(R.id.backbtn);
        mnamalengkap = findViewById(R.id.namalengkap);
        memailuser = findViewById(R.id.emailuser);
        malamatlengkap = findViewById(R.id.alamatlengkap);
        mkabupaten = findViewById(R.id.kabupaten);
        mprovinsi = findViewById(R.id.provinsi);
        mkodepos = findViewById(R.id.kodepos);
        mnohp = findViewById(R.id.nohp);
        meditNamaEmail = findViewById(R.id.editNamaEmail);
        meditAlamatKirim = findViewById(R.id.editAlamatKirim);
        mkecamatan = findViewById(R.id.kecamatan);
        //CheckLogin
        checkLogin();
        //LoadData Realtime FireStore
        loadData();
        /////

        //Tombol Kembali Atas Kiri
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Edit Email & Nama
        meditNamaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formEditEmail();
            }
        });
        //Edit Alamat Kirim
        meditAlamatKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formEditAlamat();
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(this, AccountActivity.class);
        startActivity(backhome);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
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
            Intent logoout = new Intent(AccountDetails.this, LoginActivity.class);
            startActivity(logoout);
            finish();
        }
    }
    public void loadData(){
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
                    snapshot.get("UserName");
                    mnamalengkap.setText(snapshot.get("UserName").toString());
                    memailuser.setText(snapshot.get("EmailUser").toString());
                    mkabupaten.setText(snapshot.get("Kabupaten").toString());
                    mprovinsi.setText(snapshot.get("Provinsi").toString());
                    mkodepos.setText(snapshot.get("KodePos").toString());
                    mnohp.setText(snapshot.get("NoHp").toString());
                    malamatlengkap.setText(snapshot.get("AlamatLengkap").toString());
                    mkecamatan.setText(snapshot.get("Kecamatan").toString());

                    ///load data provinsi
                    loadProvinsi();
                } else {
                    loading.dismiss();
                    Log.d("snpashot: ", "Current data: null");
                }
            }
        });
    }
    public void formEditEmail(){
        formEmail = new Dialog(this);
        formEmail.setContentView(R.layout.formemail);

        mnamaedit = formEmail.findViewById(R.id.namaedit);
        memailedit = formEmail.findViewById(R.id.emailedit);
        mpasswordemail = formEmail.findViewById(R.id.passwordemail);
        msimpan_email_nama = formEmail.findViewById(R.id.simpan_email_nama);
        mbatal_email_nama = formEmail.findViewById(R.id.batal_email_nama);
        //batal_update
        mbatal_email_nama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formEmail.dismiss();
            }
        });
        //submit_Update
        msimpan_email_nama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mnamaedit.length()<2){
                    mnamaedit.setError("Nama Tidak Boleh Kosong");
                }else {
                    if (memailedit.getText().toString().matches(emailPattern)){
                        //mengupdate Email Login
                        msimpan_email_nama.setText("Loading...");
                        msimpan_email_nama.setEnabled(false);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(memailuser.getText().toString(), mpasswordemail.getText().toString());
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Log.d("AuthUlang :", "Berhasil Auth Ulang");
                                            //jika berhasil auth ulang baru update email
                                            user.updateEmail(memailedit.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d("Update-Email-Login: ", "User profile updated.");
                                                                //JIKA Email Login Sudah Terupdate Maka Lanjut Update Data Email User
                                                                Map<String, Object> dataEmailNama = new HashMap();
                                                                dataEmailNama.put("UserName",mnamaedit.getText().toString());
                                                                dataEmailNama.put("EmailUser",memailedit.getText().toString());
                                                                DocumentReference documentReference = firebaseFirestore.collection("UserPk").document(userId);
                                                                documentReference.update(dataEmailNama).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(AccountDetails.this, "Update Berhasil", Toast.LENGTH_SHORT).show();
                                                                        Log.d("UpdateRespon: ","Update Berhasil");
                                                                        formEmail.dismiss();
                                                                        msimpan_email_nama.setText("Simpan");
                                                                        msimpan_email_nama.setEnabled(true);
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(AccountDetails.this, "Update Gagal", Toast.LENGTH_SHORT).show();
                                                                        Log.d("UpdateRespon: ",e.toString());
                                                                        formEmail.dismiss();
                                                                        msimpan_email_nama.setText("Simpan");
                                                                        msimpan_email_nama.setEnabled(true);
                                                                    }
                                                                });
                                                            }else {
                                                                msimpan_email_nama.setText("Simpan");
                                                                msimpan_email_nama.setEnabled(true);
                                                                Log.d("Update-Email-Login: ", "User profile updated.");
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            msimpan_email_nama.setText("Simpan");
                                                            msimpan_email_nama.setEnabled(true);
                                                            Log.d("Update-Email-Login: ", e.toString());
                                                        }
                                                    });
                                        }else {
                                            Toast.makeText(AccountDetails.this, "Kata Sandi/Email Yang Anda Masukan Tidak Sesuai", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("ErrorAuth :", e.toString());
                                        msimpan_email_nama.setText("Simpan");
                                        msimpan_email_nama.setEnabled(true);
                                    }
                                });



                    }else {
                        memailedit.setError("Masukan Email Yang Valid");
                    }
                }



            }
        });
        formEmail.show();
    }
    public void formEditAlamat(){
        formAlamat = new Dialog(this);
        formAlamat.setContentView(R.layout.formalamat);
        meditalamatlengkap = formAlamat.findViewById(R.id.editalamatlengkap);
        meditkabupaten = formAlamat.findViewById(R.id.editkabupaten);
        meditprovinsi = formAlamat.findViewById(R.id.editprovinsi);
        meditkodepos = formAlamat.findViewById(R.id.editkodepos);
        meditnohp = formAlamat.findViewById(R.id.editnohp);
        mbatal_alamat = formAlamat.findViewById(R.id.batal_alamat);
        msimpan_alamat = formAlamat.findViewById(R.id.simpan_alamat);
        meditkecamatan = formAlamat.findViewById(R.id.editkecamatan);

//        ArrayAdapter<String> kategori = new ArrayAdapter(AccountDetails.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
//                provinsiList);
//        kategori.notifyDataSetChanged();
//        meditprovinsi.setAdapter(kategori);
//        meditprovinsi.setSelection(pos);
        //batal_update
        mbatal_alamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formAlamat.dismiss();
            }
        });
        msimpan_alamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (provinsiId.equals("00")){
                    Toast.makeText(AccountDetails.this, "Pilih provinsi", Toast.LENGTH_SHORT).show();
                }else {
                    if (kabupatenId.equals("00")){
                        Toast.makeText(AccountDetails.this, "Pilih kabupaten", Toast.LENGTH_SHORT).show();

                    }else {
                        if (kecamatanId.equals("00")){
                            Toast.makeText(AccountDetails.this, "Pilih kecamatan", Toast.LENGTH_SHORT).show();
                        }else {
                            if (meditalamatlengkap.length()<5){
                                meditalamatlengkap.setError("Masukan alamat lengkap");
                            }else {
                                if (meditkodepos.length()<5){
                                    meditkodepos.setError("Masukan kodepos yang valid");

                                }else {
                                    if (meditnohp.length()==0){
                                        meditnohp.setError("Masukan no hp");

                                    }else {
                                        //Jika Semua form sudah di isi baru proses update
                                        msimpan_alamat.setText("Loading...");
                                        msimpan_alamat.setEnabled(false);
                                        Map<String, Object> dataEmailNama = new HashMap();
                                        dataEmailNama.put("AlamatLengkap",meditalamatlengkap.getText().toString());
                                        dataEmailNama.put("Kabupaten",Kabupaten);
                                        dataEmailNama.put("KodePos",meditkodepos.getText().toString());
                                        dataEmailNama.put("NoHp",meditnohp.getText().toString());
                                        dataEmailNama.put("Provinsi",Provinsi);
                                        dataEmailNama.put("Kecamatan",Kecamatan);
                                        dataEmailNama.put("KecamatanId",kecamatanId);
                                        DocumentReference documentReference = firebaseFirestore.collection("UserPk").document(userId);
                                        documentReference.update(dataEmailNama).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(AccountDetails.this, "Update Berhasil", Toast.LENGTH_SHORT).show();
                                                Log.d("UpdateRespon: ","Update Berhasil");
                                                formAlamat.dismiss();
                                                msimpan_alamat.setText("Simpan");
                                                msimpan_alamat.setEnabled(true);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AccountDetails.this, "Update Gagal", Toast.LENGTH_SHORT).show();
                                                Log.d("UpdateRespon: ",e.toString());
                                                formAlamat.dismiss();
                                                msimpan_alamat.setText("Simpan");
                                                msimpan_alamat.setEnabled(true);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }


            }
        });
        //// load provinsi
        ArrayAdapter arrayAdapter = new ArrayAdapter(AccountDetails.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, provinsiList);
        arrayAdapter.notifyDataSetChanged();
        meditprovinsi.setAdapter(arrayAdapter);

        ///select provinsi
        meditprovinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (provinsiListId.get(position).equals("00")){
//                    Toast.makeText(AccountDetails.this, "Tidak Load", Toast.LENGTH_SHORT).show();
                    provinsiId=provinsiListId.get(position);
                }else {
                    provinsiId=provinsiListId.get(position);
                    Provinsi=provinsiList.get(position);
//                    Toast.makeText(AccountDetails.this, provinsiListId.get(position), Toast.LENGTH_SHORT).show();
                    loadKabupaten();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ///select kabupaten
        meditkabupaten.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (kabupatenListId.get(position).equals("00")){
//                    Toast.makeText(AccountDetails.this, "Tidak Load", Toast.LENGTH_SHORT).show();
                    kabupatenId=kabupatenListId.get(position);
                }else {
                    kabupatenId=kabupatenListId.get(position);
                    Kabupaten=kabupatenList.get(position);
//                    Toast.makeText(AccountDetails.this, provinsiListId.get(position), Toast.LENGTH_SHORT).show();
                    loadKecamatan();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ///select kabupaten
        meditkecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (kecamatanListId.get(position).equals("00")){
//                    Toast.makeText(AccountDetails.this, "Tidak Load", Toast.LENGTH_SHORT).show();
                    kecamatanId=kecamatanListId.get(position);
                }else {
                    kecamatanId=kecamatanListId.get(position);
                    Kecamatan=kecamatanList.get(position);
//                    Toast.makeText(AccountDetails.this, kecamatanId, Toast.LENGTH_SHORT).show();
//                    loadKecamatan();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //submit_Update
        formAlamat.show();
    }
    public void loadKecamatan(){
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.setCancelable(false);
        loading.show();
        /// kosongkan kecamatan jika kondisi tidak null, agar data tidak double di pilihan
        if (kecamatanList!=null){
            kecamatanList.clear();
            kecamatanListId.clear();
        }
        final JsonObject jsonObject = new JsonObject();
        IRetrofit jsonPostService = ServiceGenerator.createService(IRetrofit.class, baseurl);
        Call<JsonObject> call = jsonPostService.getKecamatan("ff7c46a65db03398a608e66f5aff09de",baseurl+"subdistrict?province="+provinsiId+"&city="+kabupatenId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject post=response.body();
                Log.d("respon_getprov", post.toString());
                JsonObject rajaongkir = post.get("rajaongkir").getAsJsonObject();
                JsonObject status = rajaongkir.get("status").getAsJsonObject();
                if (status.get("code").getAsInt()==200) {
                    loading.dismiss();
                    kecamatan_list = rajaongkir.getAsJsonArray("results");
                    kecamatanListId.add("00");
                    kecamatanList.add("-Pilih Kecamatan-");
                    for (int i = 0; i < kecamatan_list.size(); ++i) {
                        JsonObject jsonObject3 = (JsonObject)kecamatan_list.get(i);
                        String kecamatanName = jsonObject3.getAsJsonObject().get("subdistrict_name").getAsString();
                        String kecamatan_id = jsonObject3.getAsJsonObject().get("subdistrict_id").getAsString();
                        kecamatanListId.add(kecamatan_id);
                        kecamatanList.add(kecamatanName);
                    }
                    //// load kabupaten
                    ArrayAdapter arrayAdapter2 = new ArrayAdapter(AccountDetails.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, kecamatanList);
                    arrayAdapter2.notifyDataSetChanged();
                    meditkecamatan.setAdapter(arrayAdapter2);
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                loading.dismiss();

            }
        });
        Log.d("json_kecamatan",baseurl+"subdistrict?province="+provinsiId+"&city="+kabupatenId);
    }
    public void loadKabupaten(){
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.setCancelable(false);
        loading.show();
        /// kosongkan kabupaten jika kondisi tidak null, agar data tidak double di pilihan
        if (kabupatenList!=null){
            kabupatenList.clear();
            kabupatenListId.clear();
        }
        final JsonObject jsonObject = new JsonObject();
        IRetrofit jsonPostService = ServiceGenerator.createService(IRetrofit.class, baseurl);
        Call<JsonObject> call = jsonPostService.getkabupaten("ff7c46a65db03398a608e66f5aff09de",baseurl+"city?province="+provinsiId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject post=response.body();
                Log.d("respon_getprov", post.toString());
                JsonObject rajaongkir = post.get("rajaongkir").getAsJsonObject();
                JsonObject status = rajaongkir.get("status").getAsJsonObject();
                if (status.get("code").getAsInt()==200) {
                    loading.dismiss();
                    kabupaten_list = rajaongkir.getAsJsonArray("results");
                    kabupatenListId.add("00");
                    kabupatenList.add("-Pilih Kabupaten-");
                    for (int i = 0; i < kabupaten_list.size(); ++i) {
                        JsonObject jsonObject3 = (JsonObject)kabupaten_list.get(i);

                        addKabupatenname = jsonObject3.getAsJsonObject().get("type").getAsString()+" " +jsonObject3.getAsJsonObject().get("city_name").getAsString();
                        addkabupatenId = jsonObject3.getAsJsonObject().get("city_id").getAsString();
                        kabupatenListId.add(addkabupatenId);
                        kabupatenList.add(addKabupatenname);
                    }
                    //// load kabupaten
                    ArrayAdapter arrayAdapter2 = new ArrayAdapter(AccountDetails.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, kabupatenList);
                    arrayAdapter2.notifyDataSetChanged();
                    meditkabupaten.setAdapter(arrayAdapter2);
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                loading.dismiss();

            }
        });
        Log.d("json_kabupaten",baseurl+"city?province="+provinsiId.toString());
    }
    public void loadProvinsi(){
        final JsonObject jsonObject = new JsonObject();
        IRetrofit jsonPostService = ServiceGenerator.createService(IRetrofit.class, baseurl);
        Call<JsonObject> call = jsonPostService.getProvinsi("ff7c46a65db03398a608e66f5aff09de");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject post=response.body();
                Log.d("respon_getprov", post.toString());
                JsonObject rajaongkir = post.get("rajaongkir").getAsJsonObject();
                JsonObject status = rajaongkir.get("status").getAsJsonObject();
                if (status.get("code").getAsInt()==200) {
                    loading.dismiss();
                    provinsi_list = rajaongkir.getAsJsonArray("results");
                    provinsiListId.add("00");
                    provinsiList.add("-Pilih Provinsi-");
                    for (int i = 0; i < provinsi_list.size(); ++i) {
                        JsonObject jsonObject3 = (JsonObject)provinsi_list.get(i);
                        String string3 = jsonObject3.getAsJsonObject().get("province_id").getAsString();
                        String string4 = jsonObject3.getAsJsonObject().get("province").getAsString();
                        provinsiListId.add(string3);
                        provinsiList.add(string4);
                    }
                    getTokenNotif();
                    loading.dismiss();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                loading.dismiss();

            }
        });
        Log.d("json_ongkir",jsonObject.toString());
    }
    public void getTokenNotif(){
        firebaseFirestore3 = FirebaseFirestore.getInstance();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Map<String, Object> dataEmailNama = new HashMap();
                        dataEmailNama.put("token",token);
                        DocumentReference documentReference = firebaseFirestore3.collection("UserPk").document(userId);
                        documentReference.update(dataEmailNama).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
//                                Toast.makeText(LoginActivity.this, "Update Berhasil", Toast.LENGTH_SHORT).show();
                                Log.d("UpdateRespon: ",token);
                                ///simpan token ke local
                                SharedPreferences sharedPreferences = getSharedPreferences("TOKEN_NOTIF", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("token_user", token);
                                editor.apply();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(LoginActivity.this, "Update Gagal", Toast.LENGTH_SHORT).show();
                                Log.d("UpdateRespon: ",e.toString());

                            }
                        });
                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("token_message", token);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}