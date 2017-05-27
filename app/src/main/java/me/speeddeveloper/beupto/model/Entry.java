package me.speeddeveloper.beupto.model;

import android.net.Uri;
import android.os.Parcel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by speedDeveloper on 21.02.2016.
 */
public class Entry extends Info {



    public static class Utility{
        public static boolean isPodcast(Entry e){
            boolean isPodcast = false;
            for(Media media : e.getAttachedMedia()){
                if(Media.Format.AUDIO == media.getFormat()){
                    isPodcast = true;
                    break;
                }
            }
            return isPodcast;
        }


        public static boolean isVideo(Entry e){
            boolean isVideo = false;
            for(Media media : e.getAttachedMedia()){
                if(Media.Format.VIDEO == media.getFormat()){
                    isVideo = true;
                    break;
                }
            }
            return isVideo;
        }

        public static boolean isArticle(Entry e){
            if(e.getAttachedMedia().size() == 0)
                return true;
            if(Utility.isPodcast(e) && Utility.isVideo(e) && e.getContent() != null && e.getContent().length() > 0){
                return true;
            }
            return false;
        }



        public static Uri getThumbnailUriFromContent(String content){
            Document document = Jsoup.parse(content);
            Elements elements = document.getElementsByTag("img");
            if(elements.isEmpty() == false){
                String href = elements.get(0).attr("href");
                return Uri.parse(href);
            }
            return null;

        }


    }




    public static final String TAG = Entry.class.getSimpleName();


    String description;
    String content;
    List<Media> attachedMedia = new ArrayList<Media>();










    // Constructor

    public Entry() {
        super("");
    }

    public Entry(String title) {
        super(title);

    }

    @Override
    public String getThumbnailUri(){
        if(super.getThumbnailUri() != null)
            return super.getThumbnailUri();


        if (getContent() != null) {
            Uri m;
            if((m = Utility.getThumbnailUriFromContent(getContent())) != null){
                return m.toString();
            }

        }
        return null;

    }



/*    public void setAudioUrl(URL url){
        this.audioUrl = url;
    }

    public void setAudioUrl(String urlString){
        try {
            this.setAudioUrl(MyUtility.createURL(urlString));
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public URL getAudioUrl(){
        return audioUrl;
    }


    public boolean isAudioEntry(int contentLimit){
        if(audioUrl != null && (this.getContent() != null && this.getContent().trim().length() >= contentLimit)){
            return true;
        }
        return false;
    }*/


    // Getter and Setter

/*    public URL getImageUrl() {
        return imageUrl;
    }*/

/*    public void setImageUrl(String imageUrl) {
        try {
            this.setImageUrl(MyUtility.createURL(imageUrl));
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }
    }*/

  /*  public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }*/

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        if(description != null && description.trim().isEmpty() == false){
            return Jsoup.parse(description).text();
        }else{
            return description;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }








    public void addMedia(Media media){
        attachedMedia.add(media);
    }





    // Parcelable stuff


    public static final Creator<Entry> CREATOR
            = new Creator<Entry>() {
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(description != null ? description : "");
        out.writeString(content != null ? content : "");
        out.writeList(attachedMedia != null ? attachedMedia : new ArrayList());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public List<Media> getAttachedMedia() {
        return attachedMedia;
    }

    public void setAttachedMedia(List<Media> attachedMedia) {
        this.attachedMedia = attachedMedia;
    }

    private Entry(Parcel in) {
        super(in);

        this.setDescription(in.readString());
        this.setContent(in.readString());

        List<Media> media = new ArrayList<>();
        in.readList(media, Entry.class.getClassLoader());
        this.setAttachedMedia(media);





    }









      /*  @Override
    public Bitmap getThumbnail() {

        Elements e = Jsoup.parse(content).getElementsByTag("img");
        Bitmap bitmap = null;
        for(Element element : e){
            String src = element.attr("src");
            if(src != null && src.trim().isEmpty() == false){
                URL url = null;
                try {
                    url = MyUtility.createURL(src);
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
                InputStream stream = null;
                try {
                    stream = url.openStream();
                    bitmap =  BitmapFactory.decodeStream(stream);
                    if(bitmap != null){
                        break;
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }finally {
                    try {
                        stream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        }
        return bitmap;
    }*/




}
