package me.speeddeveloper.beupto.activity.style;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.util.MenuTintUtils;
import me.speeddeveloper.beupto.util.MyUtil;
import me.speeddeveloper.beupto.util.SettingsUtils;

/**
 * Created by phili on 7/17/2016.
 */
public class BaseActivity extends AppCompatActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuTintUtils.tintAllIcons(menu, this.getResources().getColor(R.color.pure_white));
        return super.onCreateOptionsMenu(menu);
    }

    public void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }



}
