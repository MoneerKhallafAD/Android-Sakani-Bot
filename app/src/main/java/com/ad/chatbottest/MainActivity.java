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
                b.putString(SDKBotParams.ASSISTANT_API_KEY, "");
                b.putString(SDKBotParams.ASSISTANT_ID_AR, "");
                b.putString(SDKBotParams.ASSISTANT_URL, "");
                b.putString(SDKBotParams.ASSISTANT_VERSION_AR, "");
                b.putString(SDKBotParams.ASSISTANT_ID_EN, "");
                b.putString(SDKBotParams.ASSISTANT_VERSION_EN, "");

                b.putString(SDKBotParams.TEXT_TO_SPEECH_API_KEY, "");
                b.putString(SDKBotParams.TEXT_TO_SPEECH_SERVICES_URL, "");

                b.putString(SDKBotParams.SPEECH_TO_TEXT_API_KEY, "");
                b.putString(SDKBotParams.SPEECH_TO_TEXT_SERVICES_URL, "");
                in.putExtras(b);
                startActivity(in);
            }
        });

    }
}
