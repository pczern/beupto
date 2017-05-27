package me.speeddeveloper.beupto.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by speedDeveloper on 15.05.2016.
 */
public class Media implements Parcelable{
    public static enum Format{
        IMAGE, AUDIO, VIDEO
    }


    Format format;
    Uri uri;
    public Media(Uri uri, Format format){
        this.uri = uri;
        this.format = format;
    }

    public Format getFormat(){
        return format;
    }
    public Uri getUri(){
        return uri;
    }

    private Media(Parcel in) {
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.format = (Format) in.readSerializable();
    }

    // Parcelable stuff


    public static final Creator<Media> CREATOR
            = new Creator<Media>() {
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(uri, flags);
        out.writeSerializable(format);

    }

    @Override
    public int describeContents() {
        return 0;
    }




}
