package com.ad.sakain_chatbot.views;

import android.app.Activity;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import com.ad.sakain_chatbot.BuildConfig;
import com.ad.sakain_chatbot.model.ChatBotMessage;
import com.ad.sakain_chatbot.model.MessageOption;
import com.ad.sakain_chatbot.model.MessageType;
import com.ad.sakain_chatbot.model.SenderType;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.DialogNodeOutputOptionsElement;
import com.ibm.watson.assistant.v2.model.DialogSuggestion;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;
import com.ibm.watson.assistant.v2.model.SessionResponse;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.text_to_speech.v1.util.WaveUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WatsonAssistantUtilities {

    private ArrayList<ChatBotMessage> mMessages = new ArrayList<>();
    private String mLang = "ar";
    private Assistant mAssistant;
    private String mSessionID;
    private String mAssistantID;
    private WatsonAssistantListener mListener;
    private TextToSpeech mTextToSpeech;
    SpeechToText mSpeechToText;


    public interface WatsonAssistantListener {
        void onCreateSession();

        void onMessageSent();

        void onAudioLoaded(int position);

        void onAudioConverted(String text);

        void onDeleteTyping();
    }


    public WatsonAssistantUtilities(String lang, WatsonAssistantListener listener) {
        this.mLang = lang;
        this.mListener = listener;

    }


    void initWatsonAssistant(String apiKey, final String assistantID, String assistantUrl, String version) {
        IamAuthenticator authenticator = new IamAuthenticator(apiKey);
        mAssistant = new Assistant(version, authenticator);
        mAssistant.setServiceUrl(assistantUrl);
        new Thread(new Runnable() {
            @Override
            public void run() {
                CreateSessionOptions options = new CreateSessionOptions.Builder(assistantID).build();
                SessionResponse response = mAssistant.createSession(options).execute().getResult();
                mSessionID = response.getSessionId();
                if (BuildConfig.DEBUG) {
                    Log.e("Session IDDD ", mSessionID);

                }
                mListener.onCreateSession();
            }
        }).start();
        mAssistantID = assistantID;

    }

    void initTextToSpeech(String apiKey, String serviceURL) {
        IamAuthenticator authenticator = new IamAuthenticator(apiKey);
        mTextToSpeech = new TextToSpeech(authenticator);
        mTextToSpeech.setServiceUrl(serviceURL);
    }

    void initSpeechToText(String apiKey, String serviceURL) {
        IamAuthenticator authenticator = new IamAuthenticator(apiKey);
        mSpeechToText = new SpeechToText(authenticator);
        mSpeechToText.setServiceUrl(serviceURL);
    }

    void sendMessage(final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MessageInput input = new MessageInput.Builder().messageType("text").text(msg).build();
                MessageOptions options = new MessageOptions.Builder(mAssistantID, mSessionID)
                        .input(input)
                        .build();
                MessageResponse response = mAssistant.message(options).execute().getResult();
                if (mMessages.get(mMessages.size() - 1).getMessageType().equals(MessageType.LOADING)) {
                    mMessages.remove(mMessages.size() - 1);
                }
                buildMessage(response);
                mListener.onDeleteTyping();
                mListener.onMessageSent();
                if (BuildConfig.DEBUG)
                    Log.e("asdasd asdasd asd ", response.getOutput().getGeneric().toString());

            }
        }).start();

    }

    void synthesizeText(final String textToVoice, final int itemIndex, Activity context) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SynthesizeOptions synthesizeOptions =
                            new SynthesizeOptions.Builder().text(textToVoice).accept("audio/mp3")
                                    .voice(mLang.equals("ar") ? "ar-AR_OmarVoice" : "en-US_AllisonVoice")
                                    .build();

                    InputStream inputStream = mTextToSpeech.synthesize(synthesizeOptions).execute().getResult();
                    InputStream in = WaveUtils.reWriteWaveHeader(inputStream);
                    writeFileExternalStorage(itemIndex, "voice_message" + itemIndex + ".mp3", in);
                    in.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void recognizeText(final File audioFile) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    RecognizeOptions recognizeOptions = new RecognizeOptions.Builder()
