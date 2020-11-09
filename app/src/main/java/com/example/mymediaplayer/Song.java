package com.example.mymediaplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class Song  implements Parcelable {

    private String title;
    private String artist;
    private String path;

    public Song(String songTitle, String songArtist, String songData) {
        title = songTitle;
        artist = songArtist;
        path = songData;
    }

    protected Song(Parcel in) {
        title = in.readString();
        artist = in.readString();
        path = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getTitle(){ return title; }
    public String getArtist(){ return artist; }
    public String getPath(){ return path; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(path);
    }
}
