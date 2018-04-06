package jaffa.com.jaffareviews;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import jaffa.com.jaffareviews.Fragments.AddRatingFragment;
import jaffa.com.jaffareviews.Fragments.ConnectionsFragment;
import jaffa.com.jaffareviews.Fragments.MainGridFragment;
import jaffa.com.jaffareviews.Fragments.MovieDetailFragment;
import jaffa.com.jaffareviews.Fragments.ProfileFragment;
import jaffa.com.jaffareviews.Fragments.ProfileFragmentOld;
import jaffa.com.jaffareviews.Fragments.ReviewsByUserFragment;
import jaffa.com.jaffareviews.Fragments.SearchFragment;
import jaffa.com.jaffareviews.Fragments.SettingsFragment;
import jaffa.com.jaffareviews.FullScreenDialog.FullScreenDialogFragment;

public class MainActivity extends AppCompatActivity
        implements
        MainGridFragment.OnMainGridFragmentListener,
        MovieDetailFragment.OnMovieDetailFragmentListener,
        SearchFragment.OnSearchFragmentListener,
        SettingsFragment.OnSettingsFragmentListener,
        ConnectionsFragment.OnConnectionsFragmentListener,
        ReviewsByUserFragment.OnReviewsByUserFragmentListener,
        ProfileFragment.OnProfileFragmentListener,
        AddRatingFragment.OnAddRatingFragmentListener{

    private TextView mTextMessage;
    private Toolbar toolbar;
    private TextView mToolBarTitle;
    private ImageButton mToolbar_profile, mToolbar_info;
//    private static Tracker mTracker;
    final String dialogTag = "dialog";
    private Context context;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mToolBarTitle.setText(R.string.title_home);
                    launchMainGridFragment();
                    return true;
                case R.id.navigation_search:
                    mToolBarTitle.setText(R.string.title_search);
                    launchSearchFragment();
                    return true;
                case R.id.navigation_dashboard:
                    mToolBarTitle.setText(R.string.title_dashboard);
                    launchConnectionsFragment();
                    return true;
                case R.id.navigation_profile:
                    mToolBarTitle.setText(R.string.title_settings);
                    launchSettingsFragment();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

//        AnalyticsApplication application = (AnalyticsApplication) getApplication();
//        mTracker = application.getDefaultTracker();
//
//        mTracker.setScreenName("Main activity");
//        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mToolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolbar_profile = (ImageButton) findViewById(R.id.toolbar_profile);
        mToolbar_info = (ImageButton) findViewById(R.id.toolbar_info);

        mToolBarTitle.setText(R.string.title_home);
        launchMainGridFragment();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mToolbar_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainlist_fragment, ProfileFragment.newInstance());
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onMovieClick(String title, String movieid) {
        launchMovieDetailFragment(title, movieid);
    }

    @Override
    public  void onMyReviewClick(String fbID){
        launchReviewByUserFragment(fbID);
    }

    @Override
    public  void onConnectionClick(String fbID){
        launchReviewByUserFragment(fbID);
    }

    @Override
    public  void onReviewClick(String fbID){
        launchReviewByUserFragment(fbID);
    }

    @Override
    public  void onLeaderBoadClick(String fbID){
        launchReviewByUserFragment(fbID);
    }

    @Override
    public  void onSearchClick(String movieName, String movieid){
        launchMovieDetailFragment(movieName,movieid);
    }

    private void launchMainGridFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, MainGridFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchMovieDetailFragment(String title, String movieid) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, MovieDetailFragment.newInstance(title, movieid));
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchSearchFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, SearchFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchConnectionsFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, ConnectionsFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchSettingsFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, SettingsFragment.newInstance());
        ft.addToBackStack(null);
        ft.commit();
    }

    private void launchReviewByUserFragment(String fbID) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainlist_fragment, ReviewsByUserFragment.newInstance(fbID));
        ft.addToBackStack(null);
        ft.commit();
    }
}
