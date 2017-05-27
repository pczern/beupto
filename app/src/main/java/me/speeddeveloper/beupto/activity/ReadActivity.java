package me.speeddeveloper.beupto.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.settings.SettingsActivity;
import me.speeddeveloper.beupto.activity.settings.SettingsReadActivity;
import me.speeddeveloper.beupto.activity.style.BaseActivityNoActionBar;
import me.speeddeveloper.beupto.activity.style.BaseActivityTranslucentStatus;
import me.speeddeveloper.beupto.adapter.VendorRecyclerAdapter;
import me.speeddeveloper.beupto.adapter.ViewRecyclerAdapter;
import me.speeddeveloper.beupto.model.Entry;
import me.speeddeveloper.beupto.model.Info;
import me.speeddeveloper.beupto.util.CircleTransform;
import me.speeddeveloper.beupto.util.IFunctional;
import me.speeddeveloper.beupto.util.MyUtil;
import me.speeddeveloper.beupto.util.SettingsUtils;

/**
 * Created by phili on 8/4/2016.
 */
public class ReadActivity extends BaseActivityTranslucentStatus implements ObservableScrollViewCallbacks, TextToSpeech.OnInitListener {


    private ObservableScrollView observableScrollView;
    private Toolbar toolbar;
    private static final String TAG = ReadActivity.class.getSimpleName();
    private ImageView entryImageView;
    private int colorPrimary;
    private int colorStatusBar;
    private int entryImageViewHeight;
    private int entryImageViewHeightCollapsed;
    private int entryImageViewAlpha;
    private Info articleInfo;
    private Info subscribableInfo;
    private WebView webView;
    private int statusBarHeight;
    private AppBarLayout appBarLayout;
    private int scrollUpHeight;
    private LinearLayout contentLinearLayout;
    private View anchor;
    private boolean showReaderPanelOnScrollUp = false;
    private RelativeLayout overlayRelativeLayout;
    private TextToSpeech textToSpeech;
    private boolean userStoppedListening = true;
    private boolean isTextToSpeechInitialized = false;
    private String html;
    private RelativeLayout alignBarRelativeLayout;
    private int readerPanelHeight;
    private List<Info> entries = new ArrayList<>();
    private boolean isListExpanded = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    LinearLayout readerPanelLayout;
    boolean isFirstStart = true;
    LinearLayout linearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_read);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        observableScrollView = (ObservableScrollView) findViewById(R.id.observablescrollview);
        observableScrollView.setScrollViewCallbacks(this);
        appBarLayout = (AppBarLayout) findViewById(R.id.the_toolbar);
        anchor = findViewById(R.id.anchor);
        entryImageView = (ImageView) findViewById(R.id.imageview);
        colorPrimary = SettingsUtils.ThemeManager.getPrimaryColor(this);
        colorStatusBar = SettingsUtils.ThemeManager.getStatusBarColor(this);
        alignBarRelativeLayout = (RelativeLayout) findViewById(R.id.alignbar_relativelayout);
        entryImageViewHeight = getResources().getDimensionPixelSize(R.dimen.read_entry_image_height);
        entryImageViewHeightCollapsed = getResources().getDimensionPixelSize(R.dimen.read_entry_image_height_collapsed);
        statusBarHeight = getResources().getDimensionPixelSize(R.dimen.statusbar_height);
        scrollUpHeight = getResources().getDimensionPixelSize(R.dimen.read_entry_scroll_up_height);
        entryImageViewAlpha = getResources().getDimensionPixelSize(R.dimen.read_entry_image_alpha);
        readerPanelHeight = getResources().getDimensionPixelSize(R.dimen.readerpanel_height);

        Intent intent = getIntent();
        articleInfo = intent.getParcelableExtra(getString(R.string.intent_infos));
        subscribableInfo = intent.getParcelableExtra(getString(R.string.intent_info_source));

        TextView titleTextView = (TextView) this.findViewById(R.id.title_textview);
        titleTextView.setText(articleInfo.getTitle());


        //AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        TextView subscribableTitleTextView = (TextView) appBarLayout.findViewById(R.id.title_textview);
        subscribableTitleTextView.setText(subscribableInfo.getTitle());

        TextView subscribableDescriptionTextView = (TextView) appBarLayout.findViewById(R.id.description_textview);
        subscribableDescriptionTextView.setText(subscribableInfo.getTitle());

        //  ViewHelper.setTranslationY(entryImageView, -(entryImageViewHeight - entryImageViewHeightCollapsed) / 2);

        // The method returns a MaterialDrawable, but as it is private to the builder you'll have to store it as a regular Drawable ;)
        Drawable webErrorDrawable = MaterialDrawableBuilder.with(this) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.WEB) // provide an icon
                .setColor(Color.WHITE) // set the icon color
                .setToActionbarSize() // set the icon size
                .build(); // Finally call build
        Picasso.with(this).load(subscribableInfo.getThumbnailUri()).transform(new CircleTransform()).placeholder(R.drawable.progress_animation).error(webErrorDrawable).into(((ImageView) appBarLayout.findViewById(R.id.favicon_imageview)));


        Picasso.with(this).load(articleInfo.getThumbnailUri()).placeholder(R.drawable.progress_animation).into(((ImageView) findViewById(R.id.imageview)));
        Log.d(TAG, "height of: " + observableScrollView.getHeight());


        webView = (WebView) findViewById(R.id.webview);


        observableScrollView.setNestedScrollingEnabled(false);







   /*     ViewTreeObserver vto = observableScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                observableScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int height = observableScrollView.getMeasuredWidth();
                Log.d(TAG, "ob height: " + height);
                Log.d(TAG, "scrollupheight: " + scrollUpHeight);

                *//*observableScrollView.scrollVerticallyTo(scrollUpHeight - 50);*//*

            }
        });*/


        ImageView returnImageView = (ImageView) appBarLayout.findViewById(R.id.return_imageview);
        returnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Visit " + articleInfo.getUri() + " on BeUpTo. Yeah men!");
        sendIntent.setType("text/plain");

        ImageView shareImageView = (ImageView) findViewById(R.id.share_imageview);
        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
            }
        });





        WebViewClient webViewClient = new WebViewClient(){
          @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url){
                goToWebsite(Uri.parse(url));
                return super.shouldOverrideUrlLoading(view, url);
            }
            @Override
            public void onLoadResource(WebView  view, String  url){

            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                Log.d(TAG, "webview height; " + webView.getHeight());
                addReaderPanel();



                Log.d(TAG, "observable scroll view height: " + observableScrollView.getHeight());
                Log.d(TAG, "linearlayout height: " + linearLayout.getHeight());

            }
        };
       webView.setWebViewClient(webViewClient);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);

        webView.getSettings().setSaveFormData(false);
        webView.getSettings().setSavePassword(false);
        webView.setHorizontalScrollBarEnabled(false);
        observableScrollView.setSmoothScrollingEnabled(true);
        updateWebView();
        overlayRelativeLayout = (RelativeLayout) findViewById(R.id.overlay_relativelayout);

        LayoutInflater inflater = LayoutInflater.from(this);
        readerPanelLayout = (LinearLayout) inflater.inflate(R.layout.bar_read, null);



        //ViewHelper.setTranslationY(entryImageView, scrollUpHeight / 2);
        //ViewHelper.setTranslationY(contentLinearLayout, scrollUpHeight / 2);
        //ViewHelper.setTranslationY(entryImageView, scrollUpHeight / 2);



    /*    Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(colorStatusBar);*/

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.





    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_tts);

        if(!(userStoppedListening)){
            item.setIcon(R.drawable.ic_stop_black_24dp);
            item.setTitle("Stop Listening");
        }
        else{
            item.setIcon(R.drawable.ic_volume_up_black_24dp);
            item.setTitle("Listen Article");
        }


        MenuItem itemList = menu.findItem(R.id.action_tts);

        if(isListExpanded){
            itemList.setIcon(R.drawable.ic_expand_less_white_24dp);
            itemList.setTitle("Collapse");
        }else{
            itemList.setIcon(R.drawable.ic_expand_more_white_24dp);
            itemList.setTitle("Expand");
        }




        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();




        updateWebView();

    }




    private void addReaderPanel() {

        if(overlayRelativeLayout != null && readerPanelLayout != null)
            overlayRelativeLayout.removeView(readerPanelLayout);
        if(alignBarRelativeLayout != null && readerPanelLayout != null)
            alignBarRelativeLayout.removeView(readerPanelLayout);
        int visibility = SettingsUtils.Reader.getReaderpanelVisibility(this);


        if(SettingsUtils.Reader.getReaderpanelVisibility(this) == SettingsUtils.Reader.Panel.ON_SCROLL_UP){
            showReaderPanelOnScrollUp = true;
        }else{
            showReaderPanelOnScrollUp = false;
        }
        Log.d(TAG, "visibility: " + visibility);
        RelativeLayout.LayoutParams params;
        switch (visibility){
            case SettingsUtils.Reader.Panel.ALWAYS:
                params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


                params.setMargins(0, 0, 0, - readerPanelLayout.getHeight()  - readerPanelLayout.getHeight());
                overlayRelativeLayout.addView(readerPanelLayout, params);
                Log.d(TAG, "overlay height:" + readerPanelLayout.getHeight());
                ViewHelper.setTranslationY(readerPanelLayout, overlayRelativeLayout.getHeight() - readerPanelHeight);

                Log.d(TAG, "YES!");
                break;
            case SettingsUtils.Reader.Panel.ON_SCROLL_UP:
                showReaderPanelOnScrollUp = true;


                /*ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) webView.getLayoutParams();
                marginParams.setMargins(0, 0, 0, MyUtility.dpToPx(this, 46));*/


                params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               /* params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);*/

                overlayRelativeLayout.setVisibility(View.GONE);

                overlayRelativeLayout.addView(readerPanelLayout, params);
                Log.d(TAG, "overlay height: " + overlayRelativeLayout.getHeight());
                ViewHelper.setTranslationY(readerPanelLayout, overlayRelativeLayout.getHeight());

                overlayRelativeLayout.setVisibility(View.VISIBLE);
                break;
            case SettingsUtils.Reader.Panel.BOTTOM:
                params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                alignBarRelativeLayout.addView(readerPanelLayout, params);
                ViewHelper.setTranslationY(readerPanelLayout, 0);
                break;
        }


    }

    public void updateWebView() {
        int fontSize = SettingsUtils.Reader.getFontSize(this);
        webView.getSettings().setDefaultFontSize(fontSize);
        html = getHTML();
        Log.d(TAG, html);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        Log.d(TAG, articleInfo.getUri());
       //webView.loadData(html, "charset=utf-8", "utf-8");
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        webView.clearCache(true);
    }


    public String getHTML() {
        float lineHeight = SettingsUtils.Reader.getArticleLineHeight(this);


        String html = "<!doctype html><html><head><meta http-equiv='Content-Type' content='text/html' charset='UTF-8' />" +
                "<style>@font-face{font-family: 'Noto'; src: url('file:///android_asset/NotoSans-Regular.ttf');} @font-face{font-family: 'Noto Bold'; src: url('file:///android_asset/NotoSans-Bold.ttf');} body h1{ font-size: 2em; text-align: left;  line-height: 1.3; font-weight: 600;} img{width: 100%; height: auto;} a{text-decoration: none;} body{font-size: 1em; line-height: " + lineHeight + "; padding: 0% !important; margin: 0em !important;} body > img{height: auto; width: 100%;} h2{margin-top: 40px; font-size: 1.2em;} p{font-size: 1.05em; }" +
                "" +
                "" +
   /* Reset CSS*/             "html, body, div, span, applet, object, iframe,\n" +
                "h1, h2, h3, h4, h5, h6, p, blockquote, pre,\n" +
                "a, abbr, acronym, address, big, cite, code,\n" +
                "del, dfn, em, img, ins, kbd, q, s, samp,\n" +
                "small, strike, strong, sub, sup, tt, var,\n" +
                "b, u, i, center,\n" +
                "dl, dt, dd, ol, ul, li,\n" +
                "fieldset, form, label, legend,\n" +
                "table, caption, tbody, tfoot, thead, tr, th, td,\n" +
                "article, aside, canvas, details, embed, \n" +
                "figure, figcaption, footer, header, hgroup, \n" +
                "menu, nav, output, ruby, section, summary,\n" +
                "time, mark, audio, video {\n" +
                "\tmargin: 0;\n" +
                "\tpadding: 0;\n" +
                "\tborder: 0;\n" +
                "\tfont-size: 100%;\n" +
                "\tfont: inherit;\n" +
                "\tvertical-align: baseline;\n" +
                "}\n" +
                "/* HTML5 display-role reset for older browsers */\n" +
                "article, aside, details, figcaption, figure, \n" +
                "footer, header, hgroup, menu, nav, section {\n" +
                "\tdisplay: block;\n" +
                "}\n" +
                "body {\n" +
                "\tline-height: 1;\n" +
                "}\n" +
                "ol, ul {\n" +
                "\tlist-style: none;\n" +
                "}\n" +
                "blockquote, q {\n" +
                "\tquotes: none;\n" +
                "}\n" +
                "blockquote:before, blockquote:after,\n" +
                "q:before, q:after {\n" +
                "\tcontent: '';\n" +
                "\tcontent: none;\n" +
                "}\n" +
                "table {\n" +
                "\tborder-collapse: collapse;\n" +
                "\tborder-spacing: 0;\n" +
                "}" +
                "" +
                "" +
                "</style></head><body>";
        html += ((Entry) articleInfo).getContent().trim() + "</body></html>";
        return html;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(isFirstStart) {
            observableScrollView.scrollVerticallyTo(scrollUpHeight);
            isFirstStart = false;
        }
    }

    /* @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {


            if(scrollY <= 0){
               // ViewHelper.setTranslationY(entryImageView, -(entryImageViewHeight - entryImageViewHeightCollapsed) - scrollY / 2);
            }
            *//*ViewHelper.setTranslationY(entryImageView, -(entryImageViewHeight - entryImageViewHeightCollapsed) / 2);
        ViewHelper.setTranslationY(anchor, -(entryImageViewHeight - entryImageViewHeightCollapsed) / 2);*//*




      *//*  if(scrollY <= 0){
            entryImageView.setMinimumHeight(entryImageView.getHeight() + scrollY);
        }*//*


*//*
        if(scrollY > scrollUpHeight) {*//*

  *//*          float alpha = Math.min(1, (float) scrollY / (entryImageViewHeight + scrollUpHeight - statusBarHeight - toolbar.getHeight()));
            toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, colorPrimary));
            getWindow().setStatusBarColor(ScrollUtils.getColorWithAlpha(alpha, colorStatusBar));

            if (entryImageViewHeight - statusBarHeight - toolbar.getHeight() < scrollY  + scrollUpHeight) {
                getSupportActionBar().setElevation(10);
                getWindow().setStatusBarColor(colorStatusBar);
                ViewHelper.setTranslationY(toolbar, entryImageViewHeight - statusBarHeight - toolbar.getHeight() - scrollY);


                *//**//* if(entryImageViewHeight - statusBarHeight <= scrollY){
                Log.d(TAG, "yes");

            }else {
                ViewHelper.setTranslationY(toolbar, entryImageViewHeight - statusBarHeight - toolbar.getHeight() - scrollY);

                Log.d(TAG, "no");
            }*//**//*
                //  Log.d(TAG, "jetzt");


            } else {
                ViewHelper.setTranslationY(toolbar, 0);
            }
        }
        ViewHelper.setTranslationY(entryImageView, scrollY / 2);*//*
    }
*/


/*    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(observableScrollView.getCurrentScrollY(), false, false);
    }*/
    int scrollY;

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

        scrollY = Math.max(0, scrollY - scrollUpHeight);
        this.scrollY = scrollY;

        float alpha = Math.min(1, (float) scrollY / entryImageViewAlpha);
        appBarLayout.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, colorPrimary));
        getWindow().setStatusBarColor(ScrollUtils.getColorWithAlpha(alpha, colorStatusBar));
        ViewHelper.setTranslationY(entryImageView, scrollY / 2);


        if (entryImageViewHeightCollapsed - statusBarHeight - toolbar.getHeight() <= scrollY + 20) {

            appBarLayout.setElevation(8);

        } else {
            appBarLayout.setElevation(0);
        }


        if(scrollY + 20 >= linearLayout.getHeight() - scrollUpHeight - overlayRelativeLayout.getHeight()){
            if (toolbarIsHidden()) {
                Log.d(TAG, "toolbar was hidden2");
                showToolbar();
            }
        }


    }


    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState != null)
            Log.d(TAG, scrollState.name());

        Log.e("DEBUG", "onUpOrCancelMotionEvent: " + scrollState);
        if(showReaderPanelOnScrollUp) {


            if (scrollState == ScrollState.UP) {
                if (toolbarIsShown()) {
                    Log.d(TAG, "toolbar was shown");
                    hideToolbar();
                }
            } else if (scrollState == ScrollState.DOWN) {
                if (toolbarIsHidden()) {
                    Log.d(TAG, "toolbar was hidden");
                    showToolbar();
                }
            }
        }


    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(readerPanelLayout) == overlayRelativeLayout.getHeight() - readerPanelHeight;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(readerPanelLayout) == overlayRelativeLayout.getHeight();
    }

    private void showToolbar() {
        moveToolbar(overlayRelativeLayout.getHeight() - readerPanelHeight);
    }

    private void hideToolbar() {
        moveToolbar(overlayRelativeLayout.getHeight());
    }


    private void moveToolbar(float toTranslationY) {
        if (ViewHelper.getTranslationY(readerPanelLayout) == toTranslationY) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(readerPanelLayout), toTranslationY).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(readerPanelLayout, translationY);
                /*ViewHelper.setTranslationY((View) observableScrollView, translationY);*/
             /*   FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ((View) observableScrollView).getLayoutParams();
                lp.height = (int) -translationY + *//*getScreenHeight()*//* lp.topMargin;*/
                ((View) readerPanelLayout).requestLayout();
            }
        });
        animator.start();
    }

    @Override
    public void onDownMotionEvent() {



    }

       @Override
       public boolean onCreateOptionsMenu(Menu menu) {
           getMenuInflater().inflate(R.menu.activity_read, menu);
           return true;
       }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()){


            case R.id.action_tts:
                if(isTextToSpeechInitialized && textToSpeech != null){
                    doTextSpeech();
                }else if(textToSpeech == null){
                    textToSpeech = new TextToSpeech(this, this);
                }
                return true;
            case R.id.action_expand:
                if(isListExpanded)
                    collapseRecyclerView();
                else
                    expandRecyclerView();
                return true;

            case R.id.action_settings:
                intent = new Intent(this, SettingsReadActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_hear_music:
                intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
                startActivity(intent);

                return true;
            case R.id.action_text_options:
                SettingsUtils.Reader.showFontSizeDialog(this, new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        webView.getSettings().setDefaultFontSize(seekBar.getProgress() + 6);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }, new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        updateWebView();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }, true);
                return true;


        }


        return super.onOptionsItemSelected(item);
       /* return super.onOptionsItemSelected(item);*/
    }

