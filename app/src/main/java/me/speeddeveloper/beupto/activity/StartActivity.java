package me.speeddeveloper.beupto.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import android.widget.Toast;

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
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.Callback;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import me.speeddeveloper.beupto.MainApplication;
import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.introduction.IntroductionPagerAdapter;
import me.speeddeveloper.beupto.introduction.IntroductionScreen;
import me.speeddeveloper.beupto.util.MyUtil;
import me.speeddeveloper.beupto.util.SettingsUtils;
import me.speeddeveloper.beupto.util.UserUtil;

/**
 * Created by phili on 7/17/2016.
 */
public class StartActivity extends AppCompatActivity {


    private static final String TAG = StartActivity.class.getSimpleName();
    private IntroductionPagerAdapter introductionPagerAdapter;
    private ViewPager viewPager;
    private ImageView[] dots;
    private View dotContainer;
    private TwitterAuthClient client;
    private GoogleApiClient mGoogleApiClient;
    final static int RC_GOOGLE_SIGN_IN = 500;
    private FirebaseAuth mAuth;

    private String username;
    private String password;
    private String email;

    // TODO cipher password and email

    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in

                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                if(username != null && username.isEmpty() == false && password != null && password.isEmpty() == false)
                    UserUtil.setUserToSettingUtils(StartActivity.this, username, password, email);


                SettingsUtils.setFinishedSignUp(StartActivity.this, true);
                Intent intent = new Intent(StartActivity.this, IntroActivity.class);
                startActivity(intent);

               /* if(SettingsUtils.hasFinishedSignUp(StartActivity.this)){
                    if(SettingsUtils.hasFinishedIntro(StartActivity.this)){
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(StartActivity.this, IntroActivity.class);
                        startActivity(intent);
                    }

                }else{
                    SettingsUtils.setFinishedSignUp(StartActivity.this, true);
                    Intent intent = new Intent(StartActivity.this, IntroActivity.class);
                    startActivity(intent);

                }*/
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            // ...
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
       /* TwitterAuthConfig authConfig =  new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        Fabric.with(StartActivity.this, new Twitter(authConfig));*/

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        dotContainer = findViewById(R.id.dot_linearlayout);

        List<IntroductionScreen> introductionScreenList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

