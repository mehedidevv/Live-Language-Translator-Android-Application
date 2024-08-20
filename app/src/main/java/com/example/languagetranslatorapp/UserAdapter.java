package com.example.languagetranslatorapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {


    private Context context;
    private List<UserModel>userModelList;
    private List<UserModel> filteredList;
    String thelastmassage;

    private DatabaseReference userStatusReference;






    public UserAdapter(Context context) {
        this.context = context;
        this.userModelList = new ArrayList<>();
        this.userStatusReference = FirebaseDatabase.getInstance().getReference("users");
        this.filteredList = new ArrayList<>(userModelList);
    }
    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
        this.filteredList = new ArrayList<>(userModelList);
        notifyDataSetChanged();
    }
    public void setFilterdList(List<UserModel> filterdList){
        this.userModelList =filterdList;
        notifyDataSetChanged();
    }
    public void showAllUsers() {
        filteredList.clear();
        filteredList.addAll(userModelList);
        notifyDataSetChanged();
    }
    public void add(UserModel userModel){
        userModelList.add(userModel);
    }
    public void clear(){
        userModelList.clear();
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {
        UserModel userModel=userModelList.get(position);
        holder.name.setText(userModel.getUserName());
        holder.email.setText(userModel.getUserEmail());
        lastmassage(userModel.getUserID(), userModel.getUserID()+FirebaseAuth.getInstance().getUid(), holder.last_massage);
        listenForUserStatus(userModel.getUserID(), holder.statusTV);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,MainActivity.class);
                intent.putExtra("userid",userModel.getUserID());
                intent.putExtra("username",userModel.getUserName());
                intent.putExtra("userlanguage",userModel.getUserLanguage());
                context.startActivity(intent);

            }
        });

    }

    private void listenForUserStatus(String userID, TextView statusTV) {
        DatabaseReference userStatusRef = userStatusReference.child(userID).child("status");
        userStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    statusTV.setText(status != null && status.equals("online") ? "Online" : "Offline");
                } else {
                    statusTV.setText("Offline");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public List<UserModel> getUserModelList()
    {
        return userModelList;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name,email,last_massage,statusTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.username);
            email=itemView.findViewById(R.id.useremail);
            last_massage=itemView.findViewById(R.id.userlastmassge);
            statusTV=itemView.findViewById(R.id.status);

        }

    }
    private void lastmassage(String userID,String senderRoom, final TextView last_massage) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chat").child(senderRoom);
        databaseReference.orderByChild("timestamp").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MassageModel massageModel = dataSnapshot.getValue(MassageModel.class);
                    last_massage.setText(massageModel.getMassage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
