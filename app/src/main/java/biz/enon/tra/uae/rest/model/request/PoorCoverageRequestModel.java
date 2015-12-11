package biz.enon.tra.uae.rest.model.request;

import com.google.gson.annotations.Expose;

/**
 * Created by ak-buffalo on 14.08.15.
 */
public class PoorCoverageRequestModel {

    @Expose
    private int signalLevel;

    @Expose
    private Location location;

    @Expose
    private String address;

    public String getAddress() {
        return address;
    }

    public int getSignalLevel() {
        return signalLevel;
    }

    public void setSignalLevel(int signalLevel) {
        this.signalLevel = signalLevel;
    }

    public void setLocation(String latitude, String longitude) {
        this.location = new Location(latitude, longitude);
    }

    public Location getLocation() {
        return location;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private static class Location {

        @Expose
        private String latitude;

        @Expose
        private String longitude;

        public Location(String latitude, String longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "signalLevel:" + signalLevel + "," +
                "location:{" +
                "latitude:" + location.latitude + "," +
                "longitude:" + location.longitude +
                "}," +
                "address:" + address +
                "}";
    }
}
