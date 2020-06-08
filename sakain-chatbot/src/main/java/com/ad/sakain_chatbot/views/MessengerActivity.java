package com.ad.sakain_chatbot.views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad.sakain_chatbot.R;
import com.ad.sakain_chatbot.utilities.SDKBotParams;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.speech_to_text.v1.websocket.RecognizeCallback;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MessengerActivity extends AppCompatActivity implements WatsonAssistantUtilities.WatsonAssistantListener {

    private RecyclerView mRvMessages;
    private ImageButton btnSend, mBtnBack, mBtnAudio;
    private EditText mEdtMessage;
    private LinearLayout mLinContainer, mLinTitle, mLinAudio, mLinText;
    private TextView mTxtTitle, mTxtSubTitle, mTxtTimer;
    private WatsonAssistantUtilities mAssistant;
    private AVLoadingIndicatorView mLoadingView;
    private MessengerAdapter mAdapter;
    private int time = 0;
    private Timer timer;
    private File mAudioFile;
    private MediaRecorder recorder = null;
    private MicrophoneHelper microphoneHelper;
    private MicrophoneInputStream capture;
    String lang = "ar";
    private boolean isForRecord = false;

    private ProgressDialog mProgessDialog;

    private static final int PERMISSION_REQUEST_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messanger);
        init();
    }


    void init() {
        lang = getIntent().getExtras().getString("Lang");
        btnSend = findViewById(R.id.btnSend);
        mRvMessages = findViewById(R.id.messengerView);
        mBtnBack = findViewById(R.id.btnBack);
        mBtnAudio = findViewById(R.id.btnRecord);
        mEdtMessage = findViewById(R.id.edtMessage);
        mTxtTitle = findViewById(R.id.txtTitle);
        mTxtSubTitle = findViewById(R.id.txtSubTitle);
        mTxtTimer = findViewById(R.id.txtRecordTimer);
        mLoadingView = findViewById(R.id.avLoading);
        mLinContainer = findViewById(R.id.linContainer);
        mLinTitle = findViewById(R.id.linTitle);
        mLinAudio = findViewById(R.id.linRecord);
        mLinText = findViewById(R.id.linTextInput);

        microphoneHelper = new MicrophoneHelper(this);

        mProgessDialog =  new ProgressDialog(this, R.style.MyTheme);
        mProgessDialog.setCancelable(false);
        mProgessDialog.show();
        configureIBMServices();

        mAdapter = new MessengerAdapter(this, mAssistant.getMessages(), lang);
        mAdapter.setListener(new MessengerAdapter.MessengerAdapterItemListener() {
            @Override
            public void onSpinnerSelectedItem(int messagePosition, int optionPosition) {
                mAssistant.sendMessage(mAssistant.getMessages().get(messagePosition).getOptions().get(optionPosition).getTitle());
            }

            @Override
            public void onAudioClicked(int position) {
                if (!isPermissionRequired(position)) {
                    convertTextToAudio(position);
                }
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        mRvMessages.setLayoutManager(manager);
        mRvMessages.setAdapter(mAdapter);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAssistant.buildUserMessage(mEdtMessage.getText().toString());
                mAssistant.buildTypingMessage();
                mAdapter.notifyItemInserted(mAssistant.getMessages().size() - 1);
                mAssistant.sendMessage(mEdtMessage.getText().toString());
                mEdtMessage.setText("");
            }
        });


        mBtnAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        capture = microphoneHelper.getInputStream(true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mAssistant.mSpeechToText.recognizeUsingWebSocket(getRecognizeOptions(capture),
                                            new MicrophoneRecognizeDelegate());

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mLinAudio.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    startTimer();
                                } catch (Exception e) {
                                    showError(e);
                                }
                            }
                        }).start();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (timer != null)  timer.cancel();
                        time = 0;
                        mLinAudio.setVisibility(View.GONE);
                        mLinText.setVisibility(View.VISIBLE);
                        mLoadingView.setVisibility(View.VISIBLE);
                        microphoneHelper.closeInputStream();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        return true;

                    default:
                        return false;
                }
            }
        });


        setLangRes(lang);
    }

    private void configureIBMServices() {
        mAssistant = new WatsonAssistantUtilities(lang, this);
        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            if (b!= null) {
                if (lang.equals("ar")) {
                    mAssistant.initWatsonAssistant(b.getString(SDKBotParams.ASSISTANT_API_KEY),
                            b.getString(SDKBotParams.ASSISTANT_ID_AR),
                            b.getString(SDKBotParams.ASSISTANT_URL),
                            b.getString(SDKBotParams.ASSISTANT_VERSION_AR));
                }else  {
                    mAssistant.initWatsonAssistant(b.getString(SDKBotParams.ASSISTANT_API_KEY),
                            b.getString(SDKBotParams.ASSISTANT_ID_EN),
                            b.getString(SDKBotParams.ASSISTANT_URL),
                            b.getString(SDKBotParams.ASSISTANT_VERSION_EN));
                }


                mAssistant.initTextToSpeech(b.getString(SDKBotParams.TEXT_TO_SPEECH_API_KEY),
                        b.getString(SDKBotParams.TEXT_TO_SPEECH_SERVICES_URL));

                mAssistant.initSpeechToText(b.getString(SDKBotParams.SPEECH_TO_TEXT_API_KEY),
                        b.getString(SDKBotParams.SPEECH_TO_TEXT_SERVICES_URL));
            }
        }
    }


    void convertTextToAudio(int position) {
        mAssistant.getMessages().get(position).setAudioMessageRequired(true);
        mAdapter.notifyItemChanged(position);
        mAssistant.synthesizeText(mAssistant.getMessages().get(position).getMessage(), position, MessengerActivity.this);
    }

    private RecognizeOptions getRecognizeOptions(InputStream captureStream) {
        return new RecognizeOptions.Builder()
                .audio(captureStream)
                .contentType(ContentType.OPUS.toString())
                .model(lang.equals("ar") ? RecognizeOptions.Model.AR_AR_BROADBANDMODEL : "en-US_BroadbandModel")
                .interimResults(true)
                .inactivityTimeout(2000)
                .build();
    }


    private boolean isPermissionRequired(int position) {
        int writeOnStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int recordAudio = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        ArrayList<String> arr = new ArrayList<String> ();
        if (writeOnStorage != PackageManager.PERMISSION_GRANTED) {
            arr.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (readStorage != PackageManager.PERMISSION_GRANTED) {
            arr.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            arr.add(Manifest.permission.RECORD_AUDIO);
        }

        if (arr.isEmpty()) {
            return false;
        }else  {
            String [] permissions = arr.toArray(new String[arr.size()]);
            ActivityCompat.requestPermissions(this, permissions, position);
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case PERMISSION_REQUEST_CODE:
                boolean writeOnStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean record = grantResults[2] == PackageManager.PERMISSION_GRANTED;

               if (isForRecord) {
                   if (record) {
                       if (lang.equals("ar")) {
                           Toast.makeText(MessengerActivity.this, "الرجاء الضغط مع الاستمرار علي زر الميكروفون لارسال رسالتك الصوتية", Toast.LENGTH_LONG).show();
                       }else {
                           Toast.makeText(MessengerActivity.this, "Please press long press on the mic button to send your voice note", Toast.LENGTH_LONG).show();
                       }
                   }else {
                       showDeniedMessage();
                   }
               }else {
                   if (writeOnStorage && readStorage) {
                       convertTextToAudio(requestCode);
                   }else {
                       showDeniedMessage();
                   }
               }
               break;
        }
    }


    private void showDeniedMessage() {
        if (lang.equals("ar")) {
            Toast.makeText(MessengerActivity.this, "الرجاء تمكين التصاريح حتي نتمكن من تشغيل الخدمة", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(MessengerActivity.this, "Please enable the permissions to activate the service", Toast.LENGTH_LONG).show();
        }
    }


    private void startRecording() {
        mAudioFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "message.mp3");
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(mAudioFile.getPath());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }


    private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback implements RecognizeCallback {
        @Override
        public void onTranscription(SpeechRecognitionResults speechResults) {
            System.out.println(speechResults);
            if (speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();

                showMicText(text);
            }
        }

        @Override
        public void onError(Exception e) {
            try {
                // This is critical to avoid hangs
                // (see https://github.com/watson-developer-cloud/android-sdk/issues/59)
                capture.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            showError(e);
            //enableMicButton();
        }

        @Override
        public void onDisconnected() {
            //enableMicButton();
        }
    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MessengerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                // Update the icon background

            }
        });
    }

    private void showMicText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MessengerActivity.this, text, Toast.LENGTH_SHORT).show();
                mEdtMessage.setText(text);
            }
        });
    }


    public void startTimer() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        long second = (time / 1000) % 60;
                        long minutes = (time / (1000 * 60)) % 60;
                        String timeString = String.format("%02d:%02d", minutes, second);
                        mTxtTimer.setText(timeString);
                        time += 1000;
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }


    void setLangRes(String lang) {
        if (lang.equals("ar")) {
            mLinContainer.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            mLinTitle.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            mBtnBack.setImageResource(R.drawable.ic_right_arrow);
        } else {
            mTxtTitle.setText("Ahmed (Virtual Assistant)");
            mTxtSubTitle.setText("Online");
            mBtnBack.setImageResource(R.drawable.ic_left_arrow);
            mLinContainer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            mLinTitle.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        }
    }

    @Override
    public void onCreateSession() {
        runOnUiThread(new Runnable() {
            public void run() {
                mProgessDialog.dismiss();
                mAssistant.buildTypingMessage();
                mAdapter.notifyItemInserted(mAssistant.getMessages().size() - 1);
                mRvMessages.scrollToPosition(mAssistant.getMessages().size() - 1);
            }
        });
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mAssistant.sendMessage("");
    }

    @Override
    public void onDeleteTyping() {
        runOnUiThread(new Runnable() {
            public void run() {
                mAdapter.notifyItemRemoved(mAssistant.getMessages().size() - 1);
                mRvMessages.scrollToPosition(mAssistant.getMessages().size() - 1);
                Log.d("UI thread", "I am the UI thread");
            }
        });
    }

    @Override
    public void onMessageSent() {
        runOnUiThread(new Runnable() {
            public void run() {
                mAdapter.notifyItemInserted(mAssistant.getMessages().size() - 1);
                mRvMessages.scrollToPosition(mAssistant.getMessages().size() - 1);
            }
        });
    }

    @Override
    public void onAudioLoaded(final int position) {
        runOnUiThread(new Runnable() {
            public void run() {
                mAdapter.notifyItemChanged(position);
            }
        });
    }

    @Override
    public void onAudioConverted(final String text) {
        runOnUiThread(new Runnable() {
            public void run() {
                mEdtMessage.setText(text);
            }
        });
    }

}
