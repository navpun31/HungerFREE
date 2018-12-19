package com.spectators.hungerfree;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class TabDonateFood extends Fragment{
    private String TAG = "myApp";
    public Button btnSelect;
    public ImageView ivImage;
    public int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public String userChoosenTask;

    public AppCompatButton donateButton ;
    private NestedScrollView nestedScrollView;

    private DatabaseReference database;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    //location vars
    private LatLng latLng;
    private double lat;
    private double lng;
    private String placename;
    private String address;


    private TextInputEditText donateFoodText, QuantityText, dateText, timeText;

    private String donationId;
    private Donation donation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_donate_food, container, false);


        btnSelect = (Button) rootView.findViewById(R.id.btnSelectPhoto);
        ivImage = (ImageView) rootView.findViewById(R.id.ivImage);
        donateButton = (AppCompatButton) rootView.findViewById(R.id.donateButton);

        donateFoodText = (TextInputEditText) rootView.findViewById(R.id.donateFoodText);
        QuantityText = (TextInputEditText) rootView.findViewById(R.id.QuantityText);
        dateText = (TextInputEditText) rootView.findViewById(R.id.dateText);
        timeText = (TextInputEditText) rootView.findViewById(R.id.timeText);

        nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nestedScrollView);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donateButton.setEnabled(false);
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String email = currentUser.getEmail();
                donation = new Donation();

                donation.email = email;

                donation.lat = lat;
                donation.lng = lng;
                donation.locAddress = address;
                donation.locName = placename;

                donation.food = donateFoodText.getText().toString().trim();
                donation.quantity = QuantityText.getText().toString().trim();
                donation.date = dateText.getText().toString().trim();
                donation.time = timeText.getText().toString().trim();

                if(donation.food.equals("") || donation.food==null){
                    Snackbar.make(nestedScrollView, "Please fill all the fields.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                Log.w(TAG,"Success1");

                database = FirebaseDatabase.getInstance().getReference("Donations");
                donationId = database.push().getKey();

                Log.w(TAG,"Success2" + donationId);

//                getImage();

                database.child(donationId).setValue(donation);
                Snackbar.make(nestedScrollView, "Donation Added Successfully.", Snackbar.LENGTH_LONG).show();

                emptyInputEditText();
                donateButton.setEnabled(true);
            }
        });


        //Location
        PlaceAutocompleteFragment places= (PlaceAutocompleteFragment)
                this.getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                 latLng = place.getLatLng();
                 lat = latLng.latitude;
                 lng = latLng.longitude;
                 placename = (String) place.getName();
                 address = (String) place.getAddress();

                Log.w(TAG, placename + "  " + address + "  " + lat + "  " + lng);
            }

            @Override
            public void onError(Status status) {
            }
        });
        return rootView;
    }

    private void emptyInputEditText() {
        donateFoodText.setText(null);
        QuantityText.setText(null);
        dateText.setText(null);
        timeText.setText(null);
    }

    private Uri downloadUrl;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private StorageReference images = storageRef.child("images/" + donationId + ".jpg");
    public void getImage(){
        ivImage.setDrawingCacheEnabled(true);
        ivImage.buildDrawingCache();
        Log.w(TAG,"Success3");
        Bitmap bitmap = ivImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Log.w(TAG,"Success4");
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        Log.w(TAG,"Success5");
        UploadTask uploadTask = storageRef.putBytes(data);
        Log.w(TAG,"Success6" + uploadTask);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.w(TAG,"Fail");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                if(taskSnapshot == null){
                    Log.w(TAG,"NULL");
                }
                Log.w(TAG,"" + taskSnapshot);
                downloadUrl = taskSnapshot.getDownloadUrl();
                donation.imageUri = downloadUrl.toString();
                Log.w(TAG,"Uri " + downloadUrl);
            }
        });

    }






    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library"};

        AlertDialog.Builder builder = new AlertDialog.Builder((TabDonateFood.this).getActivity());

                builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission((TabDonateFood.this).getActivity());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }


    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getActivity().getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }
}
