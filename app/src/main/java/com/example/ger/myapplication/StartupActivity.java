package com.example.ger.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class StartupActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ID =1337;
    private static final String[] PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private FloatingActionButton mStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(requestPermission())
        {
            startMap();
        }
        else
        {
            setContentView(R.layout.activity_startup);
            mStartButton = (FloatingActionButton) findViewById(R.id.fabStart);
            mStartButton.setOnClickListener(mOnStartButtonClickListener);
        }
    }

    private Boolean requestPermission()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //requestPermissions(PERMS, PERMISSIONS_REQUEST_ID);
            ActivityCompat.requestPermissions(this, PERMS, PERMISSIONS_REQUEST_ID);
            return false;
        }
        else {
            return true;
        }
    }


    private final View.OnClickListener mOnStartButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if(requestPermission())
            {
                startMap();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMap();
                }
            }
        }
    }

    private void startMap()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

}
