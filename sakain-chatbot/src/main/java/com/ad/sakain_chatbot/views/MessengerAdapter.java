package com.ad.sakain_chatbot.views;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ad.sakain_chatbot.R;
import com.ad.sakain_chatbot.model.ChatBotMessage;
import com.ad.sakain_chatbot.model.MessageType;
import com.ad.sakain_chatbot.utilities.Utilities;
import com.bumptech.glide.Glide;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MessengerAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private static int TYPE_CHAT_BOT = 1;
    private static int TYPE_USER = 2;
    private static int TYPE_OPTION = 3;
    private static int TYPE_LOADING = 4;
    private static int TYPE_ّIMAGE = 5;
    private Context context;
    private String lang;
    private ArrayList<ChatBotMessage> mMessages;

    private MessengerAdapterItemListener mListener;

    public interface MessengerAdapterItemListener {
        void onSpinnerSelectedItem(int messagePosition, int optionPosition);
        void onAudioClicked(int position);
    }

    public void setListener(MessengerAdapterItemListener listener) {
        this.mListener = listener;
    }

    public MessengerAdapter(Context context, ArrayList<ChatBotMessage> messages, String lang) {
        this.context = context;
        this.mMessages = messages;
        this.lang = lang;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_CHAT_BOT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chatbot_message, viewGroup, false);
            return new ChatbotMessageHolder(view);
        } else if(viewType == TYPE_OPTION){
            view = LayoutInflater.from(context).inflate(R.layout.item_options, viewGroup, false);
            return new OptionMessageHolder(view);
        } else if(viewType == TYPE_LOADING){
            view = LayoutInflater.from(context).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }else if(viewType == TYPE_ّIMAGE){
            view = LayoutInflater.from(context).inflate(R.layout.item_image, viewGroup, false);
            return new ChatbotImageHolder(view);
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.item_user_message, viewGroup, false);
            return new UserMessageHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessages.get(position).getMessageType().equals(MessageType.TEXT)) {
            return TYPE_CHAT_BOT;
        }else if (mMessages.get(position).getMessageType().equals(MessageType.OPTIONS)) {
            return TYPE_OPTION;
        }else if (mMessages.get(position).getMessageType().equals(MessageType.LOADING)) {
            return TYPE_LOADING;
        }else {
            return TYPE_USER;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_CHAT_BOT) {
            ((ChatbotMessageHolder) viewHolder).bind(mMessages.get(position), lang,  mListener, position);
        }else if (getItemViewType(position) == TYPE_OPTION) {
            ((OptionMessageHolder) viewHolder).bind(mMessages.get(position), lang, context, mListener, position);
        }else if (getItemViewType(position) == TYPE_LOADING) {
            ((LoadingViewHolder) viewHolder).bind(lang);
        }else if (getItemViewType(position) == TYPE_ّIMAGE) {
            ((ChatbotImageHolder) viewHolder).bind(mMessages.get(position), lang);
        }else {
            ((UserMessageHolder) viewHolder).bind(mMessages.get(position), lang);
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class ChatbotMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtMessage, mTxtTime, mTxtAudioDuration;
        private LinearLayout mLinAudio;
        private ImageButton mBtnAudio;
        private ImageButton mBtnPlay;
        private ProgressBar mAudioBar;
        private AVLoadingIndicatorView mLoadingView;
        private MediaPlayer mediaPlayer = new MediaPlayer();
        float progress = 0.0f;

        public ChatbotMessageHolder(View view) {
            super(view);
            this.txtMessage = view.findViewById(R.id.txtMessage);
            this.mTxtTime = view.findViewById(R.id.txtTime);
            this.mTxtAudioDuration = view.findViewById(R.id.txtAudioDuration);
            this.mBtnAudio = view.findViewById(R.id.btnShowAudio);
            this.mBtnPlay = view.findViewById(R.id.btnPlay);
            this.mAudioBar = view.findViewById(R.id.prgBarTime);
            this.mLinAudio = view.findViewById(R.id.linAudioOptions);
            this.mLoadingView = view.findViewById(R.id.avAudioLoading);
        }


        void bind(ChatBotMessage message, String lang, final MessengerAdapterItemListener listener, final int position) {
            txtMessage.setText(Html.fromHtml(message.getMessage()));
            mTxtTime.setText(message.getMessageDate());
            if(message.getLoadingVoice()) {
                mLinAudio.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                initiatePlayer(message.getAudioFile());
                calculateProgress(0, mTxtAudioDuration);
            }else {
                mLinAudio.setVisibility(View.GONE);
            }
            if(message.isAudioMessageRequired()) {
                mLoadingView.setVisibility(View.VISIBLE);
            }

            if (lang.equals("ar")) {
                txtMessage.setBackgroundResource(R.drawable.bg_chatbot_message);
            }else {
                txtMessage.setBackgroundResource(R.drawable.bg_ar_chatbotmessage);
            }
            mBtnAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAudioClicked(position);
                }
            });

            mBtnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play(mTxtAudioDuration);
                }
            });
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "position : " + getLayoutPosition() + " text : " + this.txtMessage.getText(), Toast.LENGTH_SHORT).show();
        }

        private void initiatePlayer(File file) {
            try {
                mediaPlayer.reset();
                FileInputStream fis = new FileInputStream(file);
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.prepare();


                Log.e("Media Duration ", " " + mediaPlayer.getDuration());
            } catch (IOException ex) {
                String s = ex.toString();
                ex.printStackTrace();
            }
        }

        void play(final TextView txt){
            mediaPlayer.start();
            new CountDownTimer(mediaPlayer.getDuration(), 1000) {
                public void onTick(long millisUntilFinished) {
                    calculateProgress(millisUntilFinished, txt);
                }

                public void onFinish() {
                }

            }.start();
        }

        void calculateProgress(long millisUntilFinished, TextView txt) {
            long second = (millisUntilFinished / 1000) % 60;
            long minutes = (millisUntilFinished/(1000*60)) % 60;
            txt.setText(minutes + ":" + second);
            long totalMin = (mediaPlayer.getDuration()/(1000*60)) % 60;
            long totalScn = (mediaPlayer.getDuration()/(1000)) % 60;
            String timeString = String.format("%02d:%02d", minutes, second);
            String totalTimeString = String.format("%02d:%02d", totalMin, totalScn);
            txt.setText(totalTimeString+ "/" + timeString);
            progress += ((float)1000 / (float)mediaPlayer.getDuration()) * 100;
            Log.e("DDDDD", "" + ((float)1000 / (float)mediaPlayer.getDuration()) * 100);
            Log.e("PROGRESSS       :: ", "" + progress);
            mAudioBar.setProgress((int)progress);
        }


    }

    public static class UserMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtMessage , mTxtTime;
        private LinearLayout lin;

        public UserMessageHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.txtMessage = view.findViewById(R.id.txtUserMessage);
            this.mTxtTime = view.findViewById(R.id.txtUserTime);
            this.lin = view.findViewById(R.id.linUserText);
        }

        void bind(ChatBotMessage message, String lang) {
            txtMessage.setText(Html.fromHtml(message.getMessage()));
            mTxtTime.setText(message.getMessageDate());
            if (lang.equals("ar")) {
                lin.setGravity(Gravity.END);
                txtMessage.setBackgroundResource(R.drawable.bg_user_message);
                LinearLayout.LayoutParams parameter =  (LinearLayout.LayoutParams) txtMessage.getLayoutParams();
                parameter.setMargins((int) Utilities.convertDpToPixel(80, txtMessage.getContext()),0,20, 0);
                txtMessage.setLayoutParams(parameter);
            }else {
                lin.setGravity(Gravity.END);
                txtMessage.setBackgroundResource(R.drawable.bg_ar_user_message);
                LinearLayout.LayoutParams parameter =  (LinearLayout.LayoutParams) txtMessage.getLayoutParams();
                parameter.setMargins( 20,0,(int)Utilities.convertDpToPixel(80, txtMessage.getContext()), 0);
                txtMessage.setLayoutParams(parameter);
            }
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "position : " + getLayoutPosition() + " text : " + this.txtMessage.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    public static class OptionMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtMessage, mTxtTime;
        private Spinner mSp;

        public OptionMessageHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.txtMessage = view.findViewById(R.id.txtOptionMessage);
            this.mTxtTime = view.findViewById(R.id.txtOptionTime);
            this.mSp = view.findViewById(R.id.spOptions);
        }

        void bind(ChatBotMessage message, String lang, Context context, final MessengerAdapterItemListener listener, final int messagePosition) {
            txtMessage.setText(Html.fromHtml(message.getMessageTitle()));
            mTxtTime.setText(message.getMessageDate());
            if (lang.equals("ar")) {
                txtMessage.setBackgroundResource(R.drawable.bg_chatbot_message);
            } else {
                txtMessage.setBackgroundResource(R.drawable.bg_ar_chatbotmessage);
            }
            OptionsSpAdapter spAdapter = new OptionsSpAdapter(context, message.getOptions());
            mSp.setAdapter(spAdapter);
            mSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0)
                        listener.onSpinnerSelectedItem(messagePosition, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }


        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "position : " + getLayoutPosition() + " text : " + this.txtMessage.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        private AVLoadingIndicatorView mLoadingView;

        public LoadingViewHolder(View view) {
            super(view);
            this.mLoadingView = view.findViewById(R.id.avLoading);
        }


        void bind(String lang) {
            mLoadingView.show();

        }
    }

    public static class ChatbotImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtMessage, mTxtTime, mTxtTitle;
        private ImageView mImg;
        private LinearLayout mLinCon;

        public ChatbotImageHolder(View view) {
            super(view);
            this.txtMessage = view.findViewById(R.id.txtMessage);
            this.mTxtTime = view.findViewById(R.id.txtTime);
            this.mTxtTitle = view.findViewById(R.id.txtTitle);
            this.mImg = view.findViewById(R.id.imgImage);
            this.mLinCon = view.findViewById(R.id.top_view);
        }

        void bind(ChatBotMessage message, String lang) {
            txtMessage.setText(Html.fromHtml(message.getMessage()));
            mTxtTime.setText(message.getMessageTitle());
            if (lang.equals("ar")) {
                mLinCon.setBackgroundResource(R.drawable.bg_chatbot_message);
            }else {
                mLinCon.setBackgroundResource(R.drawable.bg_ar_chatbotmessage);
            }
            Glide.with(mImg.getContext()).load(message.getMessageImageSource()).centerCrop().into(mImg);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "position : " + getLayoutPosition() + " text : " + this.txtMessage.getText(), Toast.LENGTH_SHORT).show();
        }
    }
}