package com.example.engremonatef.booklisting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eng.Remon Atef on 7/13/2017.
 */

public class ListItem implements Parcelable {

    public String title;
    public String author;

    public ListItem(String author, String title) {
        this.title = author;
        this.author = title;
    }

    protected ListItem(Parcel in) {
        title = in.readString();
        author = in.readString();
    }

    public static final Creator<ListItem> CREATOR = new Creator<ListItem>() {
        @Override
        public ListItem createFromParcel(Parcel in) {
            return new ListItem(in);
        }

        @Override
        public ListItem[] newArray(int size) {
            return new ListItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
    }
}
