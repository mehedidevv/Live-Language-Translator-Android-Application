package com.example.languagetranslatorapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;


import com.example.languagetranslatorapp.splashScreen.SplashActivity2;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SinginActivity extends AppCompatActivity {

    EditText userEmail,userPassword;
    AppCompatButton singinBtn;
    TextView singupBtn;

    TextView forgetPassword;
    String email,password;

    ImageView passwordIcon;
    private  boolean passwordShowing=false;

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_singin);


        databaseReference= FirebaseDatabase.getInstance().getReference("users");

        userEmail=findViewById(R.id.edemail);
        userPassword=findViewById(R.id.edpassword);
        singinBtn=findViewById(R.id.button_login);
        singupBtn=findViewById(R.id.signup);
        passwordIcon=findViewById(R.id.passwordIcon);
        forgetPassword=findViewById(R.id.forgetPassword);

        //OnClick Listner to Forget Password
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SinginActivity.this,ForgetPassActivity.class));

            }
        });

        // On Click Listner to Signin Button........
        singinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = userEmail.getText().toString().trim();
                password = userPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    userEmail.setError("Please enter your Email");
                    userEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    userPassword.setError("Please enter your Password");
                    userPassword.requestFocus();
                    return;
                }

                Singin();

            }
        });


        //On Click Listner to Password icon
        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (passwordShowing){
                    passwordShowing=false;
                    userPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.password);
                }else {
                    passwordShowing=true;

                    userPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.pass);
                }
                userPassword.setSelection(userPassword.length());
            }
        });


        // On Click Listner to SignUp Button..........
        singupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SinginActivity.this,SingupActivity.class);
                        startActivity(intent);
            }
        });



    }



    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            startActivity(new Intent(SinginActivity.this,allUserActivity.class));
            finish();
        }
    }


    // Method for Signin.............
    private void Singin() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(),password)

                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        String username=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                        Intent intent=new Intent(SinginActivity.this, SplashActivity2.class);
                        intent.putExtra("name",username);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseAuthInvalidUserException)
                        {
                            Toast.makeText(SinginActivity.this,"User dosen't exist",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(SinginActivity.this,"Authintication Failled",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}