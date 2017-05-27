package me.speeddeveloper.beupto.util;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by phili on 7/30/2016.
 */
public class UserUtil {

    public static boolean isStringValidatableForAuth(String validatable){
        if(validatable == null || validatable.length() == 0 || validatable.contains(" ")){
            return false;
        }
        return true;
    }

    public static void setUserToSettingUtils(Context context, String username, String password, String email){

        SettingsUtils.setUsername(context, username);
        SettingsUtils.setPassword(context, password);
        SettingsUtils.setEmail(context, email);

    }

    public static void showValidatableError(Context context){
        new MaterialDialog.Builder(context).content("Please make sure that you didn't type spaces.").positiveText(android.R.string.ok).build().show();
    }


}
