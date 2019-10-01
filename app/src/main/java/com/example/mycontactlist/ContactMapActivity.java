package com.example.mycontactlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.location.LocationManager;
import android.location.LocationListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;


public class ContactMapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener {

    final int PERMISSION_REQUEST_LOCATION = 101;
    GoogleMap gMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    ArrayList<Contact> contacts = new ArrayList<>();
    Contact currentContact = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_map);

        Bundle extras = getIntent().getExtras();

        try {
            ContactDataSource ds = new ContactDataSource(ContactMapActivity.this);
            ds.open();
            if(extras != null) {
                currentContact = ds.getSpecificContact(extras.getInt("contactid"));
            }
            else {
                contacts = ds.getContacts("contactname", "ASC");
            }
            ds.close();
        }
        catch (Exception e) {
            Toast.makeText(this, "Contact(s) could not be retrieved.",
                    Toast.LENGTH_LONG).show();
        }

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        createLocationRequest();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        initListButton();
        initMapButton();
        initSettingButton();
        initMapTypeButton();
        initGetCoordinateButton();

    }

    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactMapActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
    private void initMapButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonMap);
        ibList.setEnabled(false);
    }
    private void initSettingButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactMapActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();

    }

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
        gMap.setMyLocationEnabled(true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                }
                else {
                    Toast.makeText(ContactMapActivity.this,
                            "MyContactList will not locate your contacts.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Point size = new Point();
        WindowManager w = getWindowManager();
        w.getDefaultDisplay().getSize(size);
        int measuredWidth = size.x;
        int measuredHeight = size.y;

        if (contacts.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < contacts.size(); i++) {
                currentContact = contacts.get(i);

                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;

                String address =
                        currentContact.getStreetAddress() + ", " +
                                currentContact.getCity() + ", " +
                                currentContact.getState() + " " +
                                currentContact.getZipCode();

                try {
                    addresses = geo.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LatLng point = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                builder.include(point);

                gMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_marker_40)));

            }

