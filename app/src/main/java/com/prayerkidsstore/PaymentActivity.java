package com.prayerkidsstore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.prayerkidsstore.OrderDetails.OrderDetailsAdapter;
import com.prayerkidsstore.OrderDetails.OrderDetailsItem;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {
    TextView mtotal_price_payment,morderId_payment, mcopy_rek,mnorek,mupload_bukti_btn;
    ImageView mbackbtn;
    String orderId="";
    String page = "";
    FirebaseFirestore firebaseFirestore;
    String userId="";
    ProgressDialog loading;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mbackbtn = findViewById(R.id.backbtn);
        morderId_payment = findViewById(R.id.orderId_payment);
        mtotal_price_payment = findViewById(R.id.total_price_payment);
        mnorek = findViewById(R.id.norek);
        mcopy_rek = findViewById(R.id.copy_rek);
        mupload_bukti_btn = findViewById(R.id.upload_bukti_btn);
        //Mengambil orderId Product Kemudian Load Data Product
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderId = extras.getString("OrderId");
            page = extras.getString("pages");
            checkLogin();
        }

        mbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mcopy_rek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(null,mnorek.getText().toString());
                if (clipboard == null) return;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(PaymentActivity.this, "No Rekening Berhasil Di Salin", Toast.LENGTH_SHORT).show();
            }
        });
        mupload_bukti_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoPembayaran = new Intent(PaymentActivity.this, PaymentKonfirmasiActivity.class);
                gotoPembayaran.putExtra("OrderId",orderId);
                gotoPembayaran.putExtra("pages",page);
                startActivity(gotoPembayaran);
                finish();
            }
        });
    }
    public void loadDataOrder(){
        loading = new ProgressDialog(this);
        loading.setMessage("loading");
        loading.show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference docRef = firebaseFirestore.collection("OrderPk").document(userId).collection("OrderList").document(orderId);
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
                    mtotal_price_payment.setText("Total : "+snapshot.get("TotalPrice").toString());
                    morderId_payment.setText(snapshot.get("OrderId").toString());
                    /// jika customer belum upload bukti pembayaran maka status btn lnjut bayar show
                    loading.dismiss();
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

            userId = user.getUid();
            loadDataOrder();
            Log.d("kondisi login","Login userId: "+userId);
        } else {
            // No user is signed in
            Intent logoout = new Intent(PaymentActivity.this, LoginActivity.class);
            startActivity(logoout);
            finish();
            Log.d("kondisi login","Tidak Login");
        }


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (page.equals("order")){
            Intent gotoPembayaran = new Intent(PaymentActivity.this, MyOrderDetails.class);
            gotoPembayaran.putExtra("OrderId",orderId);
            startActivity(gotoPembayaran);
            finish();
        }else {
            Intent backhome = new Intent(PaymentActivity.this, HomeAct.class);
            startActivity(backhome);
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            finish();
        }

    }
}