package com.logicalpanda.geoshare.activities;

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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.iid.InstanceID;
import com.logicalpanda.geoshare.R;
import com.logicalpanda.geoshare.config.Config;
import com.logicalpanda.geoshare.config.ConfigHelper;
import com.logicalpanda.geoshare.enums.AsyncTaskType;
import com.logicalpanda.geoshare.interfaces.IHandleAsyncTaskPostExecute;
import com.logicalpanda.geoshare.other.Globals;
import com.logicalpanda.geoshare.rest.RetrieveUserAsyncTask;

public class StartupActivity extends AppCompatActivity implements IHandleAsyncTaskPostExecute {

    private static final int PERMISSIONS_REQUEST_ID = 1;

    private EditText mNickname;
    private ProgressBar mProgressBar;

    private static final String[] PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private Boolean permissionGranted = false;

    private FloatingActionButton mStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Move this somewhere else and instantiate it automatically
        Config.restUrl = ConfigHelper.getConfigValue(this, "rest_url");
        Config.restKey = ConfigHelper.getConfigValue(this, "rest_key");
        Config.distanceToRetrieve = ConfigHelper.getConfigValue(this, "distanceToRetrieve");


        setContentView(R.layout.activity_startup);
        mStartButton = (FloatingActionButton) findViewById(R.id.fabStart);
        mStartButton.setOnClickListener(mOnStartButtonClickListener);

        mProgressBar = (ProgressBar) findViewById(R.id.startupProgressBar);
        mNickname = (EditText) findViewById(R.id.nicknameText);

        mProgressBar.setVisibility(View.VISIBLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Globals.getCurrentUser().setGoogle_instance_id(InstanceID.getInstance(this).getId());
        //TODO: retrieve User obj from backend with this id
        //TODO: user = retrieveUser();
        //TODO: Show loading while attempting to retrieve user
        logIn();
    }

    @Override
    public void onResume(){
        super.onResume();
        logIn();
    }

    private final View.OnClickListener mOnStartButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            if(attemptRequestPermission() && attemptSetNickname())
            {
                startMap();
            }
        }
    };

    private void startMap()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


    private Boolean attemptSetNickname()
    {
        if(!IsEmpty(Globals.getCurrentUser().getNickname()))
            return true;
        else {
            String text = mNickname.getText().toString();
            if (!text.isEmpty() && !text.equals("Nickname")) {
                Globals.getCurrentUser().setNickname(text);
                logIn();
                return true;
            }
        }
        return false;
    }

    private Boolean attemptRequestPermission()
    {
        if (!permissionGranted || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMS, PERMISSIONS_REQUEST_ID);
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    if(attemptSetNickname())
                    {
                        startMap();
                    }
                }
            }
        }
    }

    private static boolean IsEmpty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    private void logIn()
    {
        try {
            new RetrieveUserAsyncTask(this).execute();
        }
        catch (Exception e)
        {
            //gotta do something here
        }
    }

    @Override
    public void onAsyncTaskPostExecute(AsyncTaskType taskType) {

        if(taskType == AsyncTaskType.RetrieveUser) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (attemptRequestPermission() && !IsEmpty(Globals.getCurrentUser().getNickname())) {
                startMap();
            } else if (IsEmpty(Globals.getCurrentUser().getNickname())) {
                mNickname.setVisibility(View.VISIBLE);
                mStartButton.setVisibility(View.VISIBLE);
            }
        }
    }

    //TODO: Move to account/settings page
    //private void requestAccount()
    //{
    //    if(user.getNickname() != null && user.getNickname() != "")
    //        accountGranted = true;
    //    else {
    //        try {
    //            Intent intent = AccountPicker.newChooseAccountIntent(null, null,
    //                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);
    //            startActivityForResult(intent, REQUEST_CODE_UNIQUE_ID);
    //        } catch (ActivityNotFoundException e) {
    //            //TODO: message to user
    //        }
    //    }
    //}

    //TODO: Move to account/settings page
    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    if (requestCode == REQUEST_CODE_UNIQUE_ID && resultCode == RESULT_OK) {
    //        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
    //        user.setEmail_address(accountName);
    //        accountGranted = true;
    //    }
    //}



}
