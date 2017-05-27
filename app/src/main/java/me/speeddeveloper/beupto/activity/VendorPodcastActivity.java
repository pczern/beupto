package me.speeddeveloper.beupto.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.GrayscaleTransformation;
import jp.wasabeef.picasso.transformations.gpu.ContrastFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.InvertFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.SwirlFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.VignetteFilterTransformation;
import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.style.BaseActivityTranslucentStatus;
import me.speeddeveloper.beupto.adapter.VendorPodcastRecyclerAdapter;
import me.speeddeveloper.beupto.adapter.VendorRecyclerAdapter;
import me.speeddeveloper.beupto.model.Feed;
import me.speeddeveloper.beupto.model.Info;
import me.speeddeveloper.beupto.model.InfoFormat;
import me.speeddeveloper.beupto.model.InfoModel;
import me.speeddeveloper.beupto.model.InfoType;
import me.speeddeveloper.beupto.model.Podcast;
import me.speeddeveloper.beupto.util.AnimationUtil;
import me.speeddeveloper.beupto.util.CircleTransform;
import me.speeddeveloper.beupto.util.IFunctional;
import me.speeddeveloper.beupto.util.MyUtil;
import me.speeddeveloper.beupto.util.SubscribableUtil;
import me.speeddeveloper.beupto.util.SubscribeUtil;

/**
 * Created by phili on 8/14/2016.
 */
public class VendorPodcastActivity extends BaseActivityTranslucentStatus {


    private Podcast vendorInfo;
    private RecyclerView recyclerView;
    private static final String TAG = VendorPodcastActivity.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_vendor_podcast);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);


        Intent intent = getIntent();
        vendorInfo = (Podcast) intent.getParcelableExtra(getString(R.string.intent_infos));







        TextView titleTextView = (TextView) findViewById(R.id.headline_textview);
        TextView artistTextView = (TextView) findViewById(R.id.artist_textview);

        titleTextView.setText(vendorInfo.getTitle());
        artistTextView.setText(vendorInfo.getArtist());
        Picasso.with(this).load(vendorInfo.getThumbnailUri()).placeholder(R.drawable.progress_animation).into(((ImageView) findViewById(R.id.imageview)));
        Picasso.with(this).load(vendorInfo.getThumbnailUri()).transform(new BlurTransformation(this)).transform(new ContrastFilterTransformation(this)).into(((ImageView) findViewById(R.id.background_imageview)));




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
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        MyUtil.getFeedFromUri(this, Uri.parse(vendorInfo.getUri()), new IFunctional<Feed>(){

            @Override
            public void makeIt(Feed feed) {
                recyclerView.setAdapter(new VendorPodcastRecyclerAdapter((List<Info>) (List<?>) feed.getEntryList()));
            }

        });



        SubscribableUtil.setSubscribable(new InfoModel(vendorInfo, InfoType.PODCAST, InfoFormat.UNKNOWN), new IFunctional<Boolean>() {
            @Override
            public void makeIt(Boolean o) {
                Log.d(TAG, "saved subscribable?: " + o);
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






    }
}
