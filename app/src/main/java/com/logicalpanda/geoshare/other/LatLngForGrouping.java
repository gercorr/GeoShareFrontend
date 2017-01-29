package com.logicalpanda.geoshare.other;

public class LatLngForGrouping {

    private double roundedLatitude;
    private double roundedLongitude;

    public LatLngForGrouping(double latitude, double longitude) {
        roundedLatitude = roundToFourDec(latitude);
        roundedLongitude = roundToFourDec(longitude);
    }

    public double getLatitude() {
        return roundedLatitude;
    }

    public double getLongitude() {
        return roundedLongitude;
    }


    private static double roundToFourDec(double value) {
        double roundOff = Math.floor(value * 10000) / 10000;
        return roundOff;
    }

    @Override
    public int hashCode() {
        return (int)(roundedLatitude + roundedLatitude);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LatLngForGrouping))
            return false;
        if (obj == this)
            return true;

        LatLngForGrouping toCompare = (LatLngForGrouping) obj;

        return toCompare.roundedLatitude == this.roundedLatitude && toCompare.roundedLongitude == this.roundedLongitude;
    }
}
