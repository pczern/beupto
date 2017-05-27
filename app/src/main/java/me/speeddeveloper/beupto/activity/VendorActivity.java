package me.speeddeveloper.beupto.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.common.eventbus.Subscribe;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.style.BaseActivityNoActionBar;
import me.speeddeveloper.beupto.activity.style.BaseActivityTranslucentStatus;
import me.speeddeveloper.beupto.adapter.VendorRecyclerAdapter;
import me.speeddeveloper.beupto.model.Entry;
import me.speeddeveloper.beupto.model.Feed;
import me.speeddeveloper.beupto.model.Info;
import me.speeddeveloper.beupto.model.InfoFormat;
import me.speeddeveloper.beupto.model.InfoModel;
import me.speeddeveloper.beupto.model.InfoType;
import me.speeddeveloper.beupto.parser.XmlParser;
import me.speeddeveloper.beupto.util.AnimationUtil;
import me.speeddeveloper.beupto.util.CircleTransform;
import me.speeddeveloper.beupto.util.IFunctional;
import me.speeddeveloper.beupto.util.ItemClickSupport;
import me.speeddeveloper.beupto.util.MyUtil;
import me.speeddeveloper.beupto.util.SubscribableUtil;
import me.speeddeveloper.beupto.util.SubscribeUtil;
import me.speeddeveloper.beupto.view.ImageSlider;
import me.speeddeveloper.beupto.view.NoInterceptNestedScollView;
import retrofit.http.GET;

/**
 * Created by phili on 8/3/2016.
 */
public class VendorActivity extends BaseActivityTranslucentStatus {

    private static final String TAG = VendorActivity.class.getSimpleName();
    private ViewPager imageViewPager;
    private Info vendorInfo;
    private List<Entry> entries;
    private Feed feed;
    private boolean isViewPagerFilled = true;
    private ImageSlider imageSlider;
    private RecyclerView recyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_vendor);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);




        Intent intent = getIntent();
        vendorInfo = intent.getParcelableExtra(getString(R.string.intent_infos));

        final int imageHeightDimen = this.getResources().getDimensionPixelSize(R.dimen.image_slider_height);
        final int statusBarHeightDimen = this.getResources().getDimensionPixelSize(R.dimen.statusbar_height);
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                float alpha = Math.min(1, (float) scrollY / (imageHeightDimen - statusBarHeightDimen));

                    getWindow().setStatusBarColor(ScrollUtils.getColorWithAlpha(alpha, Color.BLACK));

            }
        });

        imageViewPager = (ViewPager) findViewById(R.id.image_viewpager);



        imageViewPager.setOffscreenPageLimit(5);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);


        TextView titleTextView = (TextView) findViewById(R.id.title_textview);
        TextView descriptionTextView = (TextView) findViewById(R.id.description_textview);

        titleTextView.setText(vendorInfo.getTitle());
        descriptionTextView.setText(vendorInfo.getDescription());
        Picasso.with(this).load(vendorInfo.getThumbnailUri()).placeholder(R.drawable.progress_animation).transform(new CircleTransform()).into(((ImageView) findViewById(R.id.favicon_imageview)));




        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // Todo change url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://unsplash.com/rss", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    InputStream is = new ByteArrayInputStream(response.getBytes("utf-8"));
                    Object object = XmlParser.parse(Uri.parse(vendorInfo.getUri()), is);
                    if(object instanceof Feed){

                        feed = (Feed) object;
                        entries = feed.getEntryList();

                        loadInfosInUI();

                    }
                }catch(Exception e){
                    Log.e(TAG, e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        requestQueue.add(stringRequest);


        SubscribableUtil.setSubscribable(new InfoModel(vendorInfo, InfoType.FEED, InfoFormat.UNKNOWN), new IFunctional<Boolean>() {
            @Override
            public void makeIt(Boolean o) {
                Log.d(TAG, "saved subscribable?: " + o);



            }
        });

       /*  // Check if the version of Android is Lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {

            // Set the status bar to dark-semi-transparentish
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Set paddingTop of toolbar to height of status bar.
            // Fixes statusbar covers toolbar issue
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }*/



        ImageView returnImageView = (ImageView) findViewById(R.id.return_imageview);
        returnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        if(SubscribeUtil.hasSubscribed(vendorInfo.getUri())){
            floatingActionButton.setImageResource(R.drawable.ic_check_white_24dp);
        }




        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!SubscribeUtil.hasSubscribed(vendorInfo.getUri())) {

                    AnimationUtil.startSubsribeAnimation((FloatingActionButton) view);
                    SubscribeUtil.subscribe(vendorInfo.getUri(), new IFunctional<Boolean>() {
                        @Override
                        public void makeIt(Boolean o) {
                            Log.d(TAG, "subscribing successful? : " + o);

                        }
                    });
                }else{
                    AnimationUtil.startUnsubsribeAnimation((FloatingActionButton) view);
                    SubscribeUtil.unsubscribe(vendorInfo.getUri(), new IFunctional<Boolean>() {
                        @Override
                        public void makeIt(Boolean o) {
                            Log.d(TAG, "subscribing successful2? : " + o);
                        }
                    });
                }
            }
        });


        TextView textView = (TextView) findViewById(R.id.website_textview);
        textView.setText(vendorInfo.getTitle());

    }



    private void loadInfosInUI() {

        if(entries.size() >= 5) {
            imageSlider = new ImageSlider((List<Info>) (List<?>) entries.subList(0, 5), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VendorActivity.this.onClick(((VendorRecyclerAdapter) recyclerView.getAdapter()).getInfo(imageViewPager.getCurrentItem()));
                }
            }, (LinearLayout) findViewById(R.id.dot_linearlayout));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            recyclerView.setAdapter(new VendorRecyclerAdapter((List<Info>) (List<?>) entries.subList(5, entries.size())));
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
        }else{
            imageSlider = new ImageSlider((List<Info>) (List<?>) entries.subList(0, entries.size()), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VendorActivity.this.onClick(((VendorRecyclerAdapter) recyclerView.getAdapter()).getInfo(imageViewPager.getCurrentItem()));
                }
            }, (LinearLayout) findViewById(R.id.dot_linearlayout));
            // Todo show that there can't be next articles

        }


        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                VendorActivity.this.onClick(((VendorRecyclerAdapter) recyclerView.getAdapter()).getInfo(position));
            }
        });
        imageViewPager.setAdapter(imageSlider);
        imageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imageSlider.jump(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    public void onClick(Info info){
        Intent intent = new Intent(this, ReadActivity.class);
        intent.putExtra(getString(R.string.intent_infos), info);
        intent.putExtra(getString(R.string.intent_info_source), vendorInfo);
        startActivity(intent);
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }







}
