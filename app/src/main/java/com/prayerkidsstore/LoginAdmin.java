package com.prayerkidsstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class LoginAdmin extends AppCompatActivity {
    TextView mdaftarakun, mloginbtn,mlupapass;
    FirebaseAuth firebaseAuth;
    EditText memailuser, mpassworduser;
    String userId="";
    FirebaseFirestore firebaseFirestore;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
        mdaftarakun=findViewById(R.id.daftarakun);
        mloginbtn = findViewById(R.id.loginbtn);
        memailuser = findViewById(R.id.emailuser);
        mpassworduser = findViewById(R.id.passworduser);
        mloginbtn = findViewById(R.id.loginbtn);
        mlupapass = findViewById(R.id.lupapass);

        mlupapass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent godaftarakun = new Intent(LoginAdmin.this,LupaPassword.class);
                startActivity(godaftarakun);
                finish();
            }
        });
        //menuju page daftar akun
        mdaftarakun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent godaftarakun = new Intent(LoginAdmin.this,RegistrasiActivity.class);
                startActivity(godaftarakun);
                finish();
            }
        });
        //Proses Login Akun
        mloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memailuser.length()==0){
                    memailuser.setError("Email Harus Di Isi");
                }else {
                    if (mpassworduser.length()==0){
                        mpassworduser.setError("Password Tidak Boleh Kosong");
                    }else {
                        //panggil LoginAkun
                        LoginAkun(memailuser.getText().toString(),mpassworduser.getText().toString());
                    }
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backhome = new Intent(this, LoginActivity.class);
        startActivity(backhome);
        finish();
    }
    public void LoginAkun(String email, String password){
        mloginbtn.setText("Loading...");
        mloginbtn.setEnabled(false);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    userId = firebaseUser.getUid();
                    Log.d("ResponLogin", "Login Berhasil, UID : "+userId );

                    getTokenNotif();
                }else {
                    Log.w("ResponLogin", "failure: ", task.getException());
                    mloginbtn.setText("Login");
                    mloginbtn.setEnabled(true);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mloginbtn.setText("Login");
                mloginbtn.setEnabled(true);
                Toast.makeText(LoginAdmin.this, "Email dan Password Tidak Sesuai", Toast.LENGTH_SHORT).show();
                Log.d("ResponLogin",e.toString());
            }
        });
    }
    public void getTokenNotif(){
        firebaseFirestore = FirebaseFirestore.getInstance();
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
                        DocumentReference documentReference = firebaseFirestore.collection("UserPk").document(userId);
                        documentReference.update(dataEmailNama).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
//                                Toast.makeText(LoginActivity.this, "Update Berhasil", Toast.LENGTH_SHORT).show();
                                Log.d("UpdateRespon: ","Update Berhasil");
                                ///simpan token ke local
                                SharedPreferences sharedPreferences = getSharedPreferences("TOKEN_NOTIF", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("token_user", token);
                                editor.apply();
                                ;              Intent logoout = new Intent(LoginAdmin.this, AccountActivity.class);
                                startActivity(logoout);
                                finish();
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