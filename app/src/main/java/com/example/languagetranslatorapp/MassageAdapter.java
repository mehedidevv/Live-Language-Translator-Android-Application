package com.example.languagetranslatorapp;

import android.content.Context;

import android.speech.tts.TextToSpeech;
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
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class MassageAdapter extends RecyclerView.Adapter<MassageAdapter.MyViewHolder> {

    TextToSpeech tts;
    private Context context;
    private boolean isScrolling = false;
    private List<MassageModel>MassageModelList;
    private MassageAdapter massageModelList;
   
    private boolean newMessageAvailable = false;

    public MassageAdapter(Context context) {
        this.context = context;
        this.MassageModelList = new ArrayList<>();
        initTextToSpeech();
    }


    public RecyclerView.OnScrollListener getOnScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isScrolling = false;
                    if (newMessageAvailable && !MassageModelList.isEmpty()) {
                        MassageModel recentMessage =MassageModelList.get(MassageModelList.size() - 1);
                        if (!recentMessage.getSender().equals(FirebaseAuth.getInstance().getUid())) {
                            speakMessage(recentMessage.getMassage());
                            newMessageAvailable = false;
                        }
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    isScrolling = true;
                    if (tts.isSpeaking()) {
                        tts.stop();
                    }
                }
            }
        };
    }




    private void initTextToSpeech() {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.getDefault());
                }
            }
        });
    }




    public void add(MassageModel MassageModel){
        MassageModelList.add(MassageModel);
        notifyItemInserted(MassageModelList.size() - 1);
        newMessageAvailable = true;

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MassageModel massageModel=MassageModelList.get(position);
        if (massageModel.getSender().equals(FirebaseAuth.getInstance().getUid()))
        {
            holder.leftchatlaout.setVisibility(View.GONE);
            holder.rightchatlayout.setVisibility(View.VISIBLE);
            holder.send_massage.setText(massageModel.getMassage());

        }
        else {

            holder.leftchatlaout.setVisibility(View.VISIBLE);
            holder.rightchatlayout.setVisibility(View.GONE);
            holder.recive_massage.setText(massageModel.getMassage());

            if (!isScrolling && position == MassageModelList.size() - 1) {
                speakMessage(massageModel.getMassage());
            }






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
    public void speakMessage(String message) {
        if (tts != null) {
            if (!isScrolling){
                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);

            }


        }
    }

    public void setScrolling(boolean isScrolling) {
        this.isScrolling = isScrolling;
    }

}
