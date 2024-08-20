package com.example.languagetranslatorapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    TextView usernamemy;
    String myname;
    SearchView searchView;

    DatabaseReference databaseReference;

    AppCompatButton logOutBtn;
    ImageView backitem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        backitem=findViewById(R.id.backitem);
        backitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<UserModel> userModelList=userAdapter.getUserModelList();// show all users
                userAdapter.showAllUsers();
                searchView.setQuery("", false);
                onBackPressed();


            }
        });




        userAdapter=new UserAdapter(this);
        recyclerView=findViewById(R.id.recyclerview);

        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView=findViewById(R.id.action_search);
        searchView.setIconified(false);
        searchView.requestFocus();
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    filterList(newText);
                } else {

                    userAdapter.showAllUsers();
                }
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                userAdapter.showAllUsers();
                searchView.setQuery("", false); // clear the query
                return false;
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

    private void filterList(String text) {
        List<UserModel> filterdList= new ArrayList<>();
        for (UserModel userModel: userAdapter.getUserModelList()){
            if (userModel.getUserName().toLowerCase().contains(text.toLowerCase())||userModel.getUserEmail().toLowerCase().contains(text.toLowerCase())){
                filterdList.add(userModel);
            }
        }
        if (filterdList.isEmpty()){
            filterdList.clear();
            List<UserModel> userModelList=userAdapter.getUserModelList();
            userAdapter.notifyDataSetChanged();

            userAdapter.getUserModelList();// show all users

        }
        else{
            userAdapter.setFilterdList(filterdList);
        }
    }

    @Override
    public void onBackPressed() {
        searchView.setQuery("", false); // clear the search query
        userAdapter.showAllUsers(); // show all users
        super.onBackPressed();
    }
    @Override
    public void onResume() {
        super.onResume();
        userAdapter.showAllUsers(); // show all users
    }


}