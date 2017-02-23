package com.example.user.aselab5;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements OnClickListener, LocationListener {

    public LocationManager locationManager;
    public LocationListener locationListener;
    public Context context;
    Location location;
    TextView txtLocation;

    ImageView user_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_photo = (ImageView) findViewById(R.id.user_photo);
        user_photo.setOnClickListener(this);

        txtLocation = (TextView) findViewById(R.id.text_Location);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            try{
//
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//
//                if (locationManager!= null){
//                    location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
//
//                    geocoder = new Geocoder(this,Locale.getDefault());
//
//                    try {
//                        addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    String address = addresses.get(0).getAddressLine(0);
//                    txtLocation.setText(address);
//                    Log.i(this.getClass().getName(),address);
//                }
//
//            }
//            catch (SecurityException e){
//                Log.e("onActivityResult", "" + e);
//            }
//        }

        double latitute = 0, longitude = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //show message or ask permissions from the user.
            return;
        }
        //Getting the current location of the user.
        if(locationManager != null){
            Location location = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                latitute = location.getLatitude();
                longitude = location.getLongitude();
            }else{
                location = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                Log.d("Location", location.toString());
                latitute = location.getLatitude();
                longitude = location.getLongitude();
            }

        }
        try {
            if(latitute > 0.0 || longitude > 0.0){
                String add = new Geocoder(getApplicationContext()).getFromLocation(latitute, longitude, 1).get(0).getAddressLine(0) + "\n"
                        + new Geocoder(getApplicationContext()).getFromLocation(latitute, longitude, 1).get(0).getAddressLine(1) + "\n" +
                        new Geocoder(getApplicationContext()).getFromLocation(latitute, longitude, 1).get(0).getAddressLine(2);
                //Log.e(this.getClass().getName(),new Geocoder(getApplicationContext()).getFromLocation(lat, lon, 1).get(0).getAddressLine(0));
                txtLocation.setText(add);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if (v.getId() == R.id.user_photo) {
            // call dialog to picture mode camera / gallery
            Image_Picker_Dialog();
        }

    }

    public void Image_Picker_Dialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Pictures Option");
        myAlertDialog.setMessage("Select Picture Mode");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Utility.pictureActionIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
                Utility.pictureActionIntent.setType("image/*");
                Utility.pictureActionIntent.putExtra("return-data", true);
                startActivityForResult(Utility.pictureActionIntent, Utility.GALLERY_PICTURE);
            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Utility.pictureActionIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(Utility.pictureActionIntent, Utility.CAMERA_PICTURE);
            }
        });
        myAlertDialog.show();

    }

    // After the selection of image you will retun on the main activity with bitmap image
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utility.GALLERY_PICTURE) {
            // data contains result
            // Do some task
            Image_Selecting_Task(data);
        } else if (requestCode == Utility.CAMERA_PICTURE) {
            // Do some task
            Image_Selecting_Task(data);
        }
    }

    public void Image_Selecting_Task(Intent data) {
        try {
            Utility.uri = data.getData();
            if (Utility.uri != null) {
                // User had pick an image.
                Cursor cursor = getContentResolver().query(Utility.uri, new String[]
                        {android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                cursor.moveToFirst();
                // Link to the image
                final String imageFilePath = cursor.getString(0);

                //Assign string path to File
                Utility.Default_DIR = new File(imageFilePath);

                // Create new dir MY_IMAGES_DIR if not created and copy image into that dir and store that image path in valid_photo
                Utility.Create_MY_IMAGES_DIR();

                // Copy your image
                Utility.copyFile(Utility.Default_DIR, Utility.MY_IMG_DIR);

                // Get new image path and decode it
                Bitmap b = Utility.decodeFile(Utility.Paste_Target_Location);

                // use new copied path and use anywhere
                String valid_photo = Utility.Paste_Target_Location.toString();
                b = Bitmap.createScaledBitmap(b, 150, 150, true);

                //set your selected image in image view
                user_photo.setImageBitmap(b);
                cursor.close();

            } else {
                Toast toast = Toast.makeText(this, "Sorry!!! You haven't selecet any image.", Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (Exception e) {
            // you get this when you will not select any single image
            Log.e("onActivityResult", "" + e);

        }
    }


@Override
public void onLocationChanged(Location location) {

}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void validate(View v){
        EditText firstNameCtrl = (EditText)findViewById(R.id.text_Firstname);
        EditText lastNameCtrl = (EditText) findViewById(R.id.text_Lastname);
        EditText usernameCtrl = (EditText)findViewById(R.id.text_Username);
        EditText passwordCtrl = (EditText) findViewById(R.id.text_Password);
        TextView errorText = (TextView)findViewById(R.id.lbl_Error);
        String userName = usernameCtrl.getText().toString();
        String password = passwordCtrl.getText().toString();
        String firstName = firstNameCtrl.getText().toString();
        String lastName = lastNameCtrl.getText().toString();

        boolean validationFlag = false;
        //Verify if the username and password are not empty.
        if(!userName.isEmpty() && !password.isEmpty()&& !firstName.isEmpty() && !lastName.isEmpty()) {

                validationFlag = true;
            Intent redirect = new Intent(this, HomeActivity.class);
            startActivity(redirect);

        }
        if(!validationFlag)
        {
            errorText.setVisibility(View.VISIBLE);
        }
        else
        {
            //This code redirects  from login page to the home page.
            Intent redirect = new Intent(this, HomeActivity.class);
            startActivity(redirect);
        }
    }

    public void signIn(View v) {
        //This code redirects the from Sign up page to the Sign In page.
        Intent redirect = new Intent(this, SigninActivity.class);
        startActivity(redirect);
    }

}
