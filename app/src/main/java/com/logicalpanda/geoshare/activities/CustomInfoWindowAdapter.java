package com.logicalpanda.geoshare.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.logicalpanda.geoshare.R;

/**
 * Created by Ger on 22/01/2017.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mContentsView;
    private final Marker mUserMarker;

    CustomInfoWindowAdapter(LayoutInflater layoutInflator, Marker userMarker) {
        mContentsView = layoutInflator.inflate(R.layout.custom_info_window, null);
        mUserMarker = userMarker;
    }

    // Use default InfoWindow frame
    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    // Defines the contents of the InfoWindow
    @Override
    public View getInfoContents(Marker arg0) {

        if(arg0.equals(mUserMarker))
            return null;

        String title = arg0.getTitle();
        String content = arg0.getSnippet();

        TextView titleBox = (TextView) mContentsView.findViewById(R.id.title);
        TextView contentBox = (TextView) mContentsView.findViewById(R.id.content);

        titleBox.setText(title);
        contentBox.setText(content);

        return mContentsView;

    }
}