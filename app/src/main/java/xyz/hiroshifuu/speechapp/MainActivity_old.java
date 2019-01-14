package xyz.hiroshifuu.speechapp;


import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity_old extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView status_tv;
    private TextView result_tv;
    private TextView result_server_tv;
    private EditText edtQuery;
    private Button start_listen_btn, stop_listen_btn;
    private SpeechRecognizerManager mSpeechManager;
    private TextToSpeech tts;
    PreparedStatement pst;
    Client myClient = null;
    String res = "";
    String query;
    public String Query;
    private static final int SERVERPORT = 5586;
    private static final String SERVER_IP = "172.17.128.222";
    public String qryresp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setClickListeners();
        tts = new TextToSpeech(getApplicationContext(), this);
    }

    private void findViews() {
        status_tv = findViewById(R.id.status_tv);
        result_tv = findViewById(R.id.result_tv);
        result_server_tv = findViewById(R.id.result_server_tv);
        start_listen_btn = findViewById(R.id.start_listen_btn);
        stop_listen_btn = findViewById(R.id.stop_listen_btn);
        //edtQuery=findViewById(R.id.edtQuery);
        // edtQuery.setText("Text");
        //query= edtQuery.getText().toString();
    }

    private void setClickListeners() {
        final Activity that = this;
        start_listen_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (PermissionHandler.checkPermission(that, PermissionHandler.RECORD_AUDIO)) {

                    //SetSpeechListener();

                    switch (v.getId()) {
                        case R.id.start_listen_btn:
                            if (mSpeechManager == null) {
                                SetSpeechListener();


                            } else if (!mSpeechManager.ismIsListening()) {
                                mSpeechManager.destroy();
                                SetSpeechListener();
                            }
                            status_tv.setText(getString(R.string.you_may_speak));
                            start_listen_btn.setClickable(false);
                            start_listen_btn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                            break;
                        case R.id.stop_listen_btn:
                            if (mSpeechManager != null) {
                                status_tv.setText(getString(R.string.destroied));
                                mSpeechManager.destroy();
                                mSpeechManager = null;
                            }
                            break;
                    }
                } else {
                    PermissionHandler.askForPermission(PermissionHandler.RECORD_AUDIO, that);
                }
            }


        });
    }

    private void SetSpeechListener() {
        mSpeechManager = new SpeechRecognizerManager(this, new SpeechRecognizerManager.onResultsReady() {
            @Override
            public void onResults(ArrayList<String> results) {
                if (results != null && results.size() > 0) {
                    res = results.get(0);
                    //res=query;
                    result_tv.setText(res);
                    //if(myClient==null) {
//                    myClient = new Client(SERVER_IP, SERVERPORT, res, main);
//                    myClient.execute();
                    //}
                    //else
                    //{
                    //myClient.execute(res);
                    //  myClient.sendDataWithString(res);
                    // myClient.doInBackground();
                    //}


                    qryresp = myClient.response;

                    TTS_speak(qryresp);
                    //=result_server_tv.getText().toString();
                    //status_tv.setText("Response is: " + res);
                    ///result_server_tv.setText("Response is: " + myClient.response); // response is blank why
                    // Log.i("Main ACT RES", res);
                    // res="";
                    // res="";

                } else {
                    status_tv.setText(getString(R.string.no_results_found));
                }
                status_tv.setText(getString(R.string.destroied));
                mSpeechManager.destroy();
                mSpeechManager = null;
                start_listen_btn.setClickable(true);
                start_listen_btn.getBackground().setColorFilter(null);
            }
        });

    }

    @Override
    protected void onPause() {
        if (mSpeechManager != null) {
            mSpeechManager.destroy();
            mSpeechManager = null;
        }
        mSpeechManager = null;
        super.onPause();

        if (tts != null) {
            tts.shutdown();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (tts.isLanguageAvailable(Locale.UK) == TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.UK);
        } else if (status == TextToSpeech.ERROR) {
            // Toast.makeText(this, "Sorry! Text To Speech failed...",
            //       Toast.LENGTH_LONG).show();
        }
        TTS_speak("WELCOME");
    }

    public void TTS_speak(String speech) {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);

        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_VOLUME, amStreamMusicMaxVol);

        tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
    }



}