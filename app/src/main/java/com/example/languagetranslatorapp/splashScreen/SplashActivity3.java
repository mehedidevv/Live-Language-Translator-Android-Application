package com.example.languagetranslatorapp.splashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.languagetranslatorapp.R;
import com.example.languagetranslatorapp.UserActivity;
import com.example.languagetranslatorapp.allUserActivity;

public class SplashActivity3 extends AppCompatActivity {

    ImageView imageViewSplash3;
    LinearLayout linearLayoutSplash3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash3);

        //Connection
        imageViewSplash3=findViewById(R.id.logo);
        linearLayoutSplash3=findViewById(R.id.LinearSplash1);

        //For Splash Screen
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Code here
                Intent myIntent = new Intent(SplashActivity3.this, allUserActivity.class);
                startActivity(myIntent);
                finish();
            }
        },3000);
        Animation animation= AnimationUtils.loadAnimation(SplashActivity3.this,R.anim.animation2);
        imageViewSplash3.startAnimation(animation);

        Animation animation1= AnimationUtils.loadAnimation(SplashActivity3.this,R.anim.animation1);
        linearLayoutSplash3.startAnimation(animation1);

    }
}