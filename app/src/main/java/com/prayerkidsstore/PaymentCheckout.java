package com.prayerkidsstore;

import static com.prayerkidsstore.AdminUserOrder.AdminUserOrderAdapter.token_add;
import static com.prayerkidsstore.KeranjangActivity.keranjangItems;
import static com.prayerkidsstore.KeranjangActivity.mtotal_harga_keranjang;
import static com.prayerkidsstore.apihelper.ServiceGenerator.baseurl;
import static com.prayerkidsstore.apihelper.ServiceGenerator.fcmbase;
import static com.prayerkidsstore.checkout.CheckoutAdapter.harga_total_plus;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.prayerkidsstore.AdminUserOrder.AdminUserOrderAdapter;
import com.prayerkidsstore.AdminUserOrder.AdminUserOrderItem;
import com.prayerkidsstore.apihelper.IRetrofit;
import com.prayerkidsstore.apihelper.ServiceGenerator;
import com.prayerkidsstore.checkout.CheckoutAdapter;
import com.prayerkidsstore.checkout.CheckoutImgAdapter;
import com.prayerkidsstore.checkout.CheckoutItem;
import com.prayerkidsstore.keranjang.KeranjangAdapter;
import com.prayerkidsstore.keranjang.KeranjangItem;
import com.prayerkidsstore.ongkir.ResultItem;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.transform.sax.SAXResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentCheckout extends AppCompatActivity {
    FirebaseUser user;
    String userId;
    ProgressDialog loading;
    FirebaseFirestore firebaseFirestore;
    TextView mpenerima, malamat_kirim, mno_hp, mgrand_total, mtotal_ongkir;
    ImageView mbackbtn;
    CheckoutAdapter checkoutAdapter;
    CheckoutImgAdapter checkoutImgAdapter;
    ArrayList<CheckoutItem>checkoutItems;
    CheckoutItem checkoutItem_list;
    RecyclerView mcheckout_list_item,mcheckout_img;
    public static TextView mtotal_harga_checkout,mtotal_items_checkout;
    //// check ongkir
    String origin = "";
    String destination = "";
    String orderbadge="";
    public static int weight=0;
    ArrayList <ResultItem> resultItem;
    int resultValue = 0;
    TextView mongkir;
    double harga_ongkir=0.0;
    ///lanjut pemmbayaran
    TextView mlanjut_Pembayaran;
    int jumlahitem =0;
    int item_position = 0;
    String codeDate="";
    LinearLayout meditalamatkirim;
    ///
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
    EditText meditkodepos,meditnohp, meditalamatlengkap;
    JsonArray token_admin;
    /////
    ArrayList<AdminUserOrderItem> adminUserOrderItems;
    AdminUserOrderItem adminUserOrderItem;
    AdminUserOrderAdapter adminUserOrderAdapter;
    List<String> token_add;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_checkout);
        mlanjut_Pembayaran = findViewById(R.id.lanjut_Pembayaran);
        mpenerima = findViewById(R.id.penerima);
        mgrand_total = findViewById(R.id.grand_total);
        mtotal_ongkir = findViewById(R.id.total_ongkir);
        mongkir = findViewById(R.id.ongkir);
        mcheckout_list_item = findViewById(R.id.checkout_list_item);
        mtotal_harga_checkout = findViewById(R.id.total_harga_checkout);
        mtotal_items_checkout = findViewById(R.id.total_item_checkout);
        mcheckout_img = findViewById(R.id.checkout_img);
        malamat_kirim = findViewById(R.id.alamat_kirim);
        mno_hp = findViewById(R.id.no_hp);
        mbackbtn = findViewById(R.id.backbtn);
        meditalamatkirim=findViewById(R.id.editalamatkirim);
        ////check login
        adminUserOrderItems = new ArrayList<>();
        adminUserOrderItem = new AdminUserOrderItem();
        checkLogin();
        //// Back Ke keranjang
        mbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mlanjut_Pembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (kecamatanId.equals("")){
                    Toast.makeText(PaymentCheckout.this, "Isi Alamat Kirim Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                }else {

                    orderPush();
                }
            }
        });
        meditalamatkirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formEditAlamat();
            }
        });
    }
    public void checkLogin(){
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //User Sedang Login
            userId = user.getUid();
            Log.d("kondisi login","Login userId: "+userId);
            ////load data aLamat
            loadDataAlamat();

        } else {
            // User Sudah Logout
            Log.d("kondisi login","Tidak Login");
            Intent logoout = new Intent(PaymentCheckout.this, LoginActivity.class);
            startActivity(logoout);
            finish();
        }
    }
    public void loadDataAlamat(){
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
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
                    String alamat_Kirim = snapshot.get("AlamatLengkap").toString()+", "+snapshot.get("Kecamatan").toString()+", "+snapshot.get("Kabupaten").toString() +", "+snapshot.get("Provinsi").toString() +", "+snapshot.get("KodePos").toString();
                    mpenerima.setText("PENERIMA: "+snapshot.get("UserName").toString());
                    malamat_kirim.setText("ALAMAT KIRIM: "+alamat_Kirim);
                    mno_hp.setText("NO-HP: "+snapshot.get("NoHp").toString());
                    destination = snapshot.get("KecamatanId").toString();
                    orderbadge = snapshot.get("OrderBadge").toString();
                    ////load data item
                    loadItemKeranjang();

                } else {
                    loading.dismiss();
                    Log.d("snpashot: ", "Current data: null");
                }
            }
        });
    }
    public void loadItemKeranjang(){
        checkoutItems = new ArrayList<>();
        checkoutItem_list = new CheckoutItem();
        // list item harga
        mcheckout_list_item.setHasFixedSize(true);
        mcheckout_list_item.setLayoutManager(new LinearLayoutManager(this));
        checkoutAdapter = new CheckoutAdapter(this, checkoutItems);
        mcheckout_list_item.setAdapter(checkoutAdapter);
        //// list item img
        mcheckout_img.setHasFixedSize(true);
        mcheckout_img.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        checkoutImgAdapter = new CheckoutImgAdapter(this, checkoutItems);
        mcheckout_img.setAdapter(checkoutImgAdapter);
        /// get data fire store
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("KeranjangUser").document(userId).collection("ProductAdd").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error !=null){
                    Log.e("Fire Store Error",error.getMessage());
                    return;

                }
                for (DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        checkoutItem_list=dc.getDocument().toObject(CheckoutItem.class);
                        checkoutItem_list.setKeyId(dc.getDocument().getId());
                        checkoutItems.add(checkoutItem_list);
                        Log.d("dataproduct",dc.getDocument().getData().toString());
                    }

                    checkoutAdapter.notifyDataSetChanged();
                    checkoutImgAdapter.notifyDataSetChanged();

                }

            }

        });

        getOrigin();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(PaymentCheckout.this, KeranjangActivity.class);
        startActivity(backhome);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
    public void deleteItemKeranjang(){
        jumlahitem=keranjangItems.size();
        if (item_position+1>jumlahitem){
          updateOrderBade();
          sendnotifchat();
        }else {
            Log.d("positions",String.valueOf(item_position)+" Jumlah iTEM"+ String.valueOf(jumlahitem));
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("KeranjangUser").document(userId).collection("ProductAdd").document(keranjangItems.get(item_position).getKeyId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("Delete_Keranjang : ", "Berhasil"+userId);
//                setelah berhasil delete langsung pindah ke page pebayaran
                    item_position+=1;
                    deleteItemKeranjang();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete_Keranjang : ", "gagal"+e.toString());
                    loading.dismiss();
                }
            });

        }

    }
    public void loadTokenAdmin(){
//        adminUserOrderItems = new ArrayList<>();
//        adminUserOrderItem = new AdminUserOrderItem();
        // adding our array list to our recycler view adapter class.
//        morder_list.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
//        morder_list.setLayoutManager(mLayoutManager);

        adminUserOrderAdapter = new AdminUserOrderAdapter(this, adminUserOrderItems);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("UserPk").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
//                    Log.d(TAG, "onSuccess: LIST EMPTY");
                    return;
                } else {
                    // Convert the whole Query Snapshot to a list
                    // of objects directly! No need to fetch each
                    // document.

                   List<AdminUserOrderItem> s = queryDocumentSnapshots.toObjects(AdminUserOrderItem.class);
//
//                    // Add all to your list
                    adminUserOrderItems.addAll(s);
                    Log.d("token-add",adminUserOrderItems.get(0).getToken().toString());
                    loading.dismiss();
//                    Log.d(TAG, "onSuccess: " + mArrayList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

//                addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error !=null){
//                    Log.e("Fire Store Error",error.getMessage());
//                    return;
//
//                }else {
//                    loading.dismiss();
//                }
//                for (DocumentChange dc : value.getDocumentChanges()){
//                    if(dc.getType() == DocumentChange.Type.ADDED){
//                        adminUserOrderItem=dc.getDocument().toObject(AdminUserOrderItem.class);
//                        adminUserOrderItem.setKeyId(dc.getDocument().getId());
//                        adminUserOrderItems.add(adminUserOrderItem);
////                        Log.d("token-add",dc.getDocument().getData().toString());
////                        Log.d("token-add",token_add.toString());
//                    }
////                    total=String.valueOf(adminUserOrderItems.size());
////                    mtotal_order.setText("Total Order: "+total);
////                    adminUserOrderAdapter.notifyDataSetChanged();
//                    loading.dismiss();
//                }
//
//
//            }
//
//        });

    }
    public void sendnotifchat(){
        token_add = new ArrayList<>();
        for (int i = 0; i < adminUserOrderItems.size(); ++i){
            /////jika user bukan admin tidak tambah token
            if (adminUserOrderItems.get(i).isAdmin()){
                token_add.add(adminUserOrderItems.get(i).getToken());
            }
        }
        Gson gson = new GsonBuilder().create();
        token_admin = gson.toJsonTree(token_add).getAsJsonArray();
        JsonObject dataid = new JsonObject();
        dataid.addProperty("id", userId+"/"+"PK"+codeDate);

        JsonObject notifikasidata = new JsonObject();
        notifikasidata.addProperty("title","Order Baru");
        notifikasidata.addProperty("body","PK"+codeDate);
        notifikasidata.addProperty("click_action","ordermasuk");

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("registration_ids", token_admin);
        jsonObject.add("data",dataid);
        jsonObject.add("notification",notifikasidata);
//        Toast.makeText(DetailsFormActivity.this,jsonObject.toString(), Toast.LENGTH_SHORT).show();
        IRetrofit jsonPostService = ServiceGenerator.createService(IRetrofit.class, fcmbase);
        Call<JsonObject> panggilkomplek = jsonPostService.sendNotifcation(jsonObject);
        panggilkomplek.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String errornya = "";
                JsonObject homedata = response.body();
                Log.d("responnotif",homedata.toString());
                int statusnya = homedata.get("success").getAsInt();
                if (statusnya == 1) {
                    Log.d("responnotif",homedata.toString());
                } else {

                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                loading.dismiss();


            }
        });
        Log.d("requestnotif",jsonObject.toString());




    }
    public void updateOrderBade(){
        int orderbadges = Integer.parseInt(orderbadge)+1;
            orderbadge = String.valueOf(orderbadges);
        Map<String, Object> dataEmailNama = new HashMap();
        dataEmailNama.put("OrderBadge",orderbadge);
        DocumentReference documentReference = firebaseFirestore.collection("UserPk").document(userId);
        documentReference.update(dataEmailNama).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                Toast.makeText(PaymentCheckout.this, "Update Berhasil", Toast.LENGTH_SHORT).show();
                Log.d("UpdateRespon: ","Update Berhasil");
                loading.dismiss();
                ////update badge order
                Intent gotoPembayaran = new Intent(PaymentCheckout.this, PaymentActivity.class);
                gotoPembayaran.putExtra("OrderId","PK"+codeDate);
                gotoPembayaran.putExtra("pages","paymentcheckout");
                startActivity(gotoPembayaran);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading.dismiss();
                Toast.makeText(PaymentCheckout.this, "Update Gagal", Toast.LENGTH_SHORT).show();
                Log.d("UpdateRespon: ",e.toString());

            }
        });
    }
    public void getOrigin(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference docRef = firebaseFirestore.collection("OriginPengiriman").document("originAdmin");
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
                    origin = snapshot.get("KecamatanId").toString();
                    ////load data Ongkir diberi jeda 3 detik untuk meload data qty terlebih dahulu
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Jika destination kosong maka tidak check ongkir
                            if (destination.equals("")){
//                                loading.dismiss();
                                loadTokenAdmin();
                            }else {
                                checkOngkir();

                            }

                        }
                    }, 3000);


                } else {
                    loading.dismiss();
                    Log.d("snpashot: ", "Current data: null");
                }
            }
        });
    }
    public void checkOngkir(){

        weight=weight*1000;
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("origin",origin);
        jsonObject.addProperty("originType","subdistrict");
        jsonObject.addProperty("destination",destination);
        jsonObject.addProperty("destinationType","subdistrict");
        jsonObject.addProperty("weight",weight);
        jsonObject.addProperty("courier","jnt");
        IRetrofit jsonPostService = ServiceGenerator.createService(IRetrofit.class, baseurl);
        Call<JsonObject> call = jsonPostService.costpost("ff7c46a65db03398a608e66f5aff09de",jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject post=response.body();
                Log.d("respon_ongkir", post.toString());
                JsonObject rajaongkir = post.get("rajaongkir").getAsJsonObject();
                JsonObject status = rajaongkir.get("status").getAsJsonObject();
                if (status.get("code").getAsInt()==200){
                    JsonArray result = rajaongkir.getAsJsonArray("results");
                    resultItem = new ArrayList<ResultItem>();
                    Gson gson2 = new Gson();
                    Type listType2 = new TypeToken<ArrayList<ResultItem>>() {
                    }.getType();
                    resultItem = gson2.fromJson(result.toString(),listType2);
                    if(resultItem!=null && resultItem.size()!=0){
                        for (int i = 0; i < resultItem.size(); i++){
                            resultValue= resultItem.get(0).getCosts().get(0).getCost().get(0).getValue();
                            Log.d("resultVlue",String.valueOf(resultValue));
                        }
                        harga_ongkir= Double.parseDouble(String.valueOf(resultValue));
//                        String harga_total_text = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(harga_ongkir);
                        String harga_total_text = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(0.0);
                        mongkir.setText(harga_total_text);
                        mtotal_ongkir.setText(harga_total_text);
                        int grandTotal = resultValue+harga_total_plus;
                        double grandTotal_Value=Double.parseDouble(String.valueOf(grandTotal));
                        String grand_total_text = NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(grandTotal_Value);
                        mgrand_total.setText(grand_total_text);
                        /////set gratis
                        mongkir.setText("Gratis Ongkir");
                    }
                    Log.d("Status",status.get("description").getAsString());
                    loadTokenAdmin();

                }else {
                    Log.d("Status",status.get("description").getAsString());
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
    public void orderPush(){
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.show();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
        SimpleDateFormat currentDate2 = new SimpleDateFormat("dd MM yyyy HH mm ss");
        Date todayDate = new Date();
        String thisDate = currentDate.format(todayDate);
        codeDate = currentDate2.format(todayDate).replace(" ","");
        firebaseFirestore=FirebaseFirestore.getInstance();
        Map<String, Object> dataproduct = new HashMap();
        dataproduct.put("OrderDate",thisDate);
        dataproduct.put("OrderId","PK"+codeDate);
        dataproduct.put("TotalPrice",mgrand_total.getText().toString());
        dataproduct.put("StatusOrder","Menunggu Pembayaran");
        dataproduct.put("orderUser",checkoutItems);
        dataproduct.put("totalItem",mtotal_items_checkout.getText().toString());
        DocumentReference documentReference = firebaseFirestore.collection("OrderPk").document(userId).collection("OrderList").document("PK"+codeDate);
        documentReference.set(dataproduct).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                deleteItemKeranjang();

                Log.d("UpdateRespon: ","Product Berhasil di tambahkan");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading.dismiss();
                Log.d("gagal-Order", e.toString());
            }
        });
    }

    public void formEditAlamat(){
        formAlamat = new Dialog(this);
        formAlamat.setContentView(R.layout.formalamat);
        meditkabupaten = formAlamat.findViewById(R.id.editkabupaten);
        meditprovinsi = formAlamat.findViewById(R.id.editprovinsi);
        meditkodepos = formAlamat.findViewById(R.id.editkodepos);
        meditnohp = formAlamat.findViewById(R.id.editnohp);
        mbatal_alamat = formAlamat.findViewById(R.id.batal_alamat);
        msimpan_alamat = formAlamat.findViewById(R.id.simpan_alamat);
        meditkecamatan = formAlamat.findViewById(R.id.editkecamatan);
        meditalamatlengkap = formAlamat.findViewById(R.id.editalamatlengkap);
        loadProvinsi();
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
                    Toast.makeText(PaymentCheckout.this, "Pilih provinsi", Toast.LENGTH_SHORT).show();
                }else {
                    if (kabupatenId.equals("00")){
                        Toast.makeText(PaymentCheckout.this, "Pilih kabupaten", Toast.LENGTH_SHORT).show();

                    }else {
                        if (kecamatanId.equals("00")){
                            Toast.makeText(PaymentCheckout.this, "Pilih kecamatan", Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(PaymentCheckout.this, "Update Berhasil", Toast.LENGTH_SHORT).show();
                                                Log.d("UpdateRespon: ","Update Berhasil");
                                                formAlamat.dismiss();
                                                msimpan_alamat.setText("Simpan");
                                                msimpan_alamat.setEnabled(true);
                                                loading = new ProgressDialog(PaymentCheckout.this);
                                                loading.setMessage("loading");
                                                loading.setCancelable(false);
                                                loading.show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(PaymentCheckout.this, "Update Gagal", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(PaymentCheckout.this, provinsiListId.get(position), Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(PaymentCheckout.this, provinsiListId.get(position), Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(AccountDetails.this, provinsiListId.get(position), Toast.LENGTH_SHORT).show();
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

                    for (int i = 0; i < kecamatan_list.size(); ++i) {
                        JsonObject jsonObject3 = (JsonObject)kecamatan_list.get(i);
                        String kecamatanName = jsonObject3.getAsJsonObject().get("subdistrict_name").getAsString();
                        String kecamatan_id = jsonObject3.getAsJsonObject().get("subdistrict_id").getAsString();
                        kecamatanListId.add(kecamatan_id);
                        kecamatanList.add(kecamatanName);
                    }
                    //// load kabupaten
                    ArrayAdapter arrayAdapter2 = new ArrayAdapter(PaymentCheckout.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, kecamatanList);
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
                    ArrayAdapter arrayAdapter2 = new ArrayAdapter(PaymentCheckout.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, kabupatenList);
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

        /// kosongkan kabupaten jika kondisi tidak null, agar data tidak double di pilihan
        if (provinsiList!=null){
            provinsiList.clear();
            provinsiListId.clear();
        }
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
                    loading.dismiss();
                    //// load provinsi
                    ArrayAdapter arrayAdapter = new ArrayAdapter(PaymentCheckout.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, provinsiList);
                    arrayAdapter.notifyDataSetChanged();
                    meditprovinsi.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                loading.dismiss();

            }
        });
        Log.d("json_ongkir",jsonObject.toString());
    }
}