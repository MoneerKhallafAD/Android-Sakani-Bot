package com.ad.sakain_chatbot.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatBotMessage {
    private String message;
    private String messageImageSource;
    private String messageType;
    private String messageOptionType;
    private String messageDate;
    private String senderType;
    private ArrayList<MessageOption> options;
    private String selectedOption;
    private List<PagerItem> itemsPager;
    private String messageTitle;
    private String audioMassage;
    private boolean isAudioMessageRequired = false;
    private Boolean isLoadingVoice = false;
    private File audioFile;

    public ChatBotMessage(String message, String messageImageSource, String messageType, String messageOptionType, String messageDate, String senderType) {
        this.message = message;
        this.messageImageSource = messageImageSource;
        this.messageType = messageType;
        this.messageOptionType = messageOptionType;
        this.messageDate = messageDate;
        this.senderType = senderType;
    }

    public ChatBotMessage(String message, String messageImageSource, String messageType, String messageOptionType, String messageDate, String senderType, ArrayList<MessageOption> options, String selectedOption, String messageTitle) {
        this.message = message;
        this.messageImageSource = messageImageSource;
        this.messageType = messageType;
        this.messageOptionType = messageOptionType;
        this.messageDate = messageDate;
        this.senderType = senderType;
        this.options = options;
        this.selectedOption = selectedOption;
        this.messageTitle = messageTitle;
    }

    public ChatBotMessage(String message, String messageImageSource, String messageType, String messageOptionType, String messageDate, String senderType, String options, String selectedOption, List<PagerItem> itemsPager, String messageTitle) {
        this.message = message;
        this.messageImageSource = messageImageSource;
        this.messageType = messageType;
        this.messageOptionType = messageOptionType;
        this.messageDate = messageDate;
        this.senderType = senderType;
        this.selectedOption = selectedOption;
        this.itemsPager = itemsPager;
        this.messageTitle = messageTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageImageSource() {
        return messageImageSource;
    }

    public void setMessageImageSource(String messageImageSource) {
        this.messageImageSource = messageImageSource;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageOptionType() {
        return messageOptionType;
    }

    public void setMessageOptionType(String messageOptionType) {
        this.messageOptionType = messageOptionType;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public ArrayList<MessageOption> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<MessageOption> options) {
        this.options = options;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public List<PagerItem> getItemsPager() {
        return itemsPager;
    }

    public void setItemsPager(List<PagerItem> itemsPager) {
        this.itemsPager = itemsPager;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getAudioMassage() {
        return audioMassage;
    }

    public void setAudioMassage(String audioMassage) {
        this.audioMassage = audioMassage;
    }

    public boolean getAudioMessageRequired() {
        return isAudioMessageRequired;
    }

    public void setAudioMessageRequired(boolean audioMessageRequired) {
        isAudioMessageRequired = audioMessageRequired;
    }

    public Boolean getLoadingVoice() {
        return isLoadingVoice;
    }

    public void setLoadingVoice(Boolean loadingVoice) {
        isLoadingVoice = loadingVoice;
    }

    public boolean isAudioMessageRequired() {
        return isAudioMessageRequired;
    }

    public File getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }
}
