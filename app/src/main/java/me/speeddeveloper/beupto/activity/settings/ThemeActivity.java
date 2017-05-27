package me.speeddeveloper.beupto.activity.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.MainActivity;
import me.speeddeveloper.beupto.activity.settings.adapter.ColorListAdapter;
import me.speeddeveloper.beupto.activity.style.BaseActivityNoActionBar;
import me.speeddeveloper.beupto.activity.style.Color;
import me.speeddeveloper.beupto.util.MyUtil;
import me.speeddeveloper.beupto.util.SettingsUtils;

/**
 * Created by phili on 7/19/2016.
 */
public class ThemeActivity extends BaseActivityNoActionBar {

    public static final String TAG = ThemeActivity.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_theme);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
   /*     GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new ThemeAdapter(this));*/


        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Theme Chooser");
                builder.setIcon(R.drawable.logo_proportional);
                builder.setMessage("We wanna enable you to customize your BeUpTo as you need. This was just an example button to demonstrate the new look you have chosen. Good luck!");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });


        ImageView primaryColorImageView = (ImageView) findViewById(R.id.settings_primary_color_imageview);
        ImageView accentColorImageView = (ImageView) findViewById(R.id.settings_accent_color_imageview);




        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int primaryColor = typedValue.data;
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        int accentColor = typedValue.data;

        primaryColorImageView.setImageDrawable(new ColorDrawable(primaryColor));
        accentColorImageView.setImageDrawable(new ColorDrawable(accentColor));


        final Switch lightThemeSwitch = (Switch) findViewById(R.id.settings_lighttheme_switch);



        View lightThemeView = findViewById(R.id.settings_lighttheme);
        lightThemeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                lightThemeSwitch.toggle();
            }
        });


        if(!(SettingsUtils.isThemeDark(this))){
            lightThemeSwitch.setChecked(true);
        }else{
            lightThemeSwitch.setChecked(false);
        }

        lightThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsUtils.setThemeDark(compoundButton.getContext(), !b);
                MyUtil.restartActivityForThemeUpdate(ThemeActivity.this);
            }
        });

        View primaryColorView = findViewById(R.id.settings_primary_color);
        primaryColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<Color> colors = SettingsUtils.ThemeManager.getPrimaryColorList(view.getContext());

                new MaterialDialog.Builder(view.getContext())
                        .title("Primary Color")
                        .iconRes(R.drawable.ic_colorize_black_24dp)
                        .adapter(new ColorListAdapter(view.getContext(), colors ),
                                new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        Log.d(TAG, "never called");
                                        Toast.makeText(ThemeActivity.this, "Clicked item " + which, Toast.LENGTH_SHORT).show();
                                        SettingsUtils.setPrimaryColor(itemView.getContext(), colors.get(which).getIdentifier());
                                        dialog.dismiss();
                                        MyUtil.restartActivityForThemeUpdate(ThemeActivity.this);
                                    }
                                })
                        .positiveText("use default").negativeText("close")
                        .show();

            }
        });


        View accentColorView = findViewById(R.id.settings_accent_color);

        accentColorView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                final List<Color> colors = SettingsUtils.ThemeManager.getAccentColorList(view.getContext());
                new MaterialDialog.Builder(view.getContext())
                        .title("Primary Color")
                        .iconRes(R.drawable.ic_colorize_black_24dp)
                        .adapter(new ColorListAdapter(view.getContext(), colors),
                                new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        Log.d(TAG, "never called");
                                        SettingsUtils.setAccentColor(dialog.getContext(), colors.get(which).getIdentifier());
                                        Toast.makeText(ThemeActivity.this, "Clicked item " + which, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        MyUtil.restartActivityForThemeUpdate(ThemeActivity.this);
                                    }
                                })
                        .positiveText("use default").negativeText("close")
                        .show();

            }
        });


       /* if(ThemeManager.isWhiteTheme()){
            ((TextView) findViewById(R.id.title_textview)).setTextColor(this.getResources().getColor(R.color.colorPrimaryText));
            ((TextView) findViewById(R.id.subtitle_textview)).setTextColor(this.getResources().getColor(R.color.colorSecondaryText));
        }*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                doFinish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        doFinish();
    }

    private void doFinish() {
        new MaterialDialog.Builder(this)
                .title("Restart")
                .content("The app will be restarted to apply the theme changes correctly")
                .positiveText("OK")
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(dialog.getContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).build().show();

    }


    public void applyThemeChanges(){

        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }
}
