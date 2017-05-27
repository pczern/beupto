package me.speeddeveloper.beupto.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by phili on 8/1/2016.
 */
public class YoutubeChannel extends Info {
    public static final Parcelable.Creator<Info> CREATOR
            = new Parcelable.Creator<Info>() {
        public YoutubeChannel createFromParcel(Parcel in) {
            return new YoutubeChannel(in);
        }

        public YoutubeChannel[] newArray(int size) {
            return new YoutubeChannel[size];
        }
    };

    String channelId;

    public YoutubeChannel(String title, String description, String thumbnailUri, String channelId) {
        super(title, description, null, thumbnailUri);
        this.channelId = channelId;
    }


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public YoutubeChannel(Parcel in) {
        super(in);
        this.setChannelId(in.readString());
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(this.getChannelId() != null ? this.getChannelId() : "");
    }

}