//                            .audio(audioFile)
//                            .contentType("audio/mp3")
//                            //.model(mLang.equals("ar") ? RecognizeOptions.Model.AR_AR_BROADBANDMODEL: RecognizeOptions.Model.EN_US_BROADBANDMODEL)
//                            .build();


                    RecognizeOptions recognizeOptions = new RecognizeOptions.Builder()
                            .audio(new FileInputStream(audioFile.getAbsolutePath()))
                            .contentType("audio/mp3")
                            .model(mLang.equals("ar") ? RecognizeOptions.Model.AR_AR_BROADBANDMODEL : RecognizeOptions.Model.EN_US_BROADBANDMODEL)
                            .interimResults(true)
                            .inactivityTimeout(2000)
                            //TODO: Uncomment this to enable Speaker Diarization
//                            .speakerLabels(true)
                            .build();

                    SpeechRecognitionResults speechRecognitionResults = mSpeechToText.recognize(recognizeOptions).execute().getResult();
                    mListener.onAudioConverted(speechRecognitionResults.getResults().get(0).getAlternatives().get(0).getTranscript());
                    System.out.println(speechRecognitionResults);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    void writeFileExternalStorage(int itemIndex, String fileName, InputStream in) {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), fileName);

        FileOutputStream outputStream = null;
        try {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            if (!file.exists())
                file.createNewFile();
            FileUtils.copyInputStreamToFile(in, file);
            mMessages.get(itemIndex).setAudioMessageRequired(false);
            mMessages.get(itemIndex).setLoadingVoice(true);
            mMessages.get(itemIndex).setAudioFile(file);
            mListener.onAudioLoaded(itemIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void buildMessage(MessageResponse response) {
        if (response == null)
            return;
        if (response.getOutput() == null)
            return;
        if (response.getOutput().getGeneric() == null)
            return;
        for (int i = 0; i < response.getOutput().getGeneric().size(); i++) {
            RuntimeResponseGeneric item = response.getOutput().getGeneric().get(i);
            if (item.responseType().equals(RuntimeResponseGeneric.ResponseType.TEXT)) {
                ChatBotMessage message = new ChatBotMessage(item.text(),
                        "", MessageType.TEXT, "", getDate(), SenderType.CHAT_BOT);
                mMessages.add(message);
            } else if (item.responseType().equals(RuntimeResponseGeneric.ResponseType.OPTION)) {
                ChatBotMessage message = new ChatBotMessage(item.title(),
                        "", MessageType.OPTIONS, "", getDate(), SenderType.CHAT_BOT);
                message.setOptions(buildOptionsMessage(item.options()));
                message.setMessageTitle(item.title());
                mMessages.add(message);
            } else if (item.responseType().equals(RuntimeResponseGeneric.ResponseType.SUGGESTION)) {
                ChatBotMessage message = new ChatBotMessage(item.title(),
                        "", MessageType.OPTIONS, "", getDate(), SenderType.CHAT_BOT);
                message.setOptions(buildSuggestionsMessage(item.suggestions()));
                message.setMessageTitle(item.title());
                mMessages.add(message);
            } else if (item.responseType().equals(RuntimeResponseGeneric.ResponseType.IMAGE)) {

            }
        }

    }

    String getDate() {
        DateFormat df = new DateFormat();
        return df.format("hh:mm a", new Date()).toString();
    }


    public ArrayList<ChatBotMessage> getMessages() {
        return mMessages;
    }

    private ArrayList<MessageOption> buildOptionsMessage(List<DialogNodeOutputOptionsElement> optionsElements) {
        ArrayList<MessageOption> options = new ArrayList<>();
        options.add(new MessageOption(mLang.equals("ar") ? "إختر من فضلك" : "Choose Please"));
        for (int x = 0; x < optionsElements.size(); x++) {
            MessageOption op = new MessageOption(optionsElements.get(x).getLabel());
            options.add(op);
        }
        return options;
    }

    private ArrayList<MessageOption> buildSuggestionsMessage(List<DialogSuggestion> suggestionList) {
        ArrayList<MessageOption> options = new ArrayList<>();
        options.add(new MessageOption(mLang.equals("ar") ? "إختر من فضلك" : "Choose Please"));
        for (int x = 0; x < suggestionList.size(); x++) {
            MessageOption op = new MessageOption(suggestionList.get(x).getLabel());
            options.add(op);
        }
        return options;
    }

    void buildUserMessage(String userMessage) {
        ChatBotMessage message = new ChatBotMessage(userMessage,
                "", MessageType.USER, "", getDate(), SenderType.USER);
        mMessages.add(message);
    }

    void buildTypingMessage() {
        ChatBotMessage message = new ChatBotMessage("",
                "", MessageType.LOADING, "", "", SenderType.CHAT_BOT);
        mMessages.add(message);
    }


}