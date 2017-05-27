package me.speeddeveloper.beupto.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.introduction.IntroductionPagerAdapter;
import me.speeddeveloper.beupto.introduction.IntroductionScreen;
import me.speeddeveloper.beupto.util.SettingsUtils;
import me.speeddeveloper.beupto.util.SocialSyncUtil;

/**
 * Created by phili on 7/30/2016.
 */
public class IntroActivity extends AppCompatActivity {


    private IntroductionPagerAdapter introductionPagerAdapter;
    private ViewPager viewPager;
    private ImageView[] dots;
    private View dotContainer;
    private TwitterAuthClient client;
    private GoogleApiClient mGoogleApiClient;
    private final static int RC_GOOGLE_SIGN_IN = 500;
    private FirebaseAuth mAuth;


    private static final String TAG = IntroActivity.class.getSimpleName();


    private SocialSyncUtil socialSyncUtil;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_intro);

        Log.d(TAG, "test");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        dotContainer = findViewById(R.id.dot_linearlayout);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new Twitter(authConfig));

        mAuth = FirebaseAuth.getInstance();
        socialSyncUtil = new SocialSyncUtil();
        socialSyncUtil.init(mAuth.getCurrentUser());
        List<IntroductionScreen> introductionScreenList = new ArrayList<>();
        introductionScreenList.add(new IntroductionScreen(R.layout.introduction_user) {
            @Override
            public void setup(View view, int position) {
                Log.d(TAG, "count: " + viewPager.getAdapter().getCount());

            }
        });
        introductionScreenList.add(new IntroductionScreen(R.layout.introduction_explaination) {
            @Override
            public void setup(View view, int position) {
                Log.d(TAG, "count: " + viewPager.getAdapter().getCount());

            }
        });


        introductionScreenList.add(new IntroductionScreen(R.layout.introduction_socialmedia) {
            @Override
            public void setup(View view, int position) {
                Log.d(TAG, "count: " + viewPager.getAdapter().getCount());


                ImageView googlePlusImageView = (ImageView) findViewById(R.id.googleplus_materialiconview);
                ImageView twitterImageView = (ImageView) findViewById(R.id.twitter_materialiconview);
                ImageView facebookImageView = (ImageView) findViewById(R.id.facebook_materialiconview);


                if (socialSyncUtil.isGooglePlusSynced()) {
                    showAuthSuccess(R.id.googleplus_materialiconview, "Synced with Google+!");
                }
                if (socialSyncUtil.isTwitterSynced()) {
                    showAuthSuccess(R.id.twitter_materialiconview, "Synced with Twitter!");
                }


                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.googleplus_default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleApiClient = new GoogleApiClient.Builder(view.getContext())
                        .enableAutoManage(IntroActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            }
                        })
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();


                googlePlusImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Configure Google Sign In

                        if(socialSyncUtil.isGooglePlusSynced() == false) {
                            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
                        }else{
                            createSnackbarMessage("Already Synced with Google+");
                        }
                    }
                });
                twitterImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(socialSyncUtil.isTwitterSynced() == false) {
                            client = new TwitterAuthClient();
                            client.authorize(IntroActivity.this, new Callback<TwitterSession>() {
                                @Override
                                public void success(Result<TwitterSession> twitterSessionResult) {
                                    Log.d(TAG, "Logged with twitter");
                                    TwitterSession session = twitterSessionResult.data;
                                    handleTwitterSession(session);
                                }

                                @Override
                                public void failure(com.twitter.sdk.android.core.TwitterException e) {
                                    Log.e(TAG, "Failed login with twitter");
                                    e.printStackTrace();
                                }
                            });
                        }else{
                            createSnackbarMessage("Already Synced with Twitter");
                        }
                    }
                });

            }
        });
        introductionScreenList.add(new IntroductionScreen(R.layout.introduction_interests) {
            @Override
            public void setup(View view, int position) {
                Log.d(TAG, "count: " + viewPager.getAdapter().getCount());

            }
        });

        introductionScreenList.add(new IntroductionScreen(R.layout.introduction_finish) {
            @Override
            public void setup(View view, int position) {
                Log.d(TAG, "count: " + viewPager.getAdapter().getCount());
                Button button = (Button) view.findViewById(R.id.finish_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SettingsUtils.setFinishedIntro(view.getContext(), true);
                        startMainActivity();
                    }
                });

            }
        });


        introductionPagerAdapter = new IntroductionPagerAdapter(introductionScreenList);


        viewPager.setAdapter(introductionPagerAdapter);
        viewPager.setOffscreenPageLimit(introductionPagerAdapter.getCount());
        setupSelectionPoints();
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);


                setSelectionPoint(position);


            }


        });


    }


    private void setupSelectionPoints() {
        int dotsCount = viewPager.getAdapter().getCount(); // -1 because corporate
        dots = new ImageView[dotsCount];
        for (int i = 0; i < dots.length; i++) {
            Log.d(TAG, "test");
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.introduction_circle_nonselected);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
            params.setMargins(20, 0, 20, 0);
            params.gravity = Gravity.CENTER;
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.dot_linearlayout);
            linearLayout.addView(dots[i], params);
        }

        dots[0].setImageResource(R.drawable.introduction_circle_selected);
    }

    private void setSelectionPoint(int position) {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setImageResource(R.drawable.introduction_circle_nonselected);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(20, 0, 20, 0);
            params.gravity = Gravity.CENTER;
        }

        dots[position].setImageResource(R.drawable.introduction_circle_selected);

    }


    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (client != null) {
            client.onActivityResult(requestCode, resultCode, data);
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Log.d(TAG, "successful google login");
            } else {
                Log.d(TAG, result.getStatus() + "errorful google login");
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            showAuthError(R.id.googleplus_materialiconview, "Error syncing with Google+!");
                        } else {
                            showAuthSuccess(R.id.googleplus_materialiconview, "Synced with Google+!");
                        }
                    }
                });
    }

    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            showAuthError(R.id.twitter_materialiconview, "Error syncing with Twitter!");
                        } else {
                            showAuthSuccess(R.id.twitter_materialiconview, "Synced with Twitter!");
                        }
                    }
                });
    }


    public void showAuthSuccess(int imageViewId, String message) {
        ImageView imageView = (ImageView) findViewById(imageViewId);
        imageView.setColorFilter(this.getResources().getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_IN);
        createSnackbarMessage(message);
    }

    public void showAuthError(int imageViewId, String message) {
        ImageView imageView = (ImageView) findViewById(imageViewId);
        imageView.setColorFilter(this.getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_IN);
        createSnackbarMessage(message);
    }


    public void createSnackbarMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(R.color.introduction_color_accent))
                .show();
    }

}
