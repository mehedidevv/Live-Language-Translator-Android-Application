package com.example.languagetranslatorapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.languagetranslatorapp.splashScreen.SplashActivity3;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SingupActivity extends AppCompatActivity {


    EditText useremail,userpassword,username,userlanguge;
    TextView singinBtn;
    AppCompatButton singupBtn;
    String email,password,name,nativelanguge;

    DatabaseReference databaseReference;


    ImageView passwordIconSignUp;
    private  boolean passwordShowing=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_singup);

        databaseReference= FirebaseDatabase.getInstance().getReference("users");

        username=findViewById(R.id.edusername);
        useremail=findViewById(R.id.edemail);
        userpassword=findViewById(R.id.edpassword);
        userlanguge=findViewById(R.id.edlanguage);
        singinBtn=findViewById(R.id.login);
        singupBtn=findViewById(R.id.button_singup);
        passwordIconSignUp=findViewById(R.id.passwordIconsignUp);



        // On Click Listner To Signup Button..........
        singupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = username.getText().toString().trim();
                email = useremail.getText().toString().trim();
                password = userpassword.getText().toString().trim();
                nativelanguge=userlanguge.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    username.setError("Please enter your username");
                    username.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    useremail.setError("Please enter your Email");
                    useremail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    userpassword.setError("Please enter your Password");
                    userpassword.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(nativelanguge)) {
                    userlanguge.setError("Please enter your native language");
                    userlanguge.requestFocus();
                    return;
                }

                Singup();

            }
        });

        //Set On Clicl Listner to Password Icon
        passwordIconSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (passwordShowing){
                    passwordShowing=false;
                    userpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIconSignUp.setImageResource(R.drawable.password);
                }else {
                    passwordShowing=true;

                    userpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIconSignUp.setImageResource(R.drawable.pass);
                }
                userpassword.setSelection(userpassword.length());
            }
        });


        // On Click Listner To Signin Button..........
        singinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SingupActivity.this,SinginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            startActivity(new Intent(SingupActivity.this,MainActivity.class));
            finish();
        }
    }

    // Method of SignUp Method...........
    private void Singup() {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(),password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        UserProfileChangeRequest userProfileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                        firebaseUser.updateProfile(userProfileChangeRequest);

                        UserModel userModel=new UserModel(FirebaseAuth.getInstance().getUid(),name,email,password,nativelanguge);
                        databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(userModel);

                        Intent intent=new Intent(SingupActivity.this, SplashActivity3.class);

                        intent.putExtra("name",name);
                        intent.putExtra("language",nativelanguge);
                        startActivity(intent);
                        finish();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SingupActivity.this,"Password Should Be Character...",Toast.LENGTH_SHORT).show();

                    }
                });

    }
}