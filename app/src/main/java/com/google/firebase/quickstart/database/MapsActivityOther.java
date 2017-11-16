package com.google.firebase.quickstart.database;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.quickstart.database.models.Locations;

public class MapsActivityOther extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static final String EXTRA_POST_KEY = "post_key";
    private String mPostKey;
    private DatabaseReference mPostReference;
    private ValueEventListener mPostListener;
    Marker mCurrLocationMarker;

    Double lat, lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_other);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);

        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("locations").child(mPostKey);
        Toast.makeText(this, mPostReference.toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, mPostKey, Toast.LENGTH_SHORT).show();
        System.out.println(mPostReference+" ref");
        System.out.println(mPostKey + " key");
        System.out.println("on create");
        System.out.println(lat);
        System.out.println(lon);

    }
/*

    @Override
    public void onStart() {
        super.onStart();

        System.out.println("on start");

        Query myTopPostsQuery =  mPostReference.child("locations").child(mPostKey);

        // Attach a listener to read the data at our posts reference
        System.out.println(mPostReference+" ref start");

        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Locations post = dataSnapshot.getValue(Locations.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        System.out.println(mPostReference+" ref start fin");
    }

*/
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
        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Locations post = dataSnapshot.getValue(Locations.class);
                System.out.println(post);
                lat = post.lat;
                lon = post.lon;
                System.out.println(lat);
                System.out.println(lon);

                //Add a marker in Sydney and move the camera
                // LatLng sydney = new LatLng(lat,lon);
               // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                LatLng latLng = new LatLng(lat, lon);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        Toast.makeText(this, "on map ready", Toast.LENGTH_SHORT).show();
        System.out.println("on map ready");


    }
}
