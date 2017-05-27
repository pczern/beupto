package me.speeddeveloper.beupto.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by speedDeveloper on 21.02.2016.
 */
public class Feed extends Info {

    public static final String TAG = Feed.class.getSimpleName();
    public static final Creator<Feed> CREATOR
            = new Creator<Feed>() {
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };
    String description;
    List<Entry> entryList;



    public static class Utility{
        public static boolean isArticle(Feed f){
            for(Entry e : f.getEntryList()){
                if(Entry.Utility.isArticle(e)) return true;
            }
            return false;
        }

        public static boolean isPodcast(Feed f){
            for(Entry e : f.getEntryList()){
                if(Entry.Utility.isPodcast(e) == false) return false;
            }
            return true;

        }

        public static boolean isVideo(Feed f){
            for(Entry e : f.getEntryList()){
                if(Entry.Utility.isVideo(e) == false) return false;
            }
            return true;

        }
    }





    // Constructor
    Type type;


    public Feed() {
        super("");
    }





    public Feed(String title, String description, List<Entry> entryList) {
        super(title);

        this.setDescription(description);
        this.setEntryList(entryList);

    }


    // Type enum

    public Feed(String title, String description, List<Entry> entryList, Type type) {
        super(title);

        this.setDescription(description);
        this.setEntryList(entryList);
        this.setType(type);

    }


    // Getter and Setter


    // Supply length of iterating entries
  /*  public boolean isPodcast(int minContentLength, int length){
        for(int i = 0; i < length; i++){
            Entry entry = this.getEntryList().get(i);
            boolean e = entry.isAudioEntry(minContentLength) ? true : false;
            if(e == false)
                return false;
        }
        return true;
    }*/

    public Feed(Parcel in) {
        super(in);

        this.setDescription(in.readString());

        List<Entry> entries = new ArrayList<Entry>();
        in.readList(entries, Entry.class.getClassLoader());
        this.setEntryList(entries);
        String dateString = in.readString();
        if (dateString.isEmpty() != true)
            this.setDate(dateString);


    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getLastModifiedDate() {
        return this.getDate();
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        setDate(lastModifiedDate);
    }

    public List<Entry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<Entry> entryList) {
        this.entryList = entryList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }




    // Equality methods

   /* public void loadEntryListThumbnails() {
        firstFor: for (Entry entry : this.getEntryList()) {


            for (Media media : entry.getAttachedMedia()) {
                if (media.getFormat() == Media.Format.IMAGE) {
                    try {
                        InputStream inputStream = media.getUrl().openStream();
                        entry.setThumbnail(BitmapFactory.decodeStream(inputStream));
                        inputStream.close();
                        continue firstFor; // continue with next entry
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
            }


            Bitmap bitmap = null;
            if (entry.getContent() != null && entry.getContent().trim().isEmpty() == false) {
                Elements e = Jsoup.parse(entry.getContent()).getElementsByTag("img");

                for (Element element : e) {
                    String src = element.attr("src");

                    if (src != null && src.trim().isEmpty() == false) {

                        URL url = null;
                        InputStream stream = null;
                        try {
                            url = MyUtility.createURL(src);


                            stream = url.openStream();
                            bitmap = BitmapFactory.decodeStream(stream);
                            if (bitmap != null) {
                                entry.setThumbnail(bitmap);
                                break;
                            }
                        } catch (MalformedURLException e1) {

                            Log.e(TAG, e1.toString());
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            Log.e(TAG, "exception", e1);
                            e1.printStackTrace();
                        } finally {
                            try {
                                if (stream != null)
                                    stream.close();
                            } catch (IOException e1) {
                                Log.e(TAG, "exception", e1);
                                e1.printStackTrace();
                            }
                        }

                    }
                }

            }

        }

    }*/

    @Override
    public boolean equals(Object o) {
        if (o instanceof Feed) {
            Feed f = (Feed) o;
            if (f.getUri().toString().equalsIgnoreCase(this.getUri().toString())) {
                return true;

            }
        }
        return false;
    }


    // Parcelable implementation

    @Override
    public int hashCode() {
        return this.getUri().toString().toLowerCase().hashCode();
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(description != null ? description : "");
        out.writeList(entryList != null ? entryList : new ArrayList());
        out.writeString(this.getDate() != null ? this.getDate().toString() : "");
    }

    public int describeContents() {
        return 0;
    }

    public enum Type {
        ATOM, RSS2
    }


}
