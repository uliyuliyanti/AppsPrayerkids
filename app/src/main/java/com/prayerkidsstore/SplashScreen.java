package com.prayerkidsstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);

        //Seting Durasi Splash Screen Satuan Milidetik 1000 = 1detik
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Menuju Home
                Intent gotohome = new Intent(SplashScreen.this,HomeAct.class);
                startActivity(gotohome);
                finish();
            }
        }, 5000);


    }
}