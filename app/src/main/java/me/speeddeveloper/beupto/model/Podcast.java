package me.speeddeveloper.beupto.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by phili on 8/1/2016.
 */
public class Podcast extends Info {
    public static final Parcelable.Creator<Info> CREATOR
            = new Parcelable.Creator<Info>() {
        public Podcast createFromParcel(Parcel in) {
            return new Podcast(in);
        }

        public Podcast[] newArray(int size) {
            return new Podcast[size];
        }
    };


    String artist;

    public Podcast(String title, String description, String artist, String uri, String thumbnailUri) {
        super(title, description, uri, thumbnailUri);
        this.artist = artist;
    }


    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Podcast(Parcel in) {
        super(in);
        this.setArtist(in.readString());
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(this.getArtist() != null ? this.getArtist() : "");
    }


}