            introductionScreenList.add(new IntroductionScreen(R.layout.introduction_corporate) {
                @Override
                public void setup(View view, int position) {
                   View v = view.findViewById(R.id.sign_up_linearlayout);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        }
                    });
                }
            });
            introductionScreenList.add(new IntroductionScreen(R.layout.introduction_sign_up) {
                @Override
                public void setup(View view, int position) {

                    final EditText usernameEditText = (EditText) findViewById(R.id.username_edittext);
                    final EditText passwordEditText = (EditText) findViewById(R.id.password_edittext);
                    final EditText emailEditText = (EditText) findViewById(R.id.email_edittext);

                    Button button = (Button) findViewById(R.id.sign_up_button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                             username = usernameEditText.getText().toString();
                             password = passwordEditText.getText().toString();
                             email = emailEditText.getText().toString();
                            if(UserUtil.isStringValidatableForAuth(username) != true && UserUtil.isStringValidatableForAuth(password) != true){
                                UserUtil.showValidatableError(view.getContext());
                                return;
                            }
                            Log.d(TAG, "username " + username);
                            Log.d(TAG, "password " + password);
                            mAuth.createUserWithEmailAndPassword(username + "@beupto.com", password).addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());


                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(StartActivity.this, "auth failed",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });


                        }
                    });
                }
            });


        introductionPagerAdapter = new IntroductionPagerAdapter(introductionScreenList);
        viewPager.setAdapter(introductionPagerAdapter);
        viewPager.setOffscreenPageLimit(introductionPagerAdapter.getCount());


        /*List<IntroductionScreen> introductionScreenList = new ArrayList<>();
        if (SettingsUtils.isFirstStartOfApp(this)) {
            introductionScreenList.add(new IntroductionScreen(R.layout.introduction_corporate) {
                @Override
                public void setup(View view, int position) {
                    View startIntroductionView = view.findViewById(R.id.start_introduction_linearlayout);
                    startIntroductionView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        }
                    });
                }
            });
            introductionScreenList.add(new IntroductionScreen(R.layout.introduction_explaination) {
                @Override
                public void setup(View view, int position) {
                    Log.d(TAG, "count: " + viewPager.getAdapter().getCount());

                }
            });

            introductionScreenList.add(new IntroductionScreen(R.layout.introduction_sign_up) {
                @Override
                public void setup(View view, int position) {
                    Log.d(TAG, "count: " + viewPager.getAdapter().getCount());
                    final EditText editText = (EditText) view.findViewById(R.id.name_edittext);
                    *//*viewPager.setOnTouchListener(new View.OnTouchListener()
                    {
                        public boolean onTouch(View p_v, MotionEvent p_event)
                        {
                            viewPager.getParent().getParent().requestDisallowInterceptTouchEvent(false);
                            //  We will have to follow above for all scrollable contents
                            return false;
                        }
                    });*//*
                }
            });
            introductionScreenList.add(new IntroductionScreen(R.layout.introduction_socialmedia) {
                @Override
                public void setup(View view, int position) {
                    Log.d(TAG, "count: " + viewPager.getAdapter().getCount());











                    ImageView googlePlusImageView = (ImageView) findViewById(R.id.googleplus_materialiconview);
                    ImageView twitterImageView = (ImageView) findViewById(R.id.twitter_materialiconview);
                    ImageView facebookImageView = (ImageView) findViewById(R.id.facebook_materialiconview);


                    for(String provider : mAuth.getCurrentUser().getProviders()){
                        if(GoogleAuthProvider.PROVIDER_ID.equalsIgnoreCase(provider)){
                            showSuccessfulGoogleSignIn();
                        }
                        if(TwitterAuthProvider.PROVIDER_ID.equalsIgnoreCase(provider)){
                            showSuccessfulTwitterSignIn();
                        }

                    }


                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.googleplus_default_web_client_id))
                            .requestEmail()
                            .build();
                    mGoogleApiClient = new GoogleApiClient.Builder(view.getContext())
                            .enableAutoManage(StartActivity.this *//* FragmentActivity *//*, new GoogleApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                                }
                            } *//* OnConnectionFailedListener *//*)
                            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                            .build();



                    googlePlusImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Configure Google Sign In


                                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);

                        }
                    });
                    twitterImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            client = new TwitterAuthClient();
                            client.authorize(StartActivity.this, new Callback<TwitterSession>() {
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
                            startMainActivity();
                        }
                    });

                }
            });


        } else {
            introductionScreenList.add(new IntroductionScreen(R.layout.corportate) {
                @Override
                public void setup(View view, int position) {
                    Log.d(TAG, "count: " + viewPager.getAdapter().getCount());
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startMainActivity();
                        }
                    });

                }
            });

        }
        introductionPagerAdapter = new IntroductionPagerAdapter(introductionScreenList);





        viewPager.setAdapter(introductionPagerAdapter);
        viewPager.setOffscreenPageLimit(introductionPagerAdapter.getCount());
        setupSelectionPoints();
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(position != 0) {
                    dotContainer.setVisibility(View.VISIBLE);
                    setSelectionPoint(position);
                }else{
                    dotContainer.setVisibility(View.GONE);
                }


            }


        });*/

      /*  View view = findViewById(R.id.sign_up_linearlayout);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/






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

    private void setSelectionPoint(int position){
        for (int i = 0; i < dots.length; i++) {
            dots[i].setImageResource(R.drawable.introduction_circle_nonselected);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(20, 0, 20, 0);
            params.gravity = Gravity.CENTER;
        }

        dots[position].setImageResource(R.drawable.introduction_circle_selected);

    }


    public void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean doFinish = intent.getBooleanExtra(getString(R.string.intent_start_finish), false);
        if(doFinish)
            finish();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(client != null){
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
                        }else{
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
                        }else{
                            showAuthSuccess(R.id.twitter_materialiconview, "Synced with Twitter!");
                        }
                    }
                });
    }






    public void showAuthSuccess(int imageViewId, String message){
        ImageView imageView = (ImageView) findViewById(imageViewId);
        imageView.setColorFilter(this.getResources().getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_IN);
        createSnackbarMessage(message);
    }
    public void showAuthError(int imageViewId, String message){
        ImageView imageView = (ImageView) findViewById(imageViewId);
        imageView.setColorFilter(this.getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_IN);
        createSnackbarMessage(message);
    }



    public void showSuccessfulGoogleSignIn(){

        ImageView googlePlusImageView = (ImageView) findViewById(R.id.googleplus_materialiconview);
        googlePlusImageView.setColorFilter(this.getResources().getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_IN);
        createSnackbarMessage("Synced with Google+!");

    }

    public void showSuccessfulTwitterSignIn(){

        createSnackbarMessage("Synced with Twitter!");
        ImageView twitterImageView = (ImageView) findViewById(R.id.twitter_materialiconview);
        twitterImageView.setColorFilter(this.getResources().getColor(android.R.color.holo_green_dark), PorterDuff.Mode.SRC_IN);

    }

    public void createSnackbarMessage(String message){
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(R.color.introduction_color_accent ))
                .show();
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if(SettingsUtils.hasFinishedSignUp(this)) {
            mAuth.signInWithEmailAndPassword(SettingsUtils.getUsername(this) + "@beupto.com", SettingsUtils.getPassword(this));
        }
      /*  if(!(SettingsUtils.hasUserAlreadySignedUp(this))) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInAnonymously", task.getException());
                                Toast.makeText(StartActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            // ...
                        }
                    });


        }else{

        }
*/

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
