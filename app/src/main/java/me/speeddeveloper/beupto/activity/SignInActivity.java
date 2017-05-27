package me.speeddeveloper.beupto.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.style.BaseActivityNoActionBar;

/**
 * Created by phili on 7/30/2016.
 */
public class SignInActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_signin);
    }
}
