package com.logicalpanda.geoshare.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.logicalpanda.geoshare.R;
import com.logicalpanda.geoshare.other.Globals;
import com.logicalpanda.geoshare.pojos.Note;
import com.logicalpanda.geoshare.pojos.User;

import java.util.ArrayList;
import java.util.Date;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mContentsView;

    CustomInfoWindowAdapter(LayoutInflater layoutInflator) {
        mContentsView = layoutInflator.inflate(R.layout.custom_info_window, null);
    }

    // Use default InfoWindow frame
    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    // Defines the contents of the InfoWindow
    @Override
    public View getInfoContents(Marker arg0) {


        ArrayList<Note> notes = (ArrayList<Note>) arg0.getTag();
        String title = Integer.toString(notes.size()) + " Notes";
        StringBuilder content = new StringBuilder();
        int count = 0;
        for (Note note : notes) {
            count ++;
            if(count > 5)
            {
                content.append("...");
                break;
            }
            content.append(timeDifference(note.getCreatedDate()) + " ");
            User currentUser = Globals.getCurrentUser();
            if(note.getUser().getId() == currentUser.getId())
            {
                content.append("Me");
            }
            else
            {
                content.append(note.getUser().getNickname());
            }
            if(count <= 5 && count != notes.size())
            {
                content.append("\n");
            }

        }

        TextView titleBox = (TextView) mContentsView.findViewById(R.id.title);
        TextView contentBox = (TextView) mContentsView.findViewById(R.id.content);

        titleBox.setText(title);
        contentBox.setText(content);

        return mContentsView;

    }

    private static String timeDifference(Date createdDate)
    {
        Date now = new Date();
        return friendlyTimeDiff(now.getTime() - createdDate.getTime());
    }

    private static String friendlyTimeDiff(long timeDifferenceMilliseconds) {
        long diffSeconds = timeDifferenceMilliseconds / 1000;
        long diffMinutes = timeDifferenceMilliseconds / (60 * 1000);
        long diffHours = timeDifferenceMilliseconds / (60 * 60 * 1000);
        long diffDays = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24);
        long diffWeeks = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 7);
        long diffMonths = (long) (timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 30.41666666));
        long diffYears = timeDifferenceMilliseconds / ((long)60 * 60 * 1000 * 24 * 365);

        if (diffMinutes < 1) {
            return diffSeconds + " seconds ago";
        } else if (diffHours < 1) {
            return diffMinutes + " minutes ago";
        } else if (diffDays < 1) {
            return diffHours + " hours ago";
        } else if (diffWeeks < 1) {
            return diffDays + " days ago";
        } else if (diffMonths < 1) {
            return diffWeeks + " weeks ago";
        } else if (diffYears < 1) {
            return diffMonths + " months ago";
        } else {
            return diffYears + " years ago";
        }
    }
}