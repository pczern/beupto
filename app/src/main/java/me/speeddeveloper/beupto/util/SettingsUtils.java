package me.speeddeveloper.beupto.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.style.Color;

/**
 * Created by phili on 7/17/2016.
 */
public class SettingsUtils {


    public static final String PREF_FIRST_START = "pref_first_start";
    public static final String PREF_SHOW_ADD_BUTTON_IN_MAINACTIVITY = "pref_show_add_button_in_mainactivity";
    public static final boolean PREF_SHOW_ADD_BUTTON_IN_MAINACTIVITY_DEFAULT = true;
    public static final String PREF_PRIMARY_COLOR = "pref_primary_color";
    public static final String PREF_ACCENT_COLOR = "pref_accent_color";
    public static final String PREF_THEME_DARK = "pref_theme_dark";
    public static final String PREF_UID = "pref_uid";
    public static final String THEME_DEFAULT_THEME = "LightTheme";
    public static final String THEME_DEFAULT_ACCENT = "PinkA400";
    public static final String THEME_DEFAULT_ACTION_BAR = "ActionBar";
    public static final String THEME_DEFAULT_PRIMARY = "Cyan";
    public static final float[] READER_LINE_HEIGHTS = new float[]{1.5f, 1.7f, 1.9f, 2.1f, 2.3f};
    public static final String PREF_FINISHED_SIGN_UP = "pref_finished_sign_up";
    public static final String PREF_FINISHED_INTRO = "pref_finished_intro";


    public static final String PREF_READERPANEL_VISIBILITY = "pref_readerpanel_visibility";

    public static final String PREF_USERNAME = "pref_username";
    public static final String PREF_PASSWORD = "pref_password";
    public static final String PREF_EMAIL = "pref_email";

    public static final String PREF_READER_FONT_SIZE = "pref_reader_font_size";
    public static final int DEFAULT_READER_FONT_SIZE = 16;
    public static final String PREF_READER_LINE_HEIGHT_INDEX = "pref_reader_line_height_index";
    public static final int DEFAULT_READER_LINE_HEIGHT_INDEX = 2;

    public static class Reader {


        public static float getArticleLineHeight(int index) {
            return READER_LINE_HEIGHTS[index];
        }

        public static float getArticleLineHeight(Context context) {
            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            int lineHeight = sharedPref.getInt(PREF_READER_LINE_HEIGHT_INDEX, DEFAULT_READER_LINE_HEIGHT_INDEX);
            return READER_LINE_HEIGHTS[lineHeight];
        }

        public static void setArticleLineHeightIndex(Context context, int newValue) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putInt(PREF_READER_LINE_HEIGHT_INDEX, newValue).apply();
        }

        public static int getFontSize(Context context) {
            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            int fontSize = sharedPref.getInt(PREF_READER_FONT_SIZE, DEFAULT_READER_FONT_SIZE);
            return fontSize;
        }

