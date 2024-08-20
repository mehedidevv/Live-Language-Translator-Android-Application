package com.example.languagetranslatorapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassActivity extends AppCompatActivity {

    EditText resetMail;
    AppCompatButton resetButton;

    ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        //Connection
        resetMail=findViewById(R.id.resetMail);
        resetButton=findViewById(R.id.button_reset);
        loadingBar=findViewById(R.id.loadingBar);

        //ON Click Listner to Reset Password button
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Mail=resetMail.getText().toString();
                if (Mail.isEmpty()){
                    Toast.makeText(ForgetPassActivity.this, "Please Enter You Valid E-Mail", Toast.LENGTH_SHORT).show();

                }else {
                    loadingBar.setVisibility(View.VISIBLE);
                    resetButton.setVisibility(View.INVISIBLE);
                    // Main Logic Will Be Here For Reset Mail....
                    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                    firebaseAuth.sendPasswordResetEmail(Mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            loadingBar.setVisibility(View.INVISIBLE);
                            resetButton.setVisibility(View.VISIBLE);
                            Toast.makeText(ForgetPassActivity.this, "Reset Mail Send Succesfully! Check Your Mail", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgetPassActivity.this,SinginActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingBar.setVisibility(View.INVISIBLE);
                            resetButton.setVisibility(View.VISIBLE);
                            Toast.makeText(ForgetPassActivity.this, "Error." +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}