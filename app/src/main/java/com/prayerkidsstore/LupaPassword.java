package com.prayerkidsstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class LupaPassword extends AppCompatActivity {
    TextView msubmitreset;
    EditText memailuser;
    String email_user="";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);
        msubmitreset = findViewById(R.id.submitreset);
        memailuser = findViewById(R.id.emailuser);

        msubmitreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memailuser.getText().toString().matches(emailPattern)){
                    //jiga patern email betul maka panggil reset password
                    resetPassword(memailuser.getText().toString());
                }else {
                    memailuser.setError("Masukan Email Yang Valid");

                }

            }
        });
    }
    public void resetPassword(String email){
        msubmitreset.setText("Loading...");
        msubmitreset.setEnabled(false);
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Password Di kirim: ", task.toString());
                            Toast.makeText(LupaPassword.this, "Silahkan periksa Email Anda", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        msubmitreset.setEnabled(true);
                        msubmitreset.setText("Submit");
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
}