package com.returno.dynasty.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class GeocoderHelper extends AsyncTask<Location,Integer,String> {
    private WeakReference <Context> context;
    public GeocoderHelper(Context context){
       this.context=new WeakReference<>(context);
    }
    @Override
    protected String doInBackground(Location... locations) {
        Location location=locations[0];
        Geocoder geocoder=new Geocoder(context.get(), Locale.getDefault());
        String result;
        try {
            List<Address> addressList=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if (addressList!=null && addressList.size()>0){
                Address address=addressList.get(0);
                result=address.getLocality()+" "+address.getAddressLine(0)+" "+address.getSubLocality();
                Timber.e(result);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Unknown location";
        }
        return "Unknown location";
    }

    public static class GeocodeLatLang extends  AsyncTask<LatLng,Integer,String> {
        private WeakReference<Context> context;

        public GeocodeLatLang(Context context) {
            this.context = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(LatLng... locations) {
            LatLng location = locations[0];
            Geocoder geocoder = new Geocoder(context.get(), Locale.getDefault());
            String result;
            try {
                List<Address> addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1);

                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    result = address.getLocality() + " " + address.getAddressLine(0) + " " + address.getSubLocality();
                    Timber.e(result);
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Unknown location";
            }
            return "Unknown location";
        }
    }
}
