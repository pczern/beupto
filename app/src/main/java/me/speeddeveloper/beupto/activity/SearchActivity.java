package me.speeddeveloper.beupto.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.speeddeveloper.beupto.MainApplication;
import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.style.BaseActivity;
import me.speeddeveloper.beupto.activity.style.BaseActivityNoActionBar;
import me.speeddeveloper.beupto.introduction.IntroductionScreen;
import me.speeddeveloper.beupto.model.Info;
import me.speeddeveloper.beupto.model.InfoFormat;
import me.speeddeveloper.beupto.model.InfoModel;
import me.speeddeveloper.beupto.model.InfoType;
import me.speeddeveloper.beupto.model.Podcast;
import me.speeddeveloper.beupto.model.Website;
import me.speeddeveloper.beupto.model.YoutubeChannel;
import me.speeddeveloper.beupto.search.FeedSearchTask;
import me.speeddeveloper.beupto.search.FeedlySearchTask;
import me.speeddeveloper.beupto.search.PodcastSearchTask;
import me.speeddeveloper.beupto.search.SearchGroup;
import me.speeddeveloper.beupto.search.SearchParameters;
import me.speeddeveloper.beupto.search.SearchRecyclerAdapter;
import me.speeddeveloper.beupto.search.YoutubeSearchTask;
import me.speeddeveloper.beupto.util.IFunctional;
import me.speeddeveloper.beupto.util.ItemClickSupport;
import me.speeddeveloper.beupto.util.SubscribableUtil;


public class SearchActivity extends BaseActivityNoActionBar {

    public static final String TAG = SearchActivity.class.getSimpleName();
    private View.OnClickListener micOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // Start the activity, the intent will be populated with the speech text
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        }
    };
    private View.OnClickListener clearOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            imm.showSoftInput(searchEditText, 0);
            searchEditText.setText("");
        }
    };


    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int RC_SIGN_IN = 5;

    private TabLayout tabLayout;
    private EditText searchEditText;
    private ImageView micImageView;
    private ViewFlipper viewFlipper;
    private ImageView dismissImageView;
    private InputMethodManager imm;
    private ViewPager viewPager;
    private String searchEditTextString = "";


    SearchGroup[][] searchGroups;


    private FirebaseAuth auth;

    PagerAdapter mSectionsPagerAdapter;

    public interface Position {
        final static int FEEDS = 0;
        final static int PODCASTS = 1;
        final static int VIDEOS = 2;
        final static int USERS = 3;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        micImageView = (ImageView) findViewById(R.id.mic_imageview);
        searchEditText = (EditText) findViewById(R.id.search_edittext);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        micImageView.setOnClickListener(micOnClickListener);


        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setTabTextColors(Color.parseColor("#8AFFFFFF"), Color.WHITE);

        searchGroups = new SearchGroup[][]
                {
                        {

                                new FeedSearchTask(this) {
                                    @Override
                                    public void onFetched(Website info) {
                                        Log.d(TAG, "info got");
                                        addInfoToRecyclerViewAtPositionInViewPager(Position.FEEDS, info);



                                    }

                                    @Override
                                    public void onFetchedList(List<Website> infoList) {
                                        addInfoListToRecyclerViewAtPositionInViewPager(Position.FEEDS, (List<Info>) (List<?>) infoList);
                                    }


                                },
                                new FeedlySearchTask(this) {
                                    @Override
                                    public void onFetched(Website info) {
                                        addInfoToRecyclerViewAtPositionInViewPager(Position.FEEDS, info);
                                    }

                                    @Override
                                    public void onFetchedList(List<Website> infoList) {
                                        addInfoListToRecyclerViewAtPositionInViewPager(Position.FEEDS, (List<Info>) (List<?>) infoList);
                                    }


                                }
                        },
                        {
                                new PodcastSearchTask(this) {
                                    @Override
                                    public void onFetched(Podcast info) {
                                        Log.d(TAG, "pod");
                                        addInfoToRecyclerViewAtPositionInViewPager(Position.PODCASTS, info);

                                    }

                                    @Override
                                    public void onFetchedList(List<Podcast> infoList) {
                                        Log.d(TAG, "pod");
                                        addInfoListToRecyclerViewAtPositionInViewPager(Position.PODCASTS, (List<Info>) (List<?>) infoList);
                                    }

                                },

                        },
                        {
                                new YoutubeSearchTask(this) {
                                    @Override
                                    public void onFetched(YoutubeChannel info) {
                                        Log.d(TAG, "pod");
                                        addInfoToRecyclerViewAtPositionInViewPager(Position.VIDEOS, info);

                                    }

                                    @Override
                                    public void onFetchedList(List<YoutubeChannel> infoList) {
                                        Log.d(TAG, "pod");
                                        addInfoListToRecyclerViewAtPositionInViewPager(Position.VIDEOS, (List<Info>) (List<?>) infoList);
                                    }

                                },

                        }
                };


        mSectionsPagerAdapter = new Adapter();
        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(4);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {







                /*handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startSearchTasksProperlyToSelectedTab();
                        ViewFlipper viewFlipper = (ViewFlipper) viewPager.findViewWithTag("viewflipper" + position);
                        viewFlipper.setDisplayedChild(1);
                    }
                }, 100);*/

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        Drawable newspaperDrawable = MaterialDrawableBuilder.with(this) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.NEWSPAPER) // provide an icon
                .setColor(Color.WHITE) // set the icon color
                .setToActionbarSize() // set the icon size
                .build(); // Finally call build

        Drawable youtubeDrawable = MaterialDrawableBuilder.with(this) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.YOUTUBE_PLAY) // provide an icon
                .setColor(Color.WHITE) // set the icon color
                .setToActionbarSize() // set the icon size
                .build(); // Finally call build


        tabLayout.setupWithViewPager(viewPager);
        final int normalColor = Color.parseColor("#8AFFFFFF");
        // assign drawables to tablayout
        tabLayout.getTabAt(Position.USERS).setIcon(R.drawable.ic_people_white_24dp).getIcon().setColorFilter(normalColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(Position.FEEDS).setIcon(newspaperDrawable).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(Position.VIDEOS).setIcon(youtubeDrawable).getIcon().setColorFilter(normalColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(Position.PODCASTS).setIcon(R.drawable.ic_music_note_white_24dp).getIcon().setColorFilter(normalColor, PorterDuff.Mode.SRC_IN);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Drawable d = tab.getIcon();
                if (d != null)
                    d.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Drawable d = tab.getIcon();
                if (d != null)
                    d.setColorFilter(normalColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                searchEditTextString = searchEditText.getText().toString();

                /*refreshRecyclerViewAtPositionInViewPager(viewPager.getCurrentItem());*/
                refreshAllRecyclerViews();
                startAllSearchTasks();

                if (searchEditText.length() > 0) {
                    tabLayout.setVisibility(View.VISIBLE);
                    viewFlipper.setDisplayedChild(1);
                } else {
                    tabLayout.setVisibility(View.GONE);
                    viewFlipper.setDisplayedChild(0);
                }

          /*      restartAllSearchTasks();
                startAllSearchTasks();*/

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        auth = FirebaseAuth.getInstance();
    }


    RecyclerView[] recyclerViews = new RecyclerView[4];
    ViewFlipper[] viewFlippers = new ViewFlipper[4];

    public void addInfoToRecyclerViewAtPositionInViewPager(int position, Info info) {


        if(recyclerViews[position] != null){
            SearchRecyclerAdapter searchRecyclerAdapter = (SearchRecyclerAdapter) recyclerViews[position].getAdapter();
            searchRecyclerAdapter.addInfo(info);
        }
        if(viewFlippers[position] != null){
            ViewFlipper viewFlipper = viewFlippers[position];
            viewFlipper.setDisplayedChild(1);
        }
    }

    public void addInfoListToRecyclerViewAtPositionInViewPager(int position, List<Info> infoList) {

        RecyclerView recyclerView = (RecyclerView) viewPager.findViewWithTag("recyclerview" + position);
        if (recyclerView != null) {
            SearchRecyclerAdapter searchRecyclerAdapter = (SearchRecyclerAdapter) recyclerView.getAdapter();
            searchRecyclerAdapter.addInfos(infoList);
            ViewFlipper viewFlipper = (ViewFlipper) viewPager.findViewWithTag("viewflipper" + position);
            viewFlipper.setDisplayedChild(1);
        }
    }

    public void refreshRecyclerViewAtPositionInViewPager(int position) {
        if(recyclerViews[position] != null){
            SearchRecyclerAdapter searchRecyclerAdapter = (SearchRecyclerAdapter) recyclerViews[position].getAdapter();
            searchRecyclerAdapter.clearInfos();
            if(viewFlippers[position].getDisplayedChild() != 0)
                viewFlippers[position].setDisplayedChild(0);
        }
    }

    public void refreshAllRecyclerViews(){
        int tabCount = tabLayout.getTabCount();
        for(int i = 0; i < tabCount; i++) {
            refreshRecyclerViewAtPositionInViewPager(i);
        }
    }

    public void restartAllSearchTasks() {
        for (int i = 0; i < searchGroups.length; i++)
            for (int j = 0; j < searchGroups[i].length; j++) {
                searchGroups[i][j].cancelAndClear();
            }

    }

    AsyncTask lastStartedAsyncTask;

    public void startSearchTasksProperlyToSelectedTab(final int position) {


        lastStartedAsyncTask = new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                lastStartedAsyncTask.cancel(true);
                final SearchParameters parameters = new SearchParameters(searchEditTextString, MainApplication.ISO_CODES);
                for (SearchGroup searchGroup : searchGroups[position]) {
                    if (searchGroup.getList().size() == 0) {
                        searchGroup.cancelAndClear();
                        searchGroup.start(parameters);
                    }
                }
                return null;
            }
        }.execute();

    }

    public void startAllSearchTasks() {
        Log.d(TAG, "viewpager current " + tabLayout.getSelectedTabPosition());
        final SearchParameters parameters = new SearchParameters(searchEditText.getText().toString(), MainApplication.ISO_CODES);
        if(lastStartedAsyncTask != null)    lastStartedAsyncTask.cancel(true);
        lastStartedAsyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Log.d(TAG, "started");
                for (int i = 0; i < searchGroups.length; i++)
                    for (int j = 0; j < searchGroups[i].length; j++) {
                        searchGroups[i][j].cancelAndClear();
                        Log.d(TAG, "search size : " + searchGroups[i][j].getList().size());
                        searchGroups[i][j].start(parameters);
                    }
                return null;
            }
        }.execute();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Result Code: " + resultCode);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            searchEditText.setText(spokenText);
            micImageView.setImageBitmap(BitmapFactory.decodeResource(searchEditText.getResources(), R.drawable.ic_bubble_chart_white_24dp));
            micImageView.setOnClickListener(clearOnClickListener);
        }
    }


    public class Adapter extends PagerAdapter {


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            LayoutInflater layoutInflater = LayoutInflater.from(container.getContext());
            ViewFlipper viewFlipper = (ViewFlipper) layoutInflater.inflate(R.layout.search_view, container, false);
            viewFlipper.setTag("viewflipper" + position);
            RecyclerView recyclerView = (RecyclerView) viewFlipper.findViewById(R.id.recyclerview);
            recyclerView.setTag("recyclerview" + position);
            recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
            recyclerView.setAdapter(new SearchRecyclerAdapter());
            //viewFlipper.addView();
            recyclerViews[position] = recyclerView;
            viewFlippers[position] = viewFlipper;

            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int itemPosition, View v) {


                    Intent intent;
                    Info info = ((SearchRecyclerAdapter) recyclerView.getAdapter()).getInfo(itemPosition);
                    switch(position){
                        case Position.FEEDS:
                            intent = new Intent(recyclerView.getContext(), VendorActivity.class);
                            intent.putExtra(getString(R.string.intent_infos), info);
                            startActivity(intent);

                            break;

                        case Position.PODCASTS:
                            intent = new Intent(recyclerView.getContext(), VendorPodcastActivity.class);
                            intent.putExtra(getString(R.string.intent_infos), info);
                            startActivity(intent);
                            break;
                        case Position.VIDEOS:
                            intent = new Intent(recyclerView.getContext(), VendorYoutubeActivity.class);
                            intent.putExtra(getString(R.string.intent_infos), info);
                            startActivity(intent);
                            break;

                    }


/*
                    ArrayList<Info> infoList = new ArrayList<>();
                    for(SearchGroup searchGroup : searchGroups[position]){

                        ArrayList<Info> infos = searchGroup.getList();
                        Log.d(TAG, "size2: " + infos.size());
                        infoList.addAll(infos);

                    }
                    Log.d(TAG, "size1: " + infoList.size());*/

                }
            });

            container.addView(viewFlipper);
            return viewFlipper;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }


}
