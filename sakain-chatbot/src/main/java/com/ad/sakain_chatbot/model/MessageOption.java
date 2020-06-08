package com.ad.sakain_chatbot.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageOption implements Parcelable {

    @SerializedName("title")
    @Expose
    private String title;

    public MessageOption(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
    }

    public MessageOption() {
    }

    protected MessageOption(Parcel in) {
        this.title = in.readString();
    }

    public static final Creator<MessageOption> CREATOR = new Creator<MessageOption>() {
        @Override
        public MessageOption createFromParcel(Parcel source) {
            return new MessageOption(source);
        }

        @Override
        public MessageOption[] newArray(int size) {
            return new MessageOption[size];
        }
    };
}
