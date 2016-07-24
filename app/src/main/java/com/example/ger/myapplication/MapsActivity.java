package com.example.ger.myapplication;

import android.Manifest;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button mSaveButton;
    private EditText mText;


    private LocationManager mLocationManager;
    private OnCameraChangeListener mCameraManager;

    private final float minZoom = 17.0f;
    private final float initialZoom = 19.0f;
    private static final int INITIAL_REQUEST=1337;
    private LatLng lastLatLng;
    private Marker userMarker;

    private static final String[] PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(PERMS, INITIAL_REQUEST);

        Config.restUrl = ConfigHelper.getConfigValue(this, "rest_url");
        Config.restKey = ConfigHelper.getConfigValue(this, "rest_key");

        setContentView(R.layout.custom_ui);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mSaveButton = (Button)findViewById(R.id.button);
        mText   = (EditText)findViewById(R.id.editText);
        mSaveButton.setOnClickListener(mOnButtonClickListener);



        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


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
    public void onMapReady(GoogleMap googleMap) throws SecurityException{
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);


        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
                (float) 0.01, mLocationListener);

        mMap.setOnCameraChangeListener(mCameraListener);

        new RetrieveNotesAsyncTask(mMap).execute();

        //mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        Location currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng newLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        lastLatLng = newLatLng;

        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(initialZoom));
        mMap.setOnMapLongClickListener(mOnMapLongClickListener);

        MarkerOptions markerOptions = new MarkerOptions().position(lastLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));//.rotation(90);
        userMarker = mMap.addMarker(markerOptions);
    }

    private final View.OnClickListener mOnButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            String text = mText.getText().toString();
            if(text != null && !text.isEmpty())
                new CreateNoteAsyncTask(mMap, lastLatLng, text).execute();
            mText.setText("");

            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    };

    private final OnMapLongClickListener mOnMapLongClickListener = new OnMapLongClickListener() {

        @Override
        public void onMapLongClick(LatLng latLng) {
            //new CreateNoteAsyncTask(mMap, latLng).execute();
        }
    };


    private final OnCameraChangeListener mCameraListener = new OnCameraChangeListener() {

        @Override
        public void onCameraChange(CameraPosition cameraPosition)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLatLng));
            if (cameraPosition.zoom < minZoom)
                mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom));

        }

    };


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            lastLatLng = newLatLng;

            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
            userMarker.setPosition(lastLatLng);
        }


        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        public void onProviderDisabled(String s) {

        }
    };
}
