package com.spectators.hungerfree;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TabHungerSpot extends Fragment {
    private AppCompatButton viewLocations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_hunger_spot, container, false);

        viewLocations = (AppCompatButton) rootView.findViewById(R.id.viewLocations);
        viewLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegister = new Intent((TabHungerSpot.this).getActivity(), MapsActivity.class);
                startActivity(intentRegister);
            }
        });

        return rootView;
    }



    private DatabaseReference database, databasePro;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String TAG = "myApp";

    ArrayList<Donation> list;
    Profile profile;
    int i = 0;

    void getDonors() {
        list = new ArrayList<Donation>();
        database = FirebaseDatabase.getInstance().getReference("Donations");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "CodeBase1");

                for (DataSnapshot profileSnap : dataSnapshot.getChildren()) {
                    Log.d(TAG, "CodeBase" + i);
                    Donation donation;
                    donation = profileSnap.getValue(Donation.class);
                    Log.d(TAG, "Value is: " + donation.email);
                    list.add(donation);
                    Log.d(TAG, "Size: " + list.size());


                    databasePro = FirebaseDatabase.getInstance().getReference("Profiles");
                    databasePro.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot profileSnap : dataSnapshot.getChildren()) {
                                profile = profileSnap.getValue(Profile.class);
                                Log.d(TAG, "Value is: " + profile);
                                if (profile.getEmail().equals(donation.email)) {


                                    break;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        Log.d("myApp", "123456789    " + list.size());
    }
}