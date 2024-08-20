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
import android.speech.tts.TextToSpeech;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    String MassageSenderId;
    String currentUserId;
    String MassageReciverId;
    String MassageReciverName;
    String senderRoom;
    String reciverRoom;
    String reciverLanguage;
    MassageModel last_massage;
    long last_spocken_timestamp;
    TextToSpeech tts;

    private Spinner fromSpinner,toSpinner;
    private EditText sourceEdt,translatedTV;
    private ImageView micIV,sendBtn,translateBtn;
   // private MaterialButton translateBtn;
    private TextView usernamemain,userlangugemain,userstatus;

    

    // String for Language From ML Kit
    String[] fromLanguages = {"From","English","Afrikaan","Arabic","Bengali","Catalen","Czech","Hindi","URDU"};
    String[] toLanguages = {"To","English","Afrikaan","Arabic","Bengali","Catalen","Czech","Hindi","URDU"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    int languageCode,fromLanguageCode,tolanguageCode = 0;



    FirebaseUser firebaseUser,currentUser;
    DatabaseReference databaseReference, dbReferanceSender, dbReferanceReciver, dbReferencechat, userStatusReference;
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
        userlangugemain=findViewById(R.id.userlanguagemain);
        userstatus=findViewById(R.id.onlinechek);

        massageAdapter=new MassageAdapter(this);
        massagerecyclerview=findViewById(R.id.recyclermassage);
        massagerecyclerview.setAdapter(massageAdapter);
        linearLayoutManager=new LinearLayoutManager(this);
        massagerecyclerview.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);
        massagerecyclerview.addOnScrollListener(massageAdapter.getOnScrollListener());
        massagerecyclerview.scrollToPosition(0);






        intent=getIntent();
        MassageReciverId=intent.getStringExtra("userid");
        reciverLanguage=intent.getStringExtra("userlanguage");
        MassageReciverName = intent.getStringExtra("username");
        MassageSenderId=FirebaseAuth.getInstance().getUid();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        usernamemain.setText(MassageReciverName);
        userlangugemain.setText(reciverLanguage);
         currentUser=FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser !=null)
        {
            currentUserId=currentUser.getUid();
        }
        initializeUserStatus();
        listenForUserStatus();


      // getSupportActionBar().setTitle(MassageReciverName);

        if (MassageReciverId!=null)
        {
            senderRoom=FirebaseAuth.getInstance().getUid()+MassageReciverId;
            reciverRoom=MassageReciverId+FirebaseAuth.getInstance().getUid();
        }


        dbReferanceSender=FirebaseDatabase.getInstance().getReference("Chat").child(senderRoom);
        dbReferanceReciver=FirebaseDatabase.getInstance().getReference("Chat").child(reciverRoom);

        dbReferanceReciver.orderByChild("timestamp");
           dbReferanceSender.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
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
                      massagerecyclerview.smoothScrollToPosition(massagerecyclerview.getAdapter().getItemCount());



               }


               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });


// On Click Listner to Send Button...............
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




// From Spinner ..............
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

        // To Spinner............
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


        // On Click Listner To Translate Button.................
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


        //On Click Listner To Mic Button...........
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

    // Method to initialize user status reference
    private void initializeUserStatus() {

        if (currentUser != null) {
            userStatusReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("status");
        }
    }

    // Method to set user status
    private void setUserStatus(String status) {
        if (userStatusReference != null) {
            userStatusReference.setValue(status);
        }
    }

    // Call this method in onStart() to set the user as online
    @Override
    protected void onStart() {
        super.onStart();
        setUserStatus("online");
    }

    // Call this method in onStop() to set the user as offline
    @Override
    protected void onStop() {
        super.onStop();
        setUserStatus("offline");
    }

    // Method to listen for user status changes
    private void listenForUserStatus() {
        if (MassageReciverId != null) {
            DatabaseReference receiverStatusReference = FirebaseDatabase.getInstance().getReference("users").child(MassageReciverId).child("status");
            receiverStatusReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String status = snapshot.getValue(String.class);
                        userstatus.setText(status != null && status.equals("online") ? "Live" : "Offline or not live");
                    } else {
                        userstatus.setText("Offline");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors.
                }
            });
        }
    }

    // Message RecyclerView



    // On Activity Result For Spinner
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


    //Method Of Translate Text For Translation..............
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

    // Method Of Get Language Code..........
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
    }   */


    //Method Of Send Message............
    private void sendMassage (String message){
      String messageId= UUID.randomUUID().toString();
        long currentTimestamp = System.currentTimeMillis();
      MassageModel massageModel=new MassageModel(FirebaseAuth.getInstance().getUid(),messageId,message,currentTimestamp);
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