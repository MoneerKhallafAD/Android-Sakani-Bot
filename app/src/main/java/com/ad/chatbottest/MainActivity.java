package com.ad.chatbottest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ad.sakain_chatbot.utilities.SDKBotParams;
import com.ad.sakain_chatbot.views.SDKInitialActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOpenSDK = findViewById(R.id.btnOpenSDK);
        btnOpenSDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, SDKInitialActivity.class);
                Bundle b = new Bundle();
                b.putString(SDKBotParams.ASSISTANT_API_KEY, "4TY-KgGnEibmqWvqnD9mVCr0E9AacQ1prpWGTR771sGP");
                b.putString(SDKBotParams.ASSISTANT_ID_AR, "72179334-5c78-4384-987f-4f9066869163");
                b.putString(SDKBotParams.ASSISTANT_URL, "https://api.eu-gb.assistant.watson.cloud.ibm.com/instances/85165291-e725-4bdf-b813-776db1da1ffc");
                b.putString(SDKBotParams.ASSISTANT_VERSION_AR, "2020-01-10");
                b.putString(SDKBotParams.ASSISTANT_ID_EN, "e67d2e34-b3f8-4d16-ba52-16f43fe6e387");
                b.putString(SDKBotParams.ASSISTANT_VERSION_EN, "2020-01-10");

                b.putString(SDKBotParams.TEXT_TO_SPEECH_API_KEY, "qhWtLbdkFPCBtYPlK7upIycTWtyEJoOpMSTnCfSZYy5V");
                b.putString(SDKBotParams.TEXT_TO_SPEECH_SERVICES_URL, "https://gateway-lon.watsonplatform.net/text-to-speech/api");

                b.putString(SDKBotParams.SPEECH_TO_TEXT_API_KEY, "fpSW-iAKwBNL4nbLJFDDkBUL6gHWZqyM0809xDBPUD0X");
                b.putString(SDKBotParams.SPEECH_TO_TEXT_SERVICES_URL, "https://api.eu-gb.speech-to-text.watson.cloud.ibm.com");
                in.putExtras(b);
                startActivity(in);
            }
        });

    }
}
