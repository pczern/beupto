package me.speeddeveloper.beupto.activity.settings;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.style.BaseActivityActionBar;
import me.speeddeveloper.beupto.util.SettingsUtils;


/**
 * Created by speedDeveloper on 17.03.2016.
 */
public class SettingsReadActivity extends BaseActivityActionBar {


    public static final String TAG = SettingsReadActivity.class.getSimpleName();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*MyUtility.debug(TAG, "theme", MainApplication.getCustomThemeId(false));*/
   /*     setTheme(MainApplication.getCustomThemeId(false));*/
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings_read);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        View textoptionsView = findViewById(R.id.settings_textoptions);
        textoptionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsUtils.Reader.showFontSizeDialog(SettingsReadActivity.this, null, null, false);
            }
        });


        View showreaderpanelalwaysView = findViewById(R.id.settings_showreaderpanelalways);
        showreaderpanelalwaysView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(SettingsReadActivity.this)
                        .title("Show Reader Panel")
                        .items(R.array.show_readerpanel_array)
                        .itemsCallbackSingleChoice(SettingsUtils.Reader.getReaderpanelVisibility(SettingsReadActivity.this), new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                SettingsUtils.Reader.setReaderPanelVisibility(SettingsReadActivity.this, which);

                                return true;
                            }
                        })
                        .positiveText(android.R.string.ok)
                        .show();
            }
        });
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
