package me.speeddeveloper.beupto.activity.style;

import android.os.Bundle;

import me.speeddeveloper.beupto.util.MyUtil;
import me.speeddeveloper.beupto.util.SettingsUtils;

/**
 * Created by phili on 7/17/2016.
 */
public abstract class BaseActivityActionBar extends BaseActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {

        this.getTheme().applyStyle(SettingsUtils.ThemeManager.getCurrentThemeResourceId(this, true, false), true);
        super.onCreate(savedInstanceState);
    }


}
