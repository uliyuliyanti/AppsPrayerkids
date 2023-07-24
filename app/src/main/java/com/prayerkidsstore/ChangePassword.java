package com.prayerkidsstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {
    ImageView mback;
    EditText memaillogin,mpasswordlama,mpasswordbaru,mretypepasswordbaru;
    TextView msimpanpassword;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mback = findViewById(R.id.backbtn);
        memaillogin = findViewById(R.id.emaillogin);
        mpasswordlama = findViewById(R.id.passwordlama);
        mpasswordbaru = findViewById(R.id.passwordbaru);
        mretypepasswordbaru = findViewById(R.id.retypepasswordbaru);
        msimpanpassword = findViewById(R.id.simpanpassword);
        //btn back
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        msimpanpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memaillogin.length()==0){
                    memaillogin.setError("Input Email Login");
                }else {
                    if (mpasswordlama.length()==0){
                        mpasswordlama.setError("Input Password Lama");
                    }else {
                        if (mpasswordbaru.length()==0){
                            mpasswordbaru.setError("Input Password Baru");
                        }else {
                            if (mpasswordbaru.getText().toString().equals(mretypepasswordbaru.getText().toString())){
                                formGantiPass();
                            }else {
                                mretypepasswordbaru.setError("Password Tidak Sama");
                            }
                        }
                    }
                }

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
    public void formGantiPass(){
        msimpanpassword.setEnabled(false);
        msimpanpassword.setText("Loading...");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(memaillogin.getText().toString(), mpasswordlama.getText().toString());
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("AuthUlang :", "Berhasil Auth Ulang");
                        //jika berhasil auth ulang baru update email
                        user.updatePassword(mpasswordbaru.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Update-Password-Login: ", "User password updated.");
                                            //JIKA Email Login Sudah Terupdate Maka Lanjut Update PasswordBaru kembali ke menu akun
                                            Toast.makeText(ChangePassword.this, "Password Berhasil Di Ganti", Toast.LENGTH_SHORT).show();
                                            onBackPressed();

                                        }else {
                                            msimpanpassword.setText("Simpan");
                                            msimpanpassword.setEnabled(true);
                                            Log.d("Update-Password-Login: ", "User profile updated.");
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        msimpanpassword.setText("Simpan");
                                        msimpanpassword.setEnabled(true);
                                        Log.d("Update-Password-Login: ", e.toString());
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ErrorAuth :", e.toString());
                        msimpanpassword.setText("Simpan");
                        msimpanpassword.setEnabled(true);
                    }
                });
    }
}