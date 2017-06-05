package com.example.android.myapplication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static java.lang.System.exit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    String mAddressOutput=null;

    private GoogleMap mMap,mMap1;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Boolean mRequestingLocationUpdates;
    public AddressResultReceiver mResultReceiver=new AddressResultReceiver(null);
    TextView address;
    int GPSoff=0;
    Long number;
    DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        address= (TextView) findViewById(R.id.address);
        Bundle extras=getIntent().getExtras();
        number=Long.parseLong(extras.getString("number"));
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                    .addApi(LocationServices.API)
                    .build();
        }

        createLocationRequest();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Query query=myRef.child("LOCALITY").orderByChild("RATE");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    String name=postSnapshot.getKey();
                    String rate= ""+ postSnapshot.child("RATE").getValue();


                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocationName(name, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(addresses.size() > 0) {
                        double latitude= addresses.get(0).getLatitude();
                        double longitude= addresses.get(0).getLongitude();
                        LatLng sydney = new LatLng( latitude,longitude);
                        int new_rate=Integer.parseInt(rate);
                        if(new_rate<2){
                        mMap1.addMarker( new MarkerOptions().position(sydney).title(name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        mMap1.moveCamera(CameraUpdateFactory.newLatLng(sydney));}
                        else if((new_rate>2)&&(new_rate<5)){
                            mMap1.addMarker( new MarkerOptions().position(sydney).title(name)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            mMap1.moveCamera(CameraUpdateFactory.newLatLng(sydney));}
                        else{
                            mMap1.addMarker( new MarkerOptions().position(sydney).title(name)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            mMap1.moveCamera(CameraUpdateFactory.newLatLng(sydney));}

                        }
                    }


                }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                //   final LocationSettingsStates = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        mRequestingLocationUpdates = true;

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        mRequestingLocationUpdates = true;
                        try {
                            GPSoff = Settings.Secure.getInt(getContentResolver(),Settings.Secure.LOCATION_MODE);
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (GPSoff == 0) {
                            new AlertDialog.Builder(MapsActivity.this)
                                    .setTitle("Location")
                                    .setMessage("You need to Switch on your GPS")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivity(onGPS);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            exit(0);
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }

        });
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
       mMap1= mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
       mMap.setMyLocationEnabled(true);
    }

    protected void onStart() {
             mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    protected void onStop() {
    //   mGoogleApiClient.disconnect();
    //    myRef.child("PUBLIC").child(number+"").child("STATUS").setValue("NO");
        super.onStop();
    }

    protected void startIntentService(Location location) {
        Log.e("i am in startIntent","gug");
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        {
            myRef.child("PUBLIC").child(number+"").child("STATUS").setValue("YES");


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                 //   Toast.makeText(this, mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();
                    LatLng sydney = new LatLng( mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    mMap1.addMarker(new MarkerOptions().position(sydney).title("Here I am"));
                    mMap1.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                 //   mMap1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                }
                startLocationUpdates();


        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
           Toast.makeText(this, location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
            address.setText(mAddressOutput);
            LatLng sydney = new LatLng( location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Here I am new"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            myRef.child("PUBLIC").child(number+"").child("CURRENT STATUS").setValue(location.getLatitude()+" "+location.getLongitude());
            Query query=myRef.child("LOCALITY").orderByChild("RATE");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("pura data",dataSnapshot+"");
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        String locality_name=postSnapshot.getKey().toString();
                        long rate= (long) postSnapshot.child("RATE").getValue();
                        if(locality_name.equalsIgnoreCase(mAddressOutput)){
                            if(rate>2){
                                NotificationCompat.Builder builder =
                                        new NotificationCompat.Builder(MapsActivity.this)
                                                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                                .setContentTitle("Ping Notification")
                                                .setContentText("Danger zone")
                                                .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                                                .setPriority(NotificationCompat.PRIORITY_HIGH); //must give priority to High, Max which will considered as heads-up notification
                                NotificationManager mNotificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.notify(0, builder.build());


                            }
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }
        startIntentService(location);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.e("onReceiveResult",resultData+"");
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            Log.e("onResultResult2",mAddressOutput);
            Toast.makeText(MapsActivity.this, "hvhjvhj", Toast.LENGTH_LONG).show();

        }

    }
}
