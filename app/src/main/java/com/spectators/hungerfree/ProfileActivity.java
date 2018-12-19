package com.spectators.hungerfree;

import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{


    private NestedScrollView nestedScrollView;
    private AppCompatTextView changeBack;
    private AppCompatTextView textViewName;
    private AppCompatTextView textViewEmail;
    private AppCompatTextView textViewMobile;


    private DatabaseReference database;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String TAG = "myApp";

    private FirebaseUser user;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        changeBack = (AppCompatTextView) findViewById(R.id.changeBack);

        textViewName = (AppCompatTextView) findViewById(R.id.textViewName);
        textViewEmail = (AppCompatTextView) findViewById(R.id.textViewEmail);
        textViewMobile = (AppCompatTextView) findViewById(R.id.textViewMobile);

        changeBack.setOnClickListener(this);

        getUserProfile();
        textViewEmail.setText(user.getEmail());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeBack:
                Intent intentRegister = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intentRegister);
                break;
        }
    }

    private void getUserProfile(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        Log.d(TAG, "Email " + email);

        database = FirebaseDatabase.getInstance().getReference("Profiles");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "CodeBase1");
                int i = 2;
                for(DataSnapshot profileSnap : dataSnapshot.getChildren()){
                    Log.d(TAG, "CodeBase" + i);
//                    String proEmail = "" + profileSnap.child("email").getValue();
//                    Log.d(TAG, "Value is: " + proEmail);
                    profile = profileSnap.getValue(Profile.class);
                    Log.d(TAG, "Value is: " + profile);
                    if(profile.getEmail().equals(email)){
                        textViewName.setText(profile.getName());
                        textViewMobile.setText(profile.getMobile());
                        return;
                    }
                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }
//
//    private void updateUI(FirebaseUser currUser){
//        if(currUser!=null){
//            Intent intentRegister = new Intent(getApplicationContext(), DashboardActivity.class);
//            startActivity(intentRegister);
//        }
//    }
}
