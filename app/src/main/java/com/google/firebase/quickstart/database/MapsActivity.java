package com.google.firebase.quickstart.database;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.quickstart.database.models.Locations;
import com.google.firebase.quickstart.database.models.User;

import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.quickstart.database.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    public static Double LAT = 0.0;
    public static Double LON = 0.0;
    public boolean sharing;
    public String keySharing = "";

    private static final String TAG = "NewPostActivity";


    private FusedLocationProviderClient mFusedLocationClient;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        //preguntar si ya esta compartiendo, si si el boton de share debe estar escondido y el lugar de eso debe de aparecer el de cancelar
        //NICE TO HAVE

        toKnowIfTheUserIsSharing();

        if (!keySharing.equals("")) {
            findViewById(R.id.fab_new_post3).setVisibility(View.VISIBLE);
            findViewById(R.id.fab_new_post2).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.fab_new_post2).setVisibility(View.VISIBLE);
            findViewById(R.id.fab_new_post3).setVisibility(View.INVISIBLE);
        }

        findViewById(R.id.fab_new_post2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert a firebase
                getValues();

            }
        });

        findViewById(R.id.fab_new_post3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert a firebase
                stopSharing();

            }
        });


}


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Toast.makeText(this, "ON LOCATION CHANGED", Toast.LENGTH_SHORT).show();

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker

        LAT = location.getLatitude();
        LON = location.getLongitude();

        //update values

        LatLng latLng = new LatLng(LAT, LON);

        Toast.makeText(this, LAT + "**" + LON, Toast.LENGTH_SHORT).show();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        System.out.println(LAT);
        System.out.println(LON);
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //update a firebase

                    if(!keySharing.equals("")) {

                        Toast.makeText(this, "entro update firebase", Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference().child("locations").child(keySharing).child("lat").setValue(LAT);
                        FirebaseDatabase.getInstance().getReference().child("locations").child(keySharing).child("lon").setValue(LON);

                    }



        //updated firebase

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    private void writeNewPost(String userId, String username, Double lat, Double lon,String route) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("locations").push().getKey();
        Locations location = new Locations(userId, username, lat, lon, route);
        Map<String, Object> postValues = location.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/locations/" + key, postValues);
        childUpdates.put("/user-locations/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    public void getValues() {

        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(MapsActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.username,LAT,LON,"1");
                        }

                        // Finish this Activity, back to the stream
                        //setEditingEnabled(true);
                        //finish();
                        Toast.makeText(MapsActivity.this,
                                "You are sharing your location",
                                Toast.LENGTH_SHORT).show();

                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        //setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void toKnowIfTheUserIsSharing() {

        //get the current user location si ya esta compartiendo hacer un update a firebase

        final String userKey = FirebaseDatabase.getInstance().getReference().child("user-locations").child(getUid()).getKey();

        //key tiene el user

        Query query = FirebaseDatabase.getInstance().getReference().child("locations").orderByChild("uid").equalTo(userKey);
        //Toast.makeText(this, query.getRef().getKey() + " key location", Toast.LENGTH_SHORT).show();


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.getValue() != null) {
                    System.out.println(mDatabase + " is sharing");
                    sharing = true;

                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        Log.v("children ", snap.getKey().toString());
                        System.out.println(snap.child("uid").getValue().toString());
                        System.out.println(userKey);
                        if (snap.child("uid").getValue().toString().equals(userKey)) {
                            System.out.println("entro al if" );
                            keySharing = snap.getKey().toString();
                            System.out.println("key sharing " + keySharing);
                           // FirebaseDatabase.getInstance().getReference().child("locations").child(keySharing).child("lat").setValue(0.118);
                        }
                    }

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed");
            }
        });



    }

    public void stopSharing () {
        //seleccionar user location, location and delete las 2
    }

}
