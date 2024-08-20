package com.example.languagetranslatorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    String MassageSenderId,MassageReciverId,MassageReciverName,senderRoom,reciverRoom;

    private Spinner fromSpinner,toSpinner;
    private EditText sourceEdt;
    private ImageView micIV;
    private MaterialButton translateBtn,sendBtn;
    private TextView translatedTV,usernamemain;
    String[] fromLanguages = {"From","English","Afrikaan","Arabic","Bengali","Catalen","Czech","Hindi","URDU"};
    String[] toLanguages = {"To","English","Afrikaan","Arabic","Bengali","Catalen","Czech","Hindi","URDU"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    int languageCode,fromLanguageCode,tolanguageCode = 0;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference,dbReferanceSender,dbReferanceReciver;

    Intent intent;

    private final List<MassageModel> massagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MassageAdapter massageAdapter;

    private RecyclerView massagerecyclerview;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromSpinner = findViewById(R.id.idFromSpinner);
        toSpinner = findViewById(R.id.idTopSpinner);
        sourceEdt = findViewById(R.id.idEdtSource);
        micIV = findViewById(R.id.idIVMic);
        translateBtn = findViewById(R.id.idBtnTranslate);
        translatedTV = findViewById(R.id.idTVTranslatedTV);
        sendBtn=findViewById(R.id.sendBtn);
       usernamemain=findViewById(R.id.usernamemain);

       massageAdapter=new MassageAdapter(this);
        massagerecyclerview=findViewById(R.id.recyclermassage);
        massagerecyclerview.setAdapter(massageAdapter);
        massagerecyclerview.setLayoutManager(new LinearLayoutManager(this));




        intent=getIntent();
        MassageReciverId=intent.getStringExtra("userid");
        MassageReciverName = intent.getStringExtra("username");
        MassageSenderId=FirebaseAuth.getInstance().getUid();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
      // getSupportActionBar().setTitle(MassageReciverName);
        if (MassageReciverId!=null)
        {
            senderRoom=FirebaseAuth.getInstance().getUid()+MassageReciverId;
            reciverRoom=MassageReciverId+FirebaseAuth.getInstance().getUid();
        }

        dbReferanceSender=FirebaseDatabase.getInstance().getReference("Chat").child(senderRoom);
        dbReferanceReciver=FirebaseDatabase.getInstance().getReference("Chat").child(reciverRoom);
           dbReferanceSender.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                      List<MassageModel> messages=new ArrayList<>();
                      for (DataSnapshot dataSnapshot:snapshot.getChildren())
                      {
                          MassageModel massageModel= dataSnapshot.getValue(MassageModel.class);
                          messages.add(massageModel);
                      }
                      massageAdapter.clear();
                      for (MassageModel message:messages)
                      {
                           massageAdapter.add(message);
                      }
                        massageAdapter.notifyDataSetChanged();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel=snapshot.getValue(UserModel.class);
                usernamemain.setText(userModel.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this, "Database Error", Toast.LENGTH_SHORT).show();

            }

        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=translatedTV.getText().toString();
                if (!msg.equals("")){
                    sendMassage(msg);
                }
                else {
                    Toast.makeText(MainActivity.this,"You can't send empty massage",Toast.LENGTH_SHORT).show();
                }
                translatedTV.setText("");
            }



        });









        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getLanguageCode(fromLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter( this,R.layout.spinner_item,fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tolanguageCode = getLanguageCode(toLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this,R.layout.spinner_item,toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);


        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatedTV.setText("");
                String sourceText = sourceEdt.getText().toString(); // Retrieve the text from the TextInputEditText
                if (sourceText.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter your text to translate", Toast.LENGTH_SHORT).show();
                } else if (fromLanguageCode == 0){
                    Toast.makeText(MainActivity.this, "Please select the source language", Toast.LENGTH_SHORT).show();
                } else if (tolanguageCode == 0){
                    Toast.makeText(MainActivity.this, "Please select the target language", Toast.LENGTH_SHORT).show();
                } else {
                    translateText(fromLanguageCode, tolanguageCode, sourceText); // Pass the retrieved text to the translateText() method
                }
            }
        });


        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak to convert into text");
                try{
                    startActivityForResult(i,REQUEST_PERMISSION_CODE);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_PERMISSION_CODE){
            if (resultCode==RESULT_OK && data!=null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourceEdt.setText(result.get(0));

            }

        }
    }

    private void translateText(int fromLanguageCode, int tolanguageCode, String source){
        translatedTV.setText("Downloading Model..");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(tolanguageCode)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translatedTV.setText("Translating..");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translatedTV.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Fail to translate"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Fail to download language model"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    //String[] fromLanguages = {"From","English","Afrikaan","Arabic","Bengali","Catalen","Czech","Hindi","URDU"};
    public int getLanguageCode(String language){
        int languageCode = 0;
        switch (language){
            case "English":
                languageCode = FirebaseTranslateLanguage.EN;
            break;
            case "Afrikaan":
                languageCode = FirebaseTranslateLanguage.AF;
                break;
            case "Arabic":
                languageCode = FirebaseTranslateLanguage.AR;
                break;
            case "Bengali":
                languageCode = FirebaseTranslateLanguage.BN;
                break;
            case "Catalen":
                languageCode = FirebaseTranslateLanguage.CA;
                break;
            case "Czech":
                languageCode = FirebaseTranslateLanguage.CS;
                break;
            case "Hindi":
                languageCode = FirebaseTranslateLanguage.HI;
                break;
            case "URDU":
                languageCode = FirebaseTranslateLanguage.UR;
                break;
            default:
                languageCode = 0;
        }
        return languageCode;

    }

   /* private void sendMassage(String sender,String reciver,String massage){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("reciver",reciver);
        hashMap.put("massage",massage);
        databaseReference.child("Massages").push().setValue(hashMap);
    }     */
    private void sendMassage (String message){
      String messageId= UUID.randomUUID().toString();
      MassageModel massageModel=new MassageModel(messageId,FirebaseAuth.getInstance().getUid(),message);
       massageAdapter.add(massageModel);

       dbReferanceSender.child(messageId).setValue(massageModel)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                      Toast.makeText(MainActivity.this, "Fail to send massage", Toast.LENGTH_SHORT).show();
                   }
               });
       dbReferanceReciver.child(messageId).setValue(massageModel);
       massagerecyclerview.scrollToPosition(massageAdapter.getItemCount()-1);
       translatedTV.setText("");

    }










}