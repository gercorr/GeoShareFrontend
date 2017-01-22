package com.logicalpanda.geoshare.activities;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.logicalpanda.geoshare.R;
import com.logicalpanda.geoshare.config.Config;
import com.logicalpanda.geoshare.config.ConfigHelper;
import com.logicalpanda.geoshare.enums.AsyncTaskType;
import com.logicalpanda.geoshare.interfaces.IHandleAsyncTaskPostExecute;
import com.logicalpanda.geoshare.other.CustomEditText;
import com.logicalpanda.geoshare.pojos.Note;
import com.logicalpanda.geoshare.rest.CreateNoteAsyncTask;
import com.logicalpanda.geoshare.rest.RetrieveNotesAsyncTask;
import com.logicalpanda.geoshare.rest.RetrieveUserAsyncTask;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, IHandleAsyncTaskPostExecute {


    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private ArrayList<Marker> mMarkers = new ArrayList<>();
    private FloatingActionButton mAddButton;
    private FloatingActionButton mSendButton;
    private CustomEditText mText;
    private ImageView mSplash;
    private ProgressBar mProgressBar;


    private LocationManager mLocationManager;

    private final float minZoom = 17.0f;
    private final float initialZoom = 19.0f;
    private LatLng lastLatLng;
    private LatLng lastSearchLatLng;
    private Marker userMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Move this somewhere else and instantiate it automatically
        Config.restUrl = ConfigHelper.getConfigValue(this, "rest_url");
        Config.restKey = ConfigHelper.getConfigValue(this, "rest_key");
        Config.distanceToRetrieve = ConfigHelper.getConfigValue(this, "distanceToRetrieve");

        setContentView(R.layout.custom_ui);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        mMapFragment.getView().setVisibility(View.GONE);


        mAddButton = (FloatingActionButton) findViewById(R.id.fab);
        mSendButton = (FloatingActionButton) findViewById(R.id.fab2);
        mText = (CustomEditText) findViewById(R.id.editText);
        mSplash = (ImageView) findViewById(R.id.splash);
        mAddButton.setOnClickListener(mOnAddButtonClickListener);
        mSendButton.setOnClickListener(mOnSendButtonClickListener);
        mText.setButtonRefs(mAddButton, mSendButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onResume(){
        super.onResume();
        logIn();
    }

    private void logIn()
    {
        mProgressBar.setVisibility(View.VISIBLE);
        try {
            new RetrieveUserAsyncTask(this).execute();
        }
        catch (Exception e)
        {
            //gotta do something here
        }
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
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);



        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
                (float) 0.01, mLocationListener);

        mMap.setOnCameraMoveListener(mCameraListener);


        mMap.setBuildingsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        Location currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LatLng newLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        lastLatLng = newLatLng;

        lastSearchLatLng = lastLatLng;
        new RetrieveNotesAsyncTask(mMap, mMarkers, lastSearchLatLng).execute();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(initialZoom));
        mMap.setMinZoomPreference(minZoom);
        MarkerOptions markerOptions = new MarkerOptions().position(lastLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));//.rotation(90);
        userMarker = mMap.addMarker(markerOptions);
    }

    private final View.OnClickListener mOnAddButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            mText.setVisibility(View.VISIBLE);
            mSendButton.setVisibility(View.VISIBLE);
            mAddButton.setVisibility(View.INVISIBLE);
            mText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    };

    private final View.OnClickListener mOnSendButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            SendInputFromTextBox();
        }
    };

    private void SendInputFromTextBox()
    {
        String text = mText.getText().toString();
        if(!text.isEmpty()) {
            new CreateNoteAsyncTask(mMap, new Note(lastLatLng, text)).execute();
        }
        mText.setText("");

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        mText.setVisibility(View.INVISIBLE);
        mSendButton.setVisibility(View.INVISIBLE);
        mAddButton.setVisibility(View.VISIBLE);
    }


    private final GoogleMap.OnCameraMoveListener mCameraListener = new GoogleMap.OnCameraMoveListener() {

        @Override
        public void onCameraMove()
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLatLng));
        }

    };


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            lastLatLng = newLatLng;

            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
            userMarker.setPosition(lastLatLng);


            if(needToRefreshMap()) {
                lastSearchLatLng = lastLatLng;
                new RetrieveNotesAsyncTask(mMap, mMarkers, lastSearchLatLng).execute();
            }

        }

        //simple box check rather than distance (faster)
        private boolean needToRefreshMap()
        {
            if(lastSearchLatLng == null || lastLatLng == null)
                return true;

            double distanceToRefresh = Double.parseDouble(Config.distanceToRetrieve)/2; //it it halved to get the distanceToRefresh. It will then refrest to the full distance

            //getting weird issues with double comparison after decimal places so just multiplied by 1000
            double lastSearchLat = lastSearchLatLng.latitude *1000;
            double lastLatMinusDist = (lastLatLng.latitude - distanceToRefresh) *1000;
            double lastLatPlusDist = (lastLatLng.latitude + distanceToRefresh) *1000;

            //if latitude is outside box
            if(lastSearchLat < lastLatMinusDist || lastSearchLat > lastLatPlusDist)
            {
                double lastSearchLong = lastSearchLatLng.longitude *1000;
                double lastLongMinusDist = (lastLatLng.longitude - distanceToRefresh) *1000;
                double lastLongPlusDist = (lastLatLng.longitude + distanceToRefresh) *1000;
                //if longitude is outside box
                if(lastSearchLong < lastLongMinusDist || lastSearchLong > lastLongPlusDist)
                {
                    return true;
                }
            }


            return false;
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

    @Override
    public void onAsyncTaskPostExecute(AsyncTaskType taskType) {
            mMapFragment.getView().setVisibility(View.VISIBLE);
            mAddButton.setVisibility(View.VISIBLE);

            mSplash.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
    }
}