//            IMCOMPLETE
//            Currently displays the last Contact info for everyone

            gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }
                @Override
                public View getInfoContents(Marker marker) {

                    View row = getLayoutInflater().inflate(R.layout.pin_marker_contact_details, null);
                    TextView t1_Title = (TextView) row.findViewById(R.id.markerTitle);
                    TextView t2_Address = (TextView) row.findViewById(R.id.markerAddress);
                    TextView t3_HomePhone = (TextView) row.findViewById(R.id.markerHomePhoneNumber);
                    TextView t4_CellPhone = (TextView) row.findViewById(R.id.markerCellPhoneNumber);

                    String address =
                            currentContact.getStreetAddress() + ", " +
                                    currentContact.getCity() + ", " +
                                    currentContact.getState() + " " +
                                    currentContact.getZipCode();

                    t1_Title.setText(currentContact.getContactName());
                    t2_Address.setText(address);
                    t3_HomePhone.setText("Home: " + currentContact.getPhoneNumber());
                    t4_CellPhone.setText("Cell: " + currentContact.getCellNumber());


                    return row;
                }
            });

            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), measuredWidth, measuredHeight, 250));
        }
        else {
            if (currentContact != null) {
                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;

                String address =
                        currentContact.getStreetAddress() + ", " +
                        currentContact.getCity() + ", " +
                        currentContact.getState() + " " +
                        currentContact.getZipCode();

                try {
                    addresses = geo.getFromLocationName(address, 1);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                final LatLng point = new LatLng(addresses.get(0).getLatitude(),
                        addresses.get(0).getLongitude());

                gMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_marker_40)));

                gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                          return null;
                    }
                    @Override
                    public View getInfoContents(Marker marker) {

                        View row = getLayoutInflater().inflate(R.layout.pin_marker_contact_details, null);
                        TextView t1_Title = (TextView) row.findViewById(R.id.markerTitle);
                        TextView t2_Address = (TextView) row.findViewById(R.id.markerAddress);
                        TextView t3_HomePhone = (TextView) row.findViewById(R.id.markerHomePhoneNumber);
                        TextView t4_CellPhone = (TextView) row.findViewById(R.id.markerCellPhoneNumber);

                        String address =
                            currentContact.getStreetAddress() + ", " +
                            currentContact.getCity() + ", " +
                            currentContact.getState() + " " +
                            currentContact.getZipCode();

                        t1_Title.setText(currentContact.getContactName());
                        t2_Address.setText(address);
                        t3_HomePhone.setText("Home: " + currentContact.getPhoneNumber());
                        t4_CellPhone.setText("Cell: " + currentContact.getCellNumber());


                        return row;
                    }
                });

                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
            }
            else {
                AlertDialog alertDialog = new AlertDialog.Builder(ContactMapActivity.this).create();
                alertDialog.setTitle("No Data");
                alertDialog.setMessage("No data is available for the mapping function.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    } });
                alertDialog.show();
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(ContactMapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            ContactMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                        Snackbar.make(findViewById(R.id.activity_contact_map),
                                "MyContactList requires this permission to locate " +
                                        "your contacts", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ActivityCompat.requestPermissions(
                                                ContactMapActivity.this,
                                                new String[]{
                                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                                PERMISSION_REQUEST_LOCATION);
                                    }
                                }).show();
                    }
                    else {
                        ActivityCompat.requestPermissions(ContactMapActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_LOCATION);
                    }
                }
                else {
                    startLocationUpdates();
                }
            }
            else {
                startLocationUpdates();
            }
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error requesting permission", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getBaseContext(), "Lat: " + location.getLatitude() +
                " Long: " + location.getLongitude() +
                " Accuracy: " + location.getAccuracy(), Toast.LENGTH_LONG).show();
    }

    private void initMapTypeButton() {
        final Button satellitebtn = (Button) findViewById(R.id.buttonMapType);
        satellitebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentSetting = satellitebtn.getText().toString();
                if (currentSetting.equalsIgnoreCase("Satellite View")) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    satellitebtn.setText("Normal View");
                }
                else {
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    satellitebtn.setText("Satellite View");
                }
            }
        });
    }

//    Exercise 7.1 & 7.2
    private void initGetCoordinateButton() {

        Button coordinatebtn = (Button) findViewById(R.id.buttonGetCoordinate);
        coordinatebtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                Intent intent = new Intent(ContactMapActivity.this, ContactCoordinateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
             }
        });
    }

//        final Button coordinatebtn = (Button) findViewById(R.id.buttonGetCoordinate);
//        coordinatebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String currentSetting = coordinatebtn.getText().toString();
//                if (currentSetting.equalsIgnoreCase("Get Coordinates")) {
//
//                    getView(View , R.layout.view_lat_lng_acc);
////                    ListView listView = (ListView) findViewById(R.id.lv_Lat_Lng_Acc);
////                    adapter = new SimpleAdapter(this, ,R.layout.view_lat_lng_acc);
////                    listView.setAdapter(adapter);
//
//                    coordinatebtn.setText("Map");
//                }
//                else {
//                    final Button satellitebtn = (Button) findViewById(R.id.buttonMapType);
//                    satellitebtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            String currentSetting = satellitebtn.getText().toString();
//                            if (currentSetting.equalsIgnoreCase("Satellite View")) {
//                                gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                                satellitebtn.setText("Normal View");
//                            }
//                            else {
//                                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                                satellitebtn.setText("Satellite View");
//                            }
//                        }
//                    });
//                    coordinatebtn.setText("Get Coordinates");
//                }
//            }
//        });
//
//    }
//
//    public View getView(View convertView, ViewGroup parent)   {
//        View v = convertView;
//            if (v == null)  {
//                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                v = vi.inflate(R.layout.view_lat_lng_acc, null);
//            }

}
