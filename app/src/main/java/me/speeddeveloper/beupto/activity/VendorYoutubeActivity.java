package me.speeddeveloper.beupto.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.style.BaseActivityNoActionBar;
import me.speeddeveloper.beupto.activity.style.BaseActivityTranslucentStatus;
import me.speeddeveloper.beupto.adapter.VendorRecyclerAdapter;
import me.speeddeveloper.beupto.adapter.VendorYoutubeRecyclerAdapter;
import me.speeddeveloper.beupto.model.Info;
import me.speeddeveloper.beupto.model.InfoFormat;
import me.speeddeveloper.beupto.model.InfoModel;
import me.speeddeveloper.beupto.model.InfoType;
import me.speeddeveloper.beupto.model.Podcast;
import me.speeddeveloper.beupto.model.YoutubeChannel;
import me.speeddeveloper.beupto.util.AnimationUtil;
import me.speeddeveloper.beupto.util.CircleTransform;
import me.speeddeveloper.beupto.util.IFunctional;
import me.speeddeveloper.beupto.util.ItemClickSupport;
import me.speeddeveloper.beupto.util.SubscribableUtil;
import me.speeddeveloper.beupto.util.SubscribeUtil;
import me.speeddeveloper.beupto.view.ImageSlider;

/**
 * Created by phili on 8/14/2016.
 */
public class VendorYoutubeActivity extends BaseActivityTranslucentStatus {

    private YoutubeChannel youtubeChannel;
    private RecyclerView recyclerView;
    private static final String TAG = VendorYoutubeActivity.class.getSimpleName();
    private ImageSlider imageSlider;
    private ViewPager imageViewPager;
    private String thumbnailUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_vendor_youtube);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Intent intent = getIntent();

        youtubeChannel = (YoutubeChannel) intent.getParcelableExtra(getString(R.string.intent_infos));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        imageViewPager = (ViewPager) findViewById(R.id.image_viewpager);
        new AsyncTask<YoutubeChannel, Void, List<Info>>() {

            @Override
            protected List<Info> doInBackground(YoutubeChannel... youtubeChannels) {

                YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("BeUpTo").build();
                try {
                    Log.d(TAG, "channelId: " + youtubeChannel.getChannelId());
                    String apiKey = "AIzaSyAKK8TkSWZZLSss8uyB4cr6NQHPztj2L_k";

                    com.google.api.services.youtube.YouTube.Channels.List channels = youtube.channels().list("contentDetails, snippet");
                    channels.setId(youtubeChannel.getChannelId());
                    channels.setKey(apiKey);

                    ChannelListResponse channelListResponse = channels.execute();


                    Log.d(TAG, "response size: " + channelListResponse.getItems().size());

                    List<Info> videos = new ArrayList<>();
                    List<Channel> searchResultList = channelListResponse.getItems();
                    if (searchResultList != null && searchResultList.isEmpty() == false) {
                        Iterator<Channel> iteratorSearchResults = searchResultList.iterator();
                        Log.d(TAG, "test1");
                        if (iteratorSearchResults.hasNext()) {
                            Channel channel = iteratorSearchResults.next();
                            String uploadPlaylistId = channel.getContentDetails().getRelatedPlaylists().getUploads();
                            thumbnailUri = channel.getSnippet().getThumbnails().getDefault().getUrl();
                            Log.d(TAG, "test2");
                            PlaylistItemListResponse response = youtube.playlistItems().list("snippet").setMaxResults(20l).setPlaylistId(uploadPlaylistId).setKey(apiKey).execute();
                            List<PlaylistItem> playlists = response.getItems();
                            Log.d(TAG, "playlist: " + uploadPlaylistId);

                            Log.d(TAG, "playsize: " + playlists.size());
                            for (PlaylistItem item : playlists) {
                                Log.d(TAG, "test3");
                                PlaylistItemSnippet snippet = item.getSnippet();
                                Info info = new Info(snippet.getTitle(), snippet.getDescription(), "http://www.youtube.com/channel/" + snippet.getChannelId(), snippet.getThumbnails().getHigh().getUrl());
                                videos.add(info);
                            }
                        }
                    }
                    return videos;
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

                return null;

            }

            @Override
            protected void onPostExecute(List<Info> infoList) {
                if (infoList != null) {
                    Log.d(TAG, "was los");
                    Log.d(TAG, infoList.get(0).getTitle());
                    TextView titleTextView = (TextView) findViewById(R.id.title_textview);
                    TextView descriptionTextView = (TextView) findViewById(R.id.description_textview);

                    titleTextView.setText(youtubeChannel.getTitle());
                    descriptionTextView.setText(youtubeChannel.getDescription());
                    Picasso.with(VendorYoutubeActivity.this).load(thumbnailUri).placeholder(R.drawable.progress_animation).transform(new CircleTransform()).into(((ImageView) findViewById(R.id.favicon_imageview)));
                    if(infoList.size() > 5) {
                        imageSlider = new ImageSlider((List<Info>) (List<?>) infoList.subList(0, 5), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                VendorYoutubeActivity.this.onClick(((VendorYoutubeRecyclerAdapter) recyclerView.getAdapter()).getInfo(imageViewPager.getCurrentItem()));
                            }
                        }, (LinearLayout) findViewById(R.id.dot_linearlayout));
                        recyclerView.setAdapter(new VendorYoutubeRecyclerAdapter((List<Info>) (List<?>) infoList.subList(5, infoList.size())));
                    }
                    else{
                        imageSlider = new ImageSlider((List<Info>) (List<?>) infoList, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                VendorYoutubeActivity.this.onClick(((VendorYoutubeRecyclerAdapter) recyclerView.getAdapter()).getInfo(imageViewPager.getCurrentItem()));
                            }
                        }, (LinearLayout) findViewById(R.id.dot_linearlayout));
                    }

                    imageViewPager.setOffscreenPageLimit(5);
                    imageViewPager.setAdapter(imageSlider);
                    ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                            VendorYoutubeActivity.this.onClick(((VendorYoutubeRecyclerAdapter) recyclerView.getAdapter()).getInfo(position));
                        }
                    });

                }
            }
        }.execute(youtubeChannel);



        SubscribableUtil.setSubscribable(new InfoModel(youtubeChannel, InfoType.YOUTUBE, InfoFormat.YOUTUBE), new IFunctional<Boolean>() {
            @Override
            public void makeIt(Boolean o) {
                Log.d(TAG, "saved subscribable?: " + o);
            }
        });



        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        if(SubscribeUtil.hasSubscribed(youtubeChannel.getUri())){
            floatingActionButton.setImageResource(R.drawable.ic_check_white_24dp);
        }




        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!SubscribeUtil.hasSubscribed(youtubeChannel.getUri())) {

                    AnimationUtil.startSubsribeAnimation((FloatingActionButton) view);
                    SubscribeUtil.subscribe(youtubeChannel.getUri(), new IFunctional<Boolean>() {
                        @Override
                        public void makeIt(Boolean o) {
                            Log.d(TAG, "subscribing successful? : " + o);

                        }
                    });
                }else{
                    AnimationUtil.startUnsubsribeAnimation((FloatingActionButton) view);
                    SubscribeUtil.unsubscribe(youtubeChannel.getUri(), new IFunctional<Boolean>() {
                        @Override
                        public void makeIt(Boolean o) {
                            Log.d(TAG, "subscribing successful2? : " + o);
                        }
                    });
                }
            }
        });

    }


    public void onClick(Info info){



    }





}
