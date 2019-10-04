package com.example.mycontactlist;

import android.Manifest;
import android.app.AppComponentFactory;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class ContactCoordinateActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener gpsListener;
    final int PERMISSION_REQUEST_LOCATION = 101;
    LocationListener networkListener;
    Location currentBestLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_lat_lng_acc);

        initListButton();
        initMapButton();
        initSettingButton();
        initGetLocationButton();
        initReturnToMapButton();


    }

    @Override
    public void onResume() {
        super.onResume();


        String setColor = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE)
                .getString("setcolor", "light green");
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_Lat_Lng_Acc);
        if (setColor.equalsIgnoreCase("light green")) {
            scrollView.setBackgroundResource(R.color.exercise_radio_color_light_green);

        } else if (setColor.equalsIgnoreCase("gold")) {
            scrollView.setBackgroundResource(R.color.exercise_radio_color_gold);

        } else {
            scrollView.setBackgroundResource(R.color.exercise_radio_color_light_purple);

        }

    }

    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactCoordinateActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initMapButton() {
        ImageButton ibMap = (ImageButton) findViewById(R.id.imageButtonMap);
        ibMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactCoordinateActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactCoordinateActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initGetLocationButton() {
        final Button locationButton = (Button) findViewById(R.id.buttonCoords);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(ContactCoordinateActivity.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    ContactCoordinateActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                                Snackbar.make(findViewById(R.id.activity_contact_map),
                                        "MyContactList requires this permission to locate " +
                                                "your contacts", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("OK", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                ActivityCompat.requestPermissions(
                                                        ContactCoordinateActivity.this,
                                                        new String[]{
                                                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                                                        PERMISSION_REQUEST_LOCATION);
                                            }
                                        }).show();
                            } else {
                                ActivityCompat.requestPermissions(ContactCoordinateActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_LOCATION);
                            }
                        } else {
                            startLocationUpdates();
                        }
                    } else {
                        startLocationUpdates();
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Error requesting permission", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            locationManager.removeUpdates(gpsListener);
            locationManager.removeUpdates(networkListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    Exercise 7.1 and 7.2
    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);

            gpsListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    TextView gpsLatitude = (TextView) findViewById(R.id.textHolderGPSLatitude);
                    TextView gpsLongitude = (TextView) findViewById(R.id.textHolderGPSLongitude);
                    TextView gpsAccuracy = (TextView) findViewById(R.id.textHolderGPSAccuracy);

                    gpsLatitude.setText(String.valueOf(location.getLatitude()));
                    gpsLongitude.setText(String.valueOf(location.getLongitude()));
                    gpsAccuracy.setText(String.valueOf(location.getAccuracy()));

                    if (isBetterLocation(location)) {
                        currentBestLocation = location;

                        TextView gpsBestLatitude = (TextView) findViewById(R.id.textHolderBestLatitude);
                        TextView gpsBestLongitude = (TextView) findViewById(R.id.textHolderBestLongitude);
                        TextView gpsBestAccuracy = (TextView) findViewById(R.id.textHolderBestAccuracy);

                        gpsBestLatitude.setText(String.valueOf(location.getLatitude()));
                        gpsBestLongitude.setText(String.valueOf(location.getLongitude()));
                        gpsBestAccuracy.setText(String.valueOf(location.getAccuracy()));
                    }
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
            };

            networkListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    TextView networkLatitude = (TextView) findViewById(R.id.textHolderNetworkLatitude);
                    TextView networkLongitude = (TextView) findViewById(R.id.textHolderNetworkLongitude);
                    TextView networkAccuracy = (TextView) findViewById(R.id.textHolderNetworkAccuracy);

                    networkLatitude.setText(String.valueOf(location.getLatitude()));
                    networkLongitude.setText(String.valueOf(location.getLongitude()));
                    networkAccuracy.setText(String.valueOf(location.getAccuracy()));

                    if (isBetterLocation(location)) {
                        currentBestLocation = location;
                        TextView networkBestLatitude = (TextView) findViewById(R.id.textHolderBestLatitude);
                        TextView networkBestLongitude = (TextView) findViewById(R.id.textHolderBestLongitude);
                        TextView networkBestAccuracy = (TextView) findViewById(R.id.textHolderBestAccuracy);

                        networkBestLatitude.setText(String.valueOf(location.getLatitude()));
                        networkBestLongitude.setText(String.valueOf(location.getLongitude()));
                        networkBestAccuracy.setText(String.valueOf(location.getAccuracy()));
                    }
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
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);


        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error, Location not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                }
                else {
                    Toast.makeText(ContactCoordinateActivity.this,"MyContactList will not locate your contacts.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

//    Exercise 7.2
    private boolean isBetterLocation(Location location) {
        boolean isBetter = false;
        if (currentBestLocation == null) {
            isBetter = true;
        } else if (location.getAccuracy() <= currentBestLocation.getAccuracy()) {
            isBetter = true;
        } else if (location.getTime() - currentBestLocation.getTime() > 5 * 60 * 1000) {
            isBetter = true;
        }
        return isBetter;
    }

    private void initReturnToMapButton() {

        Button buttonGoToMap = (Button) findViewById(R.id.buttonGotoMap);
        buttonGoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactCoordinateActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
