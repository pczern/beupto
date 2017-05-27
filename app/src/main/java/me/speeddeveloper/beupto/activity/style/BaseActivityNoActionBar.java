package me.speeddeveloper.beupto.activity.style;

import android.os.Bundle;

import me.speeddeveloper.beupto.util.SettingsUtils;

/**
 * Created by phili on 7/17/2016.
 */
public class BaseActivityNoActionBar extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.setTheme(SettingsUtils.ThemeManager.getCurrentThemeResourceId(this, false));
        this.getTheme().applyStyle(SettingsUtils.ThemeManager.getCurrentThemeResourceId(this, false, false), true);
        super.onCreate(savedInstanceState);
    }


}
