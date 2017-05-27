package me.speeddeveloper.beupto.model;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import me.speeddeveloper.beupto.firebase.FireDB;

/**
 * Created by phili on 8/17/2016.
 */
public class InfoModel {


    String infoType;
    String infoFormat;
    String title;
    String description;
    String thumbnailUri;
    String uri;


    public InfoModel(){

    }

    public InfoModel(Info info, InfoType type, InfoFormat format){
        this.title = info.getTitle();
        this.description = info.getDescription();
        this.thumbnailUri = info.getThumbnailUri();
        this.uri = Uri.parse(info.getUri()).toString();
        this.infoType = type.toString();
        this.infoFormat = format.toString();
    }

    public String getInfoFormat() {
        return infoFormat;
    }

    public void setInfoFormat(String infoFormat) {
        this.infoFormat = infoFormat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = Uri.parse(uri).toString();
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }


    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put(FireDB.NODE_TITLE, title);
        map.put(FireDB.NODE_DESCRIPTION, description);
        map.put(FireDB.NODE_URI, uri);
        map.put(FireDB.NODE_THUMBNAIL_URI, thumbnailUri);
        map.put(FireDB.NODE_TYPE, infoType.toString());
        map.put(FireDB.NODE_FORMAT, infoFormat.toString());
        return map;

    }

    public Info asInfo(){
        return new Info(title, description, uri, thumbnailUri);
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof InfoModel){
            InfoModel infoModel = (InfoModel) obj;
            return infoModel.getUri().equalsIgnoreCase(this.getUri());
        }
        return false;
    }


    @Override
    public int hashCode() {
        return this.getUri().hashCode();
    }
}
