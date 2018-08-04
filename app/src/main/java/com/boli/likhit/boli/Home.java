/*
English to Hindi Translator
Date-04-08-2018
Likhit C U
 */


package com.boli.likhit.boli;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class Home extends AppCompatActivity {

    //Request code for speech input
    private static final int REQ_CODE_SPEECH_INPUT=100;
    //view variables
    private TextView translated,translated2;
    private FloatingActionButton mike;
    //Language Translator api
    private String translate_api="https://api.mymemory.translated.net/get?";
    //private String translate_api="https://api.mymemory.translated.net/get?q=what%20is%20this&langpair=en|hi";
    private String audioOutput;
    //private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //tts=new TextToSpeech(this,this);
        //initiating views
        translated=(TextView)findViewById(R.id.translated);
        translated2=(TextView)findViewById(R.id.translated2);
        mike=(FloatingActionButton)findViewById(R.id.mike);
        //Event hnadling on taping mike
        mike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });
//        mike2=(FloatingActionButton)findViewById(R.id.mike2);
//        mike2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                speakOut();
//implements TextToSpeech.OnInitListener
//            }
//        });
    }

    public void startVoiceInput(){
        //setting up speech recognition intent
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hello,बोलीए");
        try{
            //starting new speech request
            startActivityForResult(intent,REQ_CODE_SPEECH_INPUT);
        }catch(ActivityNotFoundException e){

            Toast.makeText(getApplicationContext(),
                    "Sorry!Your device doesn't support Voice Input",Toast.LENGTH_SHORT).show();

        }

    }

    //fetching result of the speech input and its text conversion
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_CODE_SPEECH_INPUT:
                if(resultCode==RESULT_OK && null != data){
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    translated.setText(result.get(0));
                    audioOutput=result.get(0);
                    //calling translate method to translate english to hindi
                    translate(audioOutput);

                }
                break;
        }
    }

    //translation handling method
    void translate(String audiOutput){

        //setting up the final url as per the input
        audiOutput = audiOutput.replace(" ","%20");
        String json_url=translate_api+"q="+audiOutput+"&langpair=en|hi";

        //jsonObject volley request to fetch result out of api.
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, json_url, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //setting text in translation view.
                            JSONObject jObj=response.getJSONObject("responseData");
                            translated2.setText(jObj.getString("translatedText"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),"Translation Successful",Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Unable to process. Please check network connection.",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }

        );

        MySingleton.getInstance().addToRequestQueue(jsonObjectRequest);
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(jsonObjectRequest);
    }

//    @Override
//    protected void onDestroy() {
//        //shutting down tts
//        if(tts!=null){
//            tts.stop();
//            tts.shutdown();
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    public void onInit(int status) {
//        if(status==TextToSpeech.SUCCESS){
//            int result=tts.setLanguage(Locale.ENGLISH);
//
//            if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
//                Log.e("TTS","Language not supported");
//            }else{
//               // mike2.setEnabled(true);
//                speakOut();
//            }
//        }else{
//            Log.e("TTS","Initialisation Failed");
//        }
//    }
//
//    private void speakOut(){
//        String text=translated2.getText().toString();
//        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
//    }
}
