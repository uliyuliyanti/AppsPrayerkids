package com.prayerkidsstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrasiActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId="";
    EditText mnamalengkap,memailuser,mpassworduser,mretypepassworduser;
    TextView mbtndaftar;
    CheckBox mcheckprivacy;
    String namalengkap_user="";
    String password_user="";
    String repassword_user="";
    String email_user="";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);
        mnamalengkap = findViewById(R.id.namalengkap);
        memailuser = findViewById(R.id.emailuser);
        mpassworduser = findViewById(R.id.passworduser);
        mretypepassworduser = findViewById(R.id.retypepassworduser);
        mcheckprivacy = findViewById(R.id.checkprivacy);
        mbtndaftar = findViewById(R.id.btndaftar);

        //Proses Pendaftaran
        mbtndaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namalengkap_user = mnamalengkap.getText().toString();
                email_user = memailuser.getText().toString();
                password_user = mpassworduser.getText().toString();
                repassword_user = mretypepassworduser.getText().toString();
                //jika nama lengkap kurang dari 2 charakter maka eror
                if (mnamalengkap.length()<2){
                    mnamalengkap.setError("Masukan nama lengkap anda");
                }else {
                    // jika email tidak sesuai pattern maka eror
                    if (email_user.matches(emailPattern)){
                        //jika password kurang dari 6 charakter maka eror
                        if (mpassworduser.length()<6){
                            mpassworduser.setError("Minimal 6 Character");
                        }else {
                            //jika password sama dengan Re-Password maka lanjut
                            if (password_user.equals(repassword_user)){
                                // jika checkbox tidak di check maka blum dapat proses daftar
                                if (mcheckprivacy.isChecked()){
                                    DaftarAkun(email_user,password_user);
                                }else {
                                    Toast.makeText(RegistrasiActivity.this, "Wajib Menyetujui Privacy Policy", Toast.LENGTH_SHORT).show();
                                }
                                // jika password tidak sama dengan Re-Password maka Error
                            }else {
                                mretypepassworduser.setError("Password Tidak Sama");
                            }
                        }
                    }else {
                        memailuser.setError("Masukan Email Yang Valid");

                    }
                }

            }
        });

    }
    public void DaftarAkun(String username, String password){
        mbtndaftar.setText("Loading...");
        mbtndaftar.setEnabled(false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(username,password)
                .addOnCompleteListener(RegistrasiActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            userId = firebaseAuth.getCurrentUser().getUid();
                            Log.d("UidUser",userId);
                            DocumentReference documentReference = firebaseFirestore.collection("UserPk").document(userId);
                            Map<String, Object> user = new HashMap<>();
                            user.put("UserName",namalengkap_user);
                            user.put("EmailUser",email_user);
                            user.put("Admin",false);
                            user.put("NoHp","");
                            user.put("AlamatLengkap","");
                            user.put("Provinsi","");
                            user.put("Kabupaten","");
                            user.put("Kecamatan","");
                            user.put("KecamatanId","");
                            user.put("KodePos","");
                            user.put("OrderBadge","0");
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //jika Berhasil Menuju Page My Profile
                                    Intent logoout = new Intent(RegistrasiActivity.this, AccountDetails.class);
                                    startActivity(logoout);
                                    finish();
                                    Log.d("kondisi login","Logout");
                                    Toast.makeText(RegistrasiActivity.this, "Berhasil membuat akun", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // jika gagal tampilkan eror dan kembalikan teks daftar pada button
                                    mbtndaftar.setText("Daftar");
                                    mbtndaftar.setEnabled(true);
                                    Toast.makeText(RegistrasiActivity.this, "Gagal membuat akun", Toast.LENGTH_SHORT).show();
                                    Log.d("gagaldaftar",e.toString());

                                }
                            });
                        } else {
                            mbtndaftar.setText("Daftar");
                            mbtndaftar.setEnabled(true);

                        }
                    }
                }).addOnFailureListener(RegistrasiActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                       // Pendaftaran Gagal Tampilkan Log ErorDaftar dan kembalikan teks daftar pada button
                        mbtndaftar.setText("Daftar");
                        mbtndaftar.setEnabled(true);
                        Toast.makeText(RegistrasiActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("ErorDaftar",e.toString());
                    }
                });
    }
}