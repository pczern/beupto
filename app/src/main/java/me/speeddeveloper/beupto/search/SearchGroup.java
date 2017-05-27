package me.speeddeveloper.beupto.search;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.speeddeveloper.beupto.model.Info;

/**
 * Created by speedDeveloper on 17.06.2016.
 */
public abstract class SearchGroup<T extends Info>{

    public static final String TAG = SearchGroup.class.getSimpleName();
    Context context;
    ArrayList<T> infoList;
    boolean isFetching = true;
    Handler handler;

    public SearchGroup(Context context){
        this.context = context;
        infoList = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());
    }
    public Context getContext(){
        return context;
    }
    public abstract void cancel();
    public void addInfo(final T info){
        infoList.add(info);
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFetched(info);
            }
        });
    }
    public void addInfoList(final List<T> infoList){
        this.infoList.addAll(infoList);
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFetchedList(infoList);
            }
        });
    }
    public void clear(){
        infoList.clear();
    }
    public void cancelAndClear(){
        clear();
        cancel();
    }
    public abstract void start(SearchParameters parameters);
    public abstract void onFetched(T info);
    public abstract void onFetchedList(List<T> infoList);
    public void finish(){
        isFetching = false;
    };
    public boolean isFetching(){
        return isFetching;
    }
    public void setFetching(boolean isFetching){
        this.isFetching = isFetching;
    }
    public ArrayList<T> getList(){
        return infoList;
    }


}
