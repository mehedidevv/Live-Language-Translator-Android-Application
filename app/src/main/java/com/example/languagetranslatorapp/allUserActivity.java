package com.example.languagetranslatorapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class allUserActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    TextView usernamemy;
    String myname;
    AppCompatButton logOutBtn;

    DatabaseReference databaseReference;
    RelativeLayout searchButtone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_user);

        userAdapter=new UserAdapter(this);
        recyclerView=findViewById(R.id.recyclerview);

        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        usernamemy=findViewById(R.id.usernamemy);
        myname= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        usernamemy.setText(myname);

        searchButtone=findViewById(R.id.search_Button_user);
        searchButtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(allUserActivity.this,UserActivity.class));
            }
        });


        // Log Out Bitton
        logOutBtn=findViewById(R.id.LogOutBtn);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(allUserActivity.this,SinginActivity.class));
                finish();
            }
        });



        databaseReference= FirebaseDatabase.getInstance().getReference("users");


        databaseReference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAdapter.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String uId=dataSnapshot.getKey();
                    UserModel userModel=dataSnapshot.getValue(UserModel.class);

                    if(userModel!=null && userModel.getUserID()!=null && !userModel.getUserID().equals(FirebaseAuth.getInstance().getUid())){
                        userAdapter.add((userModel));
                    }
                    List<UserModel> userModelList=userAdapter.getUserModelList();
                    userAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}