package me.speeddeveloper.beupto.search;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import me.speeddeveloper.beupto.model.Website;
import me.speeddeveloper.beupto.util.MyUtil;


/**
 * Created by speedDeveloper on 23.03.2016.
 */
public abstract class FeedSearchTask extends SearchGroup<Website> {

    public static final String TAG = FeedSearchTask.class.getSimpleName();
    RequestQueue requestQueue;


    public FeedSearchTask(Context context) {
        super(context);
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }


    public void addRequest(final String uri) {
       /* final StringRequest requestNoRedirect = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                InputStream stream = null;
                try {
                    stream = new ByteArrayInputStream(Charset.forName("UTF-16").encode(response).array());
                    Document document = Jsoup.parse(stream, "UTF-8", uri);
                    Website website = new Website(document);
                    if (!(website.getFeedUris().size() > 0)) {
                        onFetched();
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    if (stream != null)
                        try {
                            stream.close();
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                }
            }
        }, null);*/


        MyRequest requestRedirect = new MyRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                InputStream stream = null;
                try {
                    Log.d(TAG, "t");
                    Document document = Jsoup.parse(response, uri.toString());
                    Website website = new Website(document);
                    // Todo add fetched WEBSITE to Firebase
                    if (website.getFeedUris().size() > 0) {
                        Log.d(TAG, "Added website " + website.getUri() + " Title " + website.getTitle());
                        addInfo(website);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    if (stream != null)
                        try {
                            stream.close();
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null)
                    Log.d(TAG, error.getMessage());
            }
        });
        requestRedirect.setTag(TAG);
        try {
            Log.d(TAG, requestRedirect.getHeaders().get("User-Agent"));
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        requestRedirect.setRetryPolicy(new DefaultRetryPolicy(3000, 0, 1));
        requestQueue.add(requestRedirect);

    }


    private ArrayList<String> getISOCodeArrayList(SearchParameters parameters) {
        ArrayList<String> codes = new ArrayList<String>();
        codes.add("com");
        codes.addAll(parameters.getIsoCodes());
        codes.add("net");
        codes.add("uk");
        codes.add("org");
        codes.add("info");
        codes.add("eu");
        return codes;
    }


    // Call everytime .toLowerCase because otherwise it doesn't work
    public String getUriString(String search, String code) throws MalformedURLException {
        search = search.trim().replace(" ", "-");
        if (code != null)
            return MyUtil.createUri(search + "." + code).toString().toLowerCase();
        return MyUtil.createUri(search).toString().toLowerCase();

    }


    @Override
    public void start(SearchParameters parameters) {

        ArrayList<String> codes = getISOCodeArrayList(parameters);

        try {
            addRequest(getUriString(parameters.getSearch(), null));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        for (String code : codes) {
            try {
                addRequest(getUriString(parameters.getSearch(), code));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cancel() {
        requestQueue.cancelAll(TAG);
    }


}


