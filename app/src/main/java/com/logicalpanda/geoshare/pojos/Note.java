package com.logicalpanda.geoshare.pojos;

import com.google.android.gms.maps.model.LatLng;
import com.logicalpanda.geoshare.other.Globals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Note implements Comparator<Note> {

    public Note()
    {}

    public Note(LatLng latLng, String text)
    {
        setLatitude(latLng.latitude);
        setLongitude(latLng.longitude);
        setText(text);
        setUser(Globals.getCurrentUser());
    }

    private int id;

    private String text;

    private double latitude;

    private double longitude;

    private User user;

    private Date createdDate;

    public int getId( ) {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText( ) {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }


    public String timeDifference()
    {
        Date now = new Date();
        String timeReadable = friendlyTimeDiff(now.getTime() - createdDate.getTime());
        timeReadable = padRight(timeReadable, 12);
        return timeReadable;
    }

    private String friendlyTimeDiff(long timeDifferenceMilliseconds) {
        long diffSeconds = timeDifferenceMilliseconds / 1000;
        long diffMinutes = timeDifferenceMilliseconds / (60 * 1000);
        long diffHours = timeDifferenceMilliseconds / (60 * 60 * 1000);
        long diffDays = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24);
        long diffWeeks = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 7);

        String timeDescription = "";
        if (diffMinutes < 1) {
            if(diffSeconds == 1) {
                return "1 second";
            }
            return diffSeconds + " seconds";
        } else if (diffHours < 1) {
            if(diffMinutes == 1) {
                return "1 minute";
            }
            return diffMinutes + " minutes";
        } else if (diffDays < 1) {
            if(diffHours == 1) {
                return "1 hour";
            }
            return diffHours + " hours";
        } else if (diffWeeks < 1) {
            if(diffDays == 1) {
                return "1 day";
            }
            return diffDays + " days";
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(createdDate);
        }
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    @Override
    public int compare(Note note1, Note note2)
    {
        return  note2.getCreatedDate().compareTo(note1.getCreatedDate());
    }
}
