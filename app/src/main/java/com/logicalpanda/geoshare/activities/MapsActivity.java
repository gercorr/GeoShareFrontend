package com.logicalpanda.geoshare.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.logicalpanda.geoshare.R;
import com.logicalpanda.geoshare.config.Config;
import com.logicalpanda.geoshare.enums.AsyncTaskType;
import com.logicalpanda.geoshare.interfaces.IHandleAsyncTaskPostExecute;
import com.logicalpanda.geoshare.other.CustomEditText;
import com.logicalpanda.geoshare.other.Globals;
import com.logicalpanda.geoshare.other.LatLngForGrouping;
import com.logicalpanda.geoshare.pojos.Note;
import com.logicalpanda.geoshare.rest.CreateNoteAsyncTask;
import com.logicalpanda.geoshare.rest.RetrieveNotesAsyncTask;
import com.logicalpanda.geoshare.rest.RetrieveUserAsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, IHandleAsyncTaskPostExecute, GoogleMap.OnInfoWindowClickListener {


    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private final HashMap<LatLngForGrouping, ArrayList<Note>> mAllCurrentNotes = new HashMap<>();
    private FloatingActionButton mAddButton;
    private FloatingActionButton mSendButton;
    private CustomEditText mText;
    private ImageView mSplash;
    private ProgressBar mProgressBar;


    private LocationManager mLocationManager;

    private final float minZoom = 17.0f;
    private final float initialZoom = 19.0f;
    private final int tilt = 50;
    private float currentZoom = 0;
    private LatLng lastLatLng;
    private LatLng lastSearchLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.SetupConfig(this);

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
    public void onMapReady(GoogleMap googleMap) throws SecurityException{
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraMoveListener(mCameraListener);
        mMap.setOnMarkerClickListener(mOnMarkerClickListener);
        mMap.setBuildingsEnabled(true);
        mMap.setMinZoomPreference(minZoom);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
                (float) 0.01, mLocationListener);
        Location currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        setCameraPosition(currentLocation, initialZoom);

        lastSearchLatLng = lastLatLng;
        retrieveNotes();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getLayoutInflater()));
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onResume(){
        if(Globals.getCurrentUser() == null || Globals.getCurrentUser().getGoogle_instance_id() == null || Globals.getCurrentUser().getGoogle_instance_id() == "")
        {
            //user not setup (or lost) start from the beginning
            Intent intent = new Intent(this, StartupActivity.class);
            startActivity(intent);
        }
        super.onResume();
        Globals.setCurrentFilteredNickname(null);
        logIn();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Globals.setCurrentFilteredNickname(query);
            retrieveNotes();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnCloseListener(mOnSearchCloseListener);

        //YUCK. XML tint value doesnt work with drawable TODO: Change drawables to white?
        MenuItem item = menu.findItem(R.id.action_search);
        Drawable icon = item.getIcon();
        icon.setColorFilter(getResources().getColor(R.color.colorText), PorterDuff.Mode.SRC_IN);
        item = menu.findItem(R.id.action_refresh);
        icon = item.getIcon();
        icon.setColorFilter(getResources().getColor(R.color.colorText), PorterDuff.Mode.SRC_IN);


        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                return true;
            case R.id.action_refresh:
                retrieveNotes();
                return true;
            case R.id.action_list:
                Intent intent = new Intent(this, NoteListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Globals.setCurrentMarker(marker);
        Intent intent = new Intent(this, NoteListActivity.class);
        startActivity(intent);
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

    private void retrieveNotes()
    {
        new RetrieveNotesAsyncTask(mMap, mAllCurrentNotes, lastSearchLatLng, Globals.getCurrentFilteredNickname()).execute();
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
            new CreateNoteAsyncTask(mMap, mAllCurrentNotes, new Note(lastLatLng, text)).execute();
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
            //LatLngBounds bounds = new LatLngBounds(lastLatLng, lastLatLng);
            //mMap.setLatLngBoundsForCameraTarget(bounds);
        }

    };

    private final GoogleMap.OnMarkerClickListener mOnMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            //marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            return false;
        }
    };

    private final SearchView.OnCloseListener mOnSearchCloseListener = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            Globals.setCurrentFilteredNickname(null);
            retrieveNotes();
            return false;
        }
    };


    private void setCameraPosition(Location location, float zoom)
    {
        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        lastLatLng = newLatLng;

        float bearing = 0;
        if(location.hasBearing())
            bearing = location.getBearing();

        currentZoom = zoom;
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(lastLatLng)             // Sets the center of the map to current location
                //.bearing(bearing) // Sets the orientation of the camera to east
                .zoom(currentZoom)
                .tilt(tilt)                   // Sets the tilt of the camera to 0 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        LatLngBounds bounds = new LatLngBounds(lastLatLng, lastLatLng);
        mMap.setLatLngBoundsForCameraTarget(bounds);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            setCameraPosition(location, mMap.getCameraPosition().zoom);

            if(needToRefreshMap()) {
                lastSearchLatLng = lastLatLng;
                retrieveNotes();
            }

        }

        //simple box check rather than distance (faster)
        private boolean needToRefreshMap()
        {
            if(lastSearchLatLng == null || lastLatLng == null)
                return true;

            double distanceToRefresh = Double.parseDouble(Config.distanceToRetrieve)/4; //it it halved to get the distanceToRefresh. It will then refrest to the full distance

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
