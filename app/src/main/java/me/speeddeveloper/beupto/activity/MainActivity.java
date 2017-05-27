package me.speeddeveloper.beupto.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.settings.SettingsActivity;
import me.speeddeveloper.beupto.activity.style.BaseActivityNoActionBar;
import me.speeddeveloper.beupto.parser.Rss2Parser;
import me.speeddeveloper.beupto.parser.XmlParser;
import me.speeddeveloper.beupto.util.SettingsUtils;
import me.speeddeveloper.beupto.util.SubscribableUtil;
import me.speeddeveloper.beupto.util.SubscribeUtil;

public class MainActivity extends BaseActivityNoActionBar {


    private Toolbar toolbar;
    private NavigationView navigationView;


    static{
//        XmlParser.registerParser("feed", AtomParser.class);
        XmlParser.registerParser("rss", Rss2Parser.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SubscribableUtil.init();
        SubscribeUtil.init();
                navigationView = (NavigationView) findViewById(R.id.nav_view);


     //   View layout = findViewById(R.id.mainlayout);


       /* int e = ThemeManager.get600Color(this);
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {changeAlpha(e, 220) , e, changeAlpha(e, 150)});
        gd.setCornerRadius(0f);*/



       // layout.setBackgroundDrawable(gd);
       navigationView.findViewById(R.id.nav_header_container).setBackgroundColor(SettingsUtils.ThemeManager.getPrimary600(this));
        initSidebar();
        SettingsUtils.ThemeManager.finishUpdating();

    }

    int changeAlpha(int origColor, int userInputedAlpha) {
        origColor = origColor & 0x00ffffff; //drop the previous alpha value
        return (userInputedAlpha << 24) | origColor; //add the one the user inputted
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateFloatingAddButton();
        makeLowPriorityStuff();
    }

    public void updateFloatingAddButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (SettingsUtils.isShowAddButtonInMainActivity(this)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        } else {
            fab.setVisibility(View.GONE);
        }
    }
    DrawerLayout drawer;
    public void initSidebar() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View subscriptionsContainer = navigationView.findViewById(R.id.nav_item_subscriptions_container);
        subscriptionsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityFromNav(view.getContext(), SubscriptionActivity.class);
            }
        });


        View popularContainer = navigationView.findViewById(R.id.nav_item_popular_container);
        popularContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityFromNav(view.getContext(), PopularActivity.class);
            }
        });

        View settingsContainer = navigationView.findViewById(R.id.nav_item_settings_container);
        settingsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityFromNav(view.getContext(), SettingsActivity.class);
            }
        });

        View aboutContainer = navigationView.findViewById(R.id.nav_item_about);
        aboutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityFromNav(view.getContext(), AboutActivity.class);
            }
        });


        //navigationView.setNavigationItemSelectedListener(this);
    }


    public void startActivityFromNav(Context context, Class<? extends Activity> aclass){
        startActivity(new Intent(context, aclass));
        drawer.closeDrawers();
    }


    // Low priority means things that are not necessarily for the ui
    public void makeLowPriorityStuff() {
        SettingsUtils.setIsFirstStartOfApp(this, false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            new MaterialDialog.Builder(this)
                    .title("Do you really want to leave?")
                    .content("You are the customer, we design the app for you! If you have any concerns contact us please. Your BeUpTo Team.")
                    .positiveText("Stay")
                    .negativeText("Leave Now")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent intent = new Intent(dialog.getContext(),StartActivity.class);
                            intent.putExtra(getString(R.string.intent_start_finish), true);
                            startActivity(intent);
                        }
                    }).build().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.activity_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(SettingsActivity.class);
            return true;
        }
        if (id == R.id.action_search) {
            startActivity(SearchActivity.class);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }




    public void startActivity(Class activityClass){
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

   /* @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/

}
