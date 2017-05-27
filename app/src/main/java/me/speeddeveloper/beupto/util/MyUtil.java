package me.speeddeveloper.beupto.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.firebase.FireDB;
import me.speeddeveloper.beupto.model.Entry;
import me.speeddeveloper.beupto.model.Feed;
import me.speeddeveloper.beupto.model.Info;
import me.speeddeveloper.beupto.parser.XmlParser;

/**
 * Created by phili on 7/22/2016.
 */
public class MyUtil {

    public static void restartActivityForThemeUpdate(Activity activity) {
        Intent intent = new Intent(activity, activity.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finish();
        activity.startActivity(intent);
    }


    private static String addProtocolIfNotAdded(String url){
        if((url.startsWith("http://") != true) && (url.startsWith("https://") != true)){
            url = "http://" + url;
        }
        return url;
    }

    public static Uri createUri(String url) throws MalformedURLException {
        URL myurl = null;
        if (url.startsWith("//")) {
            url = "http:" + url;
        }
        url = addProtocolIfNotAdded(url);
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.scheme("http");
        return builder.build();
    }


    public static void getFeedFromUri(final Context context, final Uri uri, final IFunctional<Feed> e){



        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.GET, uri.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    InputStream is = new ByteArrayInputStream(response.getBytes("utf-8"));
                    Object object = XmlParser.parse(uri, is);
                    if(object instanceof Feed){

                        Feed feed = (Feed) object;
                        e.makeIt(feed);
                    }else{
                        e.makeIt(null);
                    }
                }catch(Exception e){
                    Log.e(context.getPackageName(), e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));


    }





    public static void getEntriesFromUri(final Context context, final Uri uri, final IFunctional<List<Entry>> e){

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(new StringRequest(Request.Method.GET, uri.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    InputStream is = new ByteArrayInputStream(response.getBytes("utf-8"));
                    Object object = XmlParser.parse(uri, is);
                    if(object instanceof Feed){

                        Feed feed = (Feed) object;
                        List<Entry> entries = feed.getEntryList();
                        e.makeIt(entries);
                    }
                }catch(Exception e){
                    Log.e(context.getPackageName(), e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));

    }








    public static void subscribeInfo(Info info, final FloatingActionButton floatingActionButton, final IFunctional e){
        Animation rotateForward = AnimationUtils.loadAnimation(floatingActionButton.getContext(), R.anim.rotate_forward);
        rotateForward.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                floatingActionButton.setImageResource(R.drawable.ic_check_white_24dp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        floatingActionButton.startAnimation(rotateForward);
        if(info.getUri() != null && info.getUri().trim().isEmpty() == false){
            FirebaseDatabase.getInstance().getReference().child("subscribables").push().setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        e.makeIt(true);
                    }else{
                        e.makeIt(false);
                    }
                }
            });
        }else{
            e.makeIt(false);
        }
    }


    public static String encodeUri(Uri uri) {
        return Uri.encode(uri.toString()).replace(".", "%2E");
    }

    public static String encode(String string) {
        return Uri.encode(string).replace(".", "%2E");
    }

    public static String decode(String string) {
        return Uri.decode(string.replace("%2E", "."));
    }
}
