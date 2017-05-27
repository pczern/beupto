package me.speeddeveloper.beupto.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.style.BaseActivityActionBar;
import me.speeddeveloper.beupto.util.SettingsUtils;

/**
 * Created by phili on 7/19/2016.
 */
public class SettingsActivity extends BaseActivityActionBar {


    public static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setupItemLink(R.id.settings_look, ThemeActivity.class);
        setupItemLink(R.id.settings_readview, SettingsReadActivity.class);
        SettingsUtils.setupSwitch(this, R.id.settings_fab, R.id.settings_fab_switch, SettingsUtils.PREF_SHOW_ADD_BUTTON_IN_MAINACTIVITY, SettingsUtils.PREF_SHOW_ADD_BUTTON_IN_MAINACTIVITY_DEFAULT);



    }

    private void setupItemLink(int id, final Class actvitiyClass){
        View view = findViewById(id);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), actvitiyClass);
                startActivity(intent);
            }
        });

    }












    @Override
    protected void onRestart() {
        super.onRestart();

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

