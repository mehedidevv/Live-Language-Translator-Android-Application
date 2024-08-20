package com.example.languagetranslatorapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class MassageAdapter extends RecyclerView.Adapter<MassageAdapter.MyViewHolder> {

    private Context context;
    private List<MassageModel>MassageModelList;

    public MassageAdapter(Context context) {
        this.context = context;
        this.MassageModelList = new ArrayList<>();
    }

    public void add(MassageModel MassageModel){
        MassageModelList.add(MassageModel);
    }
    public void clear(){
        MassageModelList.clear();
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public MassageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.massage_layout,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MassageAdapter.MyViewHolder holder, int position) {
        MassageModel massageModel=MassageModelList.get(position);
        if (massageModel.getSender().equals(FirebaseAuth.getInstance().getUid()))
        {
            holder.send_massage.setText(massageModel.getMassage());
        }
        else {
            holder.recive_massage.setText(massageModel.getMassage());
        }

   
    }

    @Override
    public int getItemCount() {
        return MassageModelList.size();
    }

    public List<MassageModel> getMassageModelList()
    {
        return MassageModelList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftchatlaout,rightchatlayout;
        private TextView recive_massage,send_massage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftchatlaout=itemView.findViewById(R.id.leftchatlayout);
            rightchatlayout=itemView.findViewById(R.id.rightchatlayout);
            send_massage=itemView.findViewById(R.id.sent_massage);
            recive_massage=itemView.findViewById(R.id.recive_massage);
           
        }
    }
}