        public static void setFontSize(Context context, int newValue) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putInt(PREF_READER_FONT_SIZE, newValue).apply();
        }

        public static MaterialDialog showFontSizeDialog(final Activity context, final SeekBar.OnSeekBarChangeListener fontSizeListener, final SeekBar.OnSeekBarChangeListener lineHeightListener, boolean recreate) {

            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            final int fontSize = sharedPref.getInt(PREF_READER_FONT_SIZE, DEFAULT_READER_FONT_SIZE);
            final int lineHeightIndex = sharedPref.getInt(PREF_READER_LINE_HEIGHT_INDEX, DEFAULT_READER_LINE_HEIGHT_INDEX);


            final MaterialDialog dialog = new MaterialDialog.Builder(context)
                    .title("Text Options")
                    .customView(R.layout.dialog_text_options, true)
                    .positiveText(android.R.string.ok)
                    .negativeText("Use Default")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                            dialog.dismiss();
                        }
                    }).onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            SeekBar fontSizeSeekBar = (SeekBar) dialog.findViewById(R.id.fontsize_seekbar);
                            fontSizeSeekBar.setProgress(fontSizeSeekBar.getMax() / 2);

                            SeekBar lineHeightSeekBar = (SeekBar) dialog.findViewById(R.id.lineheight_seekbar);
                            lineHeightSeekBar.setProgress(DEFAULT_READER_LINE_HEIGHT_INDEX);


                            SharedPreferences.Editor editor = sharedPref.edit();

                            SettingsUtils.Reader.setFontSize(context, fontSizeSeekBar.getProgress() + 6);
                            SettingsUtils.Reader.setArticleLineHeightIndex(context, lineHeightSeekBar.getProgress());
                            dialog.dismiss();
                        }
                    }).cancelable(false)
                    .show();


            SeekBar fontSizeSeekBar = (SeekBar) dialog.findViewById(R.id.fontsize_seekbar);
            SeekBar lineHeightSeekBar = (SeekBar) dialog.findViewById(R.id.lineheight_seekbar);


            fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {


                    SettingsUtils.Reader.setFontSize(context, seekBar.getProgress() + 6);
                    if (fontSizeListener != null)
                        fontSizeListener.onProgressChanged(seekBar, i, b);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (fontSizeListener != null)
                        fontSizeListener.onStartTrackingTouch(seekBar);

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (fontSizeListener != null)
                        fontSizeListener.onStopTrackingTouch(seekBar);

                }
            });


            lineHeightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {


                    SettingsUtils.Reader.setArticleLineHeightIndex(context, seekBar.getProgress());
                    if (lineHeightListener != null)
                        lineHeightListener.onProgressChanged(seekBar, i, b);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (lineHeightListener != null)
                        lineHeightListener.onStartTrackingTouch(seekBar);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (lineHeightListener != null)
                        lineHeightListener.onStopTrackingTouch(seekBar);
                }
            });

            if (recreate)
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        context.recreate();
                    }
                });

            fontSizeSeekBar.setProgress(fontSize - 6);
            lineHeightSeekBar.setProgress(lineHeightIndex);
            return dialog;
        }


        public static class Panel {
            public static final int ALWAYS = 2;
            public static final int ON_SCROLL_UP = 0;
            public static final int BOTTOM = 1;
        }


        public static void setReaderPanelVisibility(final Context context, int newValue) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putInt(PREF_READERPANEL_VISIBILITY, newValue).apply();
        }

        public static int getReaderpanelVisibility(Context context) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getInt(PREF_READERPANEL_VISIBILITY, Reader.Panel.ON_SCROLL_UP);
        }
    }


    public static void setUsername(final Context context, String newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_USERNAME, newValue).apply();
    }

    public static void setPassword(final Context context, String newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_PASSWORD, newValue).apply();
    }

    public static String getUsername(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_USERNAME, "");
    }

    public static String getPassword(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_PASSWORD, "");
    }

    public static void setEmail(final Context context, String newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_EMAIL, newValue).apply();
    }

    public static String getEmail(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_EMAIL, "");
    }

    public static boolean hasFinishedSignUp(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_FINISHED_SIGN_UP, false);
    }

    public static void setFinishedSignUp(final Context context, boolean newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_FINISHED_SIGN_UP, newValue).apply();
    }

    public static boolean hasFinishedIntro(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_FINISHED_INTRO, false);
    }

    public static void setFinishedIntro(final Context context, boolean newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_FINISHED_INTRO, newValue).apply();
    }


    public static void setUid(Context context, String uid) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_UID, uid).apply();
    }

    public static String getUid(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_UID, null);
    }


    public static boolean hasUserAlreadySignedUp(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getString(PREF_UID, "").isEmpty() == false) {
            return true;
        }
        return false;
    }


    public static boolean isThemeDark(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_THEME_DARK, false);
    }

    public static void setThemeDark(final Context context, boolean newValue) {
        ThemeManager.startUpdating();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_THEME_DARK, newValue).apply();
    }

    public static String getAccentColor(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_ACCENT_COLOR, THEME_DEFAULT_ACCENT);
    }

    public static String getPrimaryColor(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_PRIMARY_COLOR, THEME_DEFAULT_PRIMARY);
    }

    public static void setPrimaryColor(final Context context, String newValue) {
        ThemeManager.startUpdating();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_PRIMARY_COLOR, newValue).apply();
    }

    public static void setAccentColor(final Context context, String newValue) {
        ThemeManager.startUpdating();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_ACCENT_COLOR, newValue).apply();
    }

    public static boolean hasAppAlreadyBeenStartedOnce(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_FIRST_START, false);
    }

    public static boolean isFirstStartOfApp(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_FIRST_START, true);
    }

    public static void setIsFirstStartOfApp(final Context context, boolean newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_FIRST_START, newValue).apply();
    }

    public static boolean isShowAddButtonInMainActivity(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_SHOW_ADD_BUTTON_IN_MAINACTIVITY, true);
    }

    public static void setIsShowAddButtonInMainActivity(final Context context, boolean newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_SHOW_ADD_BUTTON_IN_MAINACTIVITY, newValue).apply();
    }


    public static void setupSwitch(Activity activity, int containerViewId, int switchViewId, final String key, boolean defaultValue) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean isChecked = sharedPreferences.getBoolean(key, defaultValue);
        View containerView = activity.findViewById(containerViewId);
        final Switch switchView = (Switch) activity.findViewById(switchViewId);

        switchView.setChecked(isChecked);

        containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchView.toggle();
                sharedPreferences.edit().putBoolean(key, switchView.isChecked()).apply();
            }
        });
        switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putBoolean(key, switchView.isChecked()).apply();
            }
        });
    }


    public static class ThemeManager {

        public static final String LIGHT_THEME = "LightTheme";
        public static final String DARK_THEME = "DarkTheme";
        public static final String ACTION_BAR = "ActionBar";
        public static final String NO_ACTION_BAR = "NoActionBar";
        public static final String TRANSPARENT = "Transparent";

        private static boolean isUpdated = false;


        public static boolean isUpdating() {
            return isUpdated;
        }

        public static void finishUpdating() {
            isUpdated = false;
        }

        public static void startUpdating() {
            isUpdated = true;
        }


        public static List<Color> getPrimaryColorList(Context context) {
            List<Color> colorList = new ArrayList<>();
            Resources resources = context.getResources();
            colorList.add(new Color("Red", "Red", resources.getColor(R.color.Red500)));
            colorList.add(new Color("Pink", "Pink", resources.getColor(R.color.Pink500)));
            colorList.add(new Color("Purple", "Purple", resources.getColor(R.color.Purple500)));
            colorList.add(new Color("Deep Purple", "DeepPurple", resources.getColor(R.color.DeepPurple500)));
            colorList.add(new Color("Indigo", "Indigo", resources.getColor(R.color.Indigo500)));
            colorList.add(new Color("Blue", "Blue", resources.getColor(R.color.Blue500)));
            colorList.add(new Color("LightBlue", "LightBlue", resources.getColor(R.color.LightBlue500)));
            colorList.add(new Color("Cyan", "Cyan", resources.getColor(R.color.Cyan500)));
            colorList.add(new Color("Teal", "Teal", resources.getColor(R.color.Teal500)));
            colorList.add(new Color("Green", "Green", resources.getColor(R.color.Green500)));
            colorList.add(new Color("Light Green", "LightGreen", resources.getColor(R.color.LightGreen500)));
            colorList.add(new Color("Lime", "Lime", resources.getColor(R.color.Lime500)));
            colorList.add(new Color("Yellow", "Yellow", resources.getColor(R.color.Yellow500)));
            colorList.add(new Color("Amber", "Amber", resources.getColor(R.color.Amber500)));
            colorList.add(new Color("Orange", "Orange", resources.getColor(R.color.Orange500)));
            colorList.add(new Color("Deep Orange", "DeepOrange", resources.getColor(R.color.DeepOrange500)));
            colorList.add(new Color("Brown", "Brown", resources.getColor(R.color.Brown500)));
            colorList.add(new Color("Grey", "Grey", resources.getColor(R.color.Grey500)));
            colorList.add(new Color("BlueGrey", "BlueGrey", resources.getColor(R.color.BlueGrey500)));

            return colorList;
        }


        public static List<Color> getStatusBarColorList(Context context) {
            List<Color> colorList = new ArrayList<>();
            Resources resources = context.getResources();
            colorList.add(new Color("Red", "Red", resources.getColor(R.color.Red700)));
            colorList.add(new Color("Pink", "Pink", resources.getColor(R.color.Pink700)));
            colorList.add(new Color("Purple", "Purple", resources.getColor(R.color.Purple700)));
            colorList.add(new Color("Deep Purple", "DeepPurple", resources.getColor(R.color.DeepPurple700)));
            colorList.add(new Color("Indigo", "Indigo", resources.getColor(R.color.Indigo700)));
            colorList.add(new Color("Blue", "Blue", resources.getColor(R.color.Blue700)));
            colorList.add(new Color("LightBlue", "LightBlue", resources.getColor(R.color.LightBlue700)));
            colorList.add(new Color("Cyan", "Cyan", resources.getColor(R.color.Cyan700)));
            colorList.add(new Color("Teal", "Teal", resources.getColor(R.color.Teal700)));
            colorList.add(new Color("Green", "Green", resources.getColor(R.color.Green700)));
            colorList.add(new Color("Light Green", "LightGreen", resources.getColor(R.color.LightGreen700)));
            colorList.add(new Color("Lime", "Lime", resources.getColor(R.color.Lime700)));
            colorList.add(new Color("Yellow", "Yellow", resources.getColor(R.color.Yellow700)));
            colorList.add(new Color("Amber", "Amber", resources.getColor(R.color.Amber700)));
            colorList.add(new Color("Orange", "Orange", resources.getColor(R.color.Orange700)));
            colorList.add(new Color("Deep Orange", "DeepOrange", resources.getColor(R.color.DeepOrange700)));
            colorList.add(new Color("Brown", "Brown", resources.getColor(R.color.Brown700)));
            colorList.add(new Color("Grey", "Grey", resources.getColor(R.color.Grey700)));
            colorList.add(new Color("BlueGrey", "BlueGrey", resources.getColor(R.color.BlueGrey700)));

            return colorList;
        }


        public static List<Color> getAccentColorList(Context context) {
            List<Color> colorList = new ArrayList<>();
            Resources resources = context.getResources();

            colorList.add(new Color("Red", "RedA400", resources.getColor(R.color.RedA400)));
            colorList.add(new Color("Pink", "PinkA400", resources.getColor(R.color.PinkA400)));
            colorList.add(new Color("Purple", "PurpleA400", resources.getColor(R.color.PurpleA400)));
            colorList.add(new Color("Deep Purple", "DeepPurpleA400", resources.getColor(R.color.DeepPurpleA400)));
            colorList.add(new Color("Indigo", "IndigoA400", resources.getColor(R.color.IndigoA400)));
            colorList.add(new Color("Blue", "BlueA400", resources.getColor(R.color.BlueA400)));
            colorList.add(new Color("LightBlue", "LightBlueA400", resources.getColor(R.color.LightBlueA400)));
            colorList.add(new Color("Cyan", "CyanA700", resources.getColor(R.color.CyanA700)));
            colorList.add(new Color("Teal", "TealA700", resources.getColor(R.color.TealA700)));
            colorList.add(new Color("Green", "GreenA700", resources.getColor(R.color.GreenA700)));
            colorList.add(new Color("Light Green", "LightGreenA700", resources.getColor(R.color.LightGreenA700)));
            colorList.add(new Color("Lime", "LimeA700", resources.getColor(R.color.LimeA700)));
            colorList.add(new Color("Yellow", "YellowA700", resources.getColor(R.color.YellowA700)));
            colorList.add(new Color("Amber", "AmberA400", resources.getColor(R.color.AmberA400)));
            colorList.add(new Color("Orange", "OrangeA400", resources.getColor(R.color.OrangeA400)));
            colorList.add(new Color("Deep Orange", "DeepOrangeA400", resources.getColor(R.color.DeepOrangeA400)));


            return colorList;
        }


        public static int getCurrentThemeResourceId(Context context, boolean showActionBar, boolean isTransparent) {


            String primaryColor = SettingsUtils.getPrimaryColor(context);
            String accentColor = SettingsUtils.getAccentColor(context);
            boolean isThemeDark = SettingsUtils.isThemeDark(context);


            String identifier = "";
            if (isThemeDark)
                identifier = DARK_THEME;
            else
                identifier = LIGHT_THEME;

            if (showActionBar)
                identifier += "." + ACTION_BAR;
            else {
                identifier += "." + NO_ACTION_BAR;
                if (isTransparent)
                    identifier += "." + TRANSPARENT;
            }
            identifier += "." + primaryColor + "." + accentColor;


            return context.getResources().getIdentifier(identifier, "style", context.getPackageName());
        }


        public static int getAccentColor(Context context) {
            TypedValue typedValue = new TypedValue();
            TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
            int color = a.getColor(0, 0);
            a.recycle();
            return color;
        }

        public static int getPrimaryColor(Context context) {
            TypedValue typedValue = new TypedValue();
            TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
            int color = a.getColor(0, 0);
            a.recycle();
            return color;
        }


        public static int getPrimary600(Context context) {
            Resources resources = context.getResources();
            String primaryColor = SettingsUtils.getPrimaryColor(context);
            return resources.getColor(resources.getIdentifier(primaryColor + "600", "color", context.getPackageName()));
        }


        public static int getStatusBarColor(Context context) {
            Resources resources = context.getResources();
            String primaryColor = SettingsUtils.getPrimaryColor(context);
            return resources.getColor(resources.getIdentifier(primaryColor + "700", "color", context.getPackageName()));
        }
    }

}
