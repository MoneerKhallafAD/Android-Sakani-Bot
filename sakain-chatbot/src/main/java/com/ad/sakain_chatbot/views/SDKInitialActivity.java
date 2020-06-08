package com.ad.sakain_chatbot.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ad.sakain_chatbot.R;
import com.ad.sakain_chatbot.utilities.SDKBotParams;

public class SDKInitialActivity extends AppCompatActivity {

    private Button btnStartChatAr;
    private Button btnStartChatEn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_initial);
        init();

    }

    void init() {
        btnStartChatAr = findViewById(R.id.btnStartAr);
        btnStartChatEn = findViewById(R.id.btnStartEn);
        if (getIntent().getExtras() != null) {
            final Bundle b = getIntent().getExtras();
            if (b!= null) {
                btnStartChatAr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(SDKInitialActivity.this, MessengerActivity.class);
                        in.putExtra("Lang","ar");
                        in.putExtras(b);
                        startActivity(in);
                    }
                });

                btnStartChatEn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(SDKInitialActivity.this, MessengerActivity.class);
                        in.putExtra("Lang","en");
                        in.putExtras(b);
                        startActivity(in);
                    }
                });
            }
        }

    }

}
