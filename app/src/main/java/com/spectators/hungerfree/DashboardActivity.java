package com.spectators.hungerfree;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DashboardActivity extends AppCompatActivity {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private boolean fabExpanded = false;
    private FloatingActionButton fabSettings;
    private FloatingActionButton fabLogout;
    private FloatingActionButton fabProfile;
    private FloatingActionButton fabPassword;

    private LinearLayout layoutFabProfile;
    private LinearLayout layoutFabPassword;
    private LinearLayout layoutFabLogout;
    private LinearLayoutCompat back;

    private CoordinatorLayout main_content;

    //closes FAB submenus
    private void closeSubMenusFab(){
        this.layoutFabProfile.setVisibility(View.INVISIBLE);
        this.layoutFabPassword.setVisibility(View.INVISIBLE);
        this.layoutFabLogout.setVisibility(View.INVISIBLE);
        fabSettings.setImageResource(R.drawable.fab_options_open);
        this.fabExpanded = false;
        back.setBackgroundColor(Color.TRANSPARENT);
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        this.layoutFabProfile.setVisibility(View.VISIBLE);
        this.layoutFabPassword.setVisibility(View.VISIBLE);
        this.layoutFabLogout.setVisibility(View.VISIBLE);
        fabSettings.setImageResource(R.drawable.fab_options);
        this.fabExpanded = true;
        back.setBackgroundColor(Color.parseColor("#88767676"));
    }


    private DatabaseReference database;
    private String TAG = "myApp";
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser currUser){
        if(currUser==null){
            Intent intentRegister = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentRegister);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dashboard);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        fabSettings = (FloatingActionButton) this.findViewById(R.id.fabSetting);
        fabLogout = (FloatingActionButton) this.findViewById(R.id.fabLogout);
        fabPassword = (FloatingActionButton) this.findViewById(R.id.fabPass);
        fabProfile = (FloatingActionButton) this.findViewById(R.id.fabProfile);

        layoutFabProfile = (LinearLayout) this.findViewById(R.id.layoutFabProfile);
        layoutFabPassword = (LinearLayout) this.findViewById(R.id.layoutFabPassword);
        layoutFabLogout = (LinearLayout) this.findViewById(R.id.layoutFabLogout);

        back = (LinearLayoutCompat) this.findViewById(R.id.back);

        //When main Fab (Settings) is clicked, it expands if not expanded already.
        //Collapses if main FAB was open already.
        //This gives FAB (Settings) open/close behavior
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.getInstance().signOut();
                Intent intentRegister = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intentRegister);
            }
        });

        fabPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRegister = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(intentRegister);
            }
        });

        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRegister = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intentRegister);
            }
        });

        //Only main FAB is visible in the beginning
        closeSubMenusFab();




//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new TabDonateFood();
                case 1:
                    return new TabHungerSpot();
//                case 2:
//                    return new TabProfile();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position){
            switch (position){
                case 0:
                    return "Donate Food";
                case 1:
                    return "Spot Food";
            }
            return null;
        }
    }




}
