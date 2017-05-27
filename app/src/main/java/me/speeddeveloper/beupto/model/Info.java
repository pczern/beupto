package me.speeddeveloper.beupto.model;

/**
 * Created by phili on 7/17/2016.
 */

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Date;



/**
 * Created by speedDeveloper on 10.02.2016.
 */
public class Info implements Parcelable {
    public static final String TAG = Info.class.getSimpleName();

    public static final Parcelable.Creator<Info> CREATOR
            = new Parcelable.Creator<Info>() {
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        public Info[] newArray(int size) {
            return new Info[size];
        }
    };
    private String title;
    private String uri;
    private String thumbnailUri;
    private String description;
    private String date;

    public Info() {

    }

    public Info(String title) {
        this.title = title;
    }

    public Info(String title, String uri) {
        this.title = title;
        this.uri = uri;
    }

    public Info(String title, String uri, String thumbnailUri) {
        this.title = title;
        this.uri = uri;
        this.thumbnailUri = thumbnailUri;
    }


    // Getter and Setter

    public Info(String title, String description, String uri, String thumbnailUri) {
        this.title = title;
        this.description = description;
        this.uri = uri;
        this.thumbnailUri = thumbnailUri;
    }

    public Info(Parcel in) {
        Log.d(TAG, "Parcel constructor begin");
        this.setTitle(in.readString());
        this.setDescription(in.readString());
        this.setUri(in.readString());
        this.setThumbnailUri(in.readString());
        Log.d(TAG, "Parcel constructor middle");
    /*    this.setUri((Uri) in.readParcelable(Uri.class.getClassLoader()));
        this.setThumbnailUri((Uri) in.readParcelable(Uri.class.getClassLoader()));*/
        Log.d(TAG, "Parcel constructor end");


       /* this.setThumbnail((Bitmap) in.readParcelable(getClass().getClassLoader()));*/


    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Info) {
            Info info = (Info) obj;
            if(info.getUri() != null && this.getUri() != null){
                Log.d(TAG, "uri1: " + info.getUri());
                Log.d(TAG, "uri2: " + this.getUri());
                boolean e =  info.getUri().equalsIgnoreCase(this.getUri());
                Log.d(TAG, "are equal?: " + e);
                return e;
            }
        }
        return false;
    }



    // Parcelable implemenation

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void writeToParcel(Parcel out, int flags) {
        Log.d(TAG, "writeToParcel begin");
        out.writeString(this.getTitle() != null ? this.getTitle() : "");
        out.writeString(this.getDescription() != null ? this.getDescription() : "");
        out.writeString(this.getUri() != null ? this.getUri() : "");
        out.writeString(this.getThumbnailUri() != null ? this.getThumbnailUri() : "");

      /*  out.writeParcelable(this.getUri(), flags);
        out.writeParcelable(this.getThumbnailUri(), flags);*/
        /*out.writeParcelable(this.getThumbnail(), flags);*/
        Log.d(TAG, "writeToParcel end");
    }

    public int describeContents() {
        return 0;
    }


}