// Todo what is that?????


    public void goToWebsite(Uri uri) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(colorPrimary);
        CustomTabsIntent customTabsIntent = builder.build();
        builder.setStartAnimations(this,  R.anim.slide_in_right,R.anim.slide_in_right2);
        builder.setExitAnimations(this, 0, R.anim.slide_out_left);
        builder.setSecondaryToolbarColor(Color.RED);

        customTabsIntent.launchUrl(this, uri);
    }


    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            isTextToSpeechInitialized = true;
            doTextSpeech();
        }else if(status == TextToSpeech.ERROR){
            // Todo show error warning that TextToSpeech can't be opened
        }
    }


    public void doTextSpeech(){
        if(textToSpeech.isSpeaking()){
            textToSpeech.stop();
            userStoppedListening = true;
        }else{
            Log.d(TAG, "what?");
            textToSpeech.speak(articleInfo.getTitle() + "\n" + Jsoup.parse(html).body().text(), TextToSpeech.QUEUE_FLUSH, null);
            textToSpeech.playSilence(700, TextToSpeech.QUEUE_ADD, null);
            textToSpeech.speak(Jsoup.parse(html).text(), TextToSpeech.QUEUE_ADD, null);
            userStoppedListening = false;
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        isTextToSpeechInitialized = false;
        userStoppedListening = true;


        super.onPause();
    }


    public void applyThemeChanges(){

        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        if(isListExpanded) {
            collapseRecyclerView();
        }else
            super.onBackPressed();
    }

    public void expandRecyclerView(){
        isListExpanded = true;
        invalidateOptionsMenu();
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setItemAnimator(new SlideInLeftAnimator(new OvershootInterpolator(1f)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        hideToolbar();
        float alpha = Math.min(1, (float) scrollY / entryImageViewAlpha);
        ValueAnimator animation = ValueAnimator.ofFloat(alpha, 1f);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float alpha = (float) valueAnimator.getAnimatedValue();
                appBarLayout.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, colorPrimary));
                getWindow().setStatusBarColor(ScrollUtils.getColorWithAlpha(alpha, colorStatusBar));
            }
        });


        ValueAnimator shadowAnimation = ValueAnimator.ofInt(0, 8);
        shadowAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int elevation = (int) valueAnimator.getAnimatedValue();
                appBarLayout.setElevation(elevation);
            }
        });
        shadowAnimation.setDuration(300);
        animation.setDuration(300);
        shadowAnimation.start();
        animation.start();




    /*    ObjectAnimator anim = ObjectAnimator.ofFloat(appBarLayout, "alpha", 0f, 1f);
        anim.setDuration(1000);
        anim.start();
*/
        if(entries.isEmpty()){
            MyUtil.getEntriesFromUri(this, Uri.parse(subscribableInfo.getUri()), new IFunctional<List<Entry>>() {
                @Override
                public void makeIt(List<Entry> entries) {

                    recyclerView.setVisibility(View.VISIBLE);
                    ViewRecyclerAdapter viewRecyclerAdapter = new ViewRecyclerAdapter();
                    recyclerView.setAdapter(viewRecyclerAdapter);
                    ReadActivity.this.entries = (List<Info>) (List<?>) entries;

                   for(Info info : entries){

                       viewRecyclerAdapter.addInfo(info);
                       appBarLayout.setBackgroundColor(colorPrimary);
                   }

                }
            });
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            ViewRecyclerAdapter viewRecyclerAdapter = new ViewRecyclerAdapter();
            recyclerView.setAdapter(viewRecyclerAdapter);
            for (Info info : entries) {
                viewRecyclerAdapter.addInfo(info);
                appBarLayout.setBackgroundColor(colorPrimary);
            }

        }

    }

    public void collapseRecyclerView(){
        isListExpanded = false;
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setVisibility(View.GONE);

    }

}