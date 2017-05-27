package me.speeddeveloper.beupto.search;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;

import me.speeddeveloper.beupto.model.Info;
import me.speeddeveloper.beupto.model.Website;
import me.speeddeveloper.beupto.util.MyUtil;



/**
 * Created by speedDeveloper on 23.03.2016.
 */

public abstract class FeedlySearchTask extends SearchGroup<Website> {

    private static final String TAG = FeedlySearchTask.class.getSimpleName();
    RequestQueue requestQueue;

    public FeedlySearchTask(Context context) {
        super(context);
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }


    @Override
    public void start(SearchParameters parameters) {
        JsonObjectRequest requestRedirect = new JsonObjectRequest(Request.Method.GET, "http://cloud.feedly.com/v3/search/feeds?query=" + parameters.getSearch(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    List<Website> infoList = new ArrayList<>();
                    JSONArray results = response.getJSONArray("results");
                    for(int i = 0; i < results.length(); i++){
                        JSONObject jsonObject = (JSONObject) results.get(i);
                        String title =  jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        String url = jsonObject.getString("feedId").substring(5);
                        String iconUrl = jsonObject.getString("visualUrl");
                        Website info = new Website(title, description, url, iconUrl);
                        infoList.add(info);
                    }
                    addInfoList(infoList);


                }catch(JSONException exception){
                    Log.e(TAG, exception.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestRedirect.setTag(TAG);
        requestQueue.add(requestRedirect);
    }

    @Override
    public void cancel() {
        requestQueue.cancelAll(TAG);
    }


}



