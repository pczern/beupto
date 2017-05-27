package me.speeddeveloper.beupto.activity;

import android.os.Bundle;
import android.view.View;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.style.BaseActivityTranslucentStatus;

/**
 * Created by phili on 8/21/2016.
 */
public class AboutActivity extends BaseActivityTranslucentStatus {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_about);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
