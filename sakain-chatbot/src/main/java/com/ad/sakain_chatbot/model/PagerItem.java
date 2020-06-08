package com.ad.sakain_chatbot.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PagerItem implements Parcelable {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("image")
    @Expose
    private String image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.image);
    }

    public PagerItem() {
    }

    protected PagerItem(Parcel in) {
        this.title = in.readString();
        this.image = in.readString();
    }

    public static final Creator<PagerItem> CREATOR = new Creator<PagerItem>() {
        @Override
        public PagerItem createFromParcel(Parcel source) {
            return new PagerItem(source);
        }

        @Override
        public PagerItem[] newArray(int size) {
            return new PagerItem[size];
        }
    };
}
