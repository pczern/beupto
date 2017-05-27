package me.speeddeveloper.beupto.search;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.speeddeveloper.beupto.model.Info;
import me.speeddeveloper.beupto.model.Podcast;


/**
 * Created by speedDeveloper on 24.03.2016.
 */
public abstract class PodcastSearchTask extends SearchGroup<Podcast>{


    public static final String TAG = PodcastSearchTask.class.getSimpleName();
    RequestQueue requestQueue;

    public PodcastSearchTask(Context context) {
        super(context);
        requestQueue = Volley.newRequestQueue(context);
    }


    @Override
    public void start(SearchParameters parameters) {
        String search = Uri.encode(parameters.getSearch());



        JsonObjectRequest request = new JsonObjectRequest("https://itunes.apple.com/search?term=" + search + "&country=" + parameters.getIsoCodes().get(0).toUpperCase() + "&media=podcast&entity=podcast", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response: " + response.toString());

                List<Podcast> podcastList = new ArrayList<>();
                try {
                    JSONArray array = response.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        String collectionCensoredName = (String) object.get("collectionCensoredName");
                        String feedUrl = (String) object.get("feedUrl");
                        String thumbnailUri = (String) object.get("artworkUrl100");
                        String artistName = (String) object.get("artistName");
                        podcastList.add(new Podcast(collectionCensoredName, "Artist - " + artistName, artistName, feedUrl, thumbnailUri));
                    }

                } catch (JSONException exception) {
                    Log.e(TAG, exception.getMessage());
                }

                if(podcastList != null)
                    addInfoList(podcastList);


               /* createUI(podcastList, MainApplication.STANDARD_LAYOUT, InfoSort.ALPHABETIC, false);*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage() != null)
                    Log.e(TAG, error.getMessage());
                else
                    Log.e(TAG, TAG + "error");
            }
        });
        request.setTag(TAG);
        requestQueue.add(request);
    }



    @Override
    public void cancel() {
        requestQueue.cancelAll(TAG);
    }

}
