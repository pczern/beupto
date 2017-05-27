package me.speeddeveloper.beupto;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by phili on 7/19/2016.
 */
public class MainApplication extends Application {


    public static List<String> ISO_CODES = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        MainApplication.ISO_CODES = new ArrayList<>();
        MainApplication.ISO_CODES.add(Locale.getDefault().getCountry());
    }
}