package biz.enon.tra.uae.rest.robo_requests;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.util.List;

import biz.enon.tra.uae.rest.TRAServicesAPI;

/**
 * Created by ak-buffalo on 17.08.15.
 */
public class GeoLocationRequest extends BaseRequest<Address, TRAServicesAPI>  {
    private Geocoder mGeoCoder;
    private Location mLocation;

    public GeoLocationRequest(final Geocoder _geoCoder, final Location _location) {
        super(Address.class, TRAServicesAPI.class);
        mGeoCoder = _geoCoder;
        mLocation = _location;
    }

    @Override
    public final Address loadDataFromNetwork() throws Exception {
        List<Address> addresses  = mGeoCoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
        return (addresses != null && addresses.size() != 0) ? addresses.get(0) : null;
    }
}
