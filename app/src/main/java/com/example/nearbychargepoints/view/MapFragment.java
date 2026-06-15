package com.example.nearbychargepoints.view;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nearbychargepoints.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {
    double lat, lon;
    String  Town, Postcode, Chargesta, Ref; //Town,Postcode,Charger,ConnectType;
    public MapFragment(double x, double y, String z, String a, String b, String c) {
        lat=x;
        lon=y;
        Town=z;
        Postcode=a;
        Chargesta=b;
        Ref=c;

    }
    public MapFragment(Context applicationContext) {
    }

    public MapFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Initialize view
        View view=inflater.inflate(R.layout.fragment_map, container, false);
        String title="Latitude "+lat+"\n"+"Longitude "+ lon+"\n";
// Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);
// Async map
//
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(new MyOnMapReadyCallback());
// Return view
        return view;
    }
    private class MyOnMapReadyCallback implements OnMapReadyCallback {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
// When map is loaded
            LatLng bradford = new LatLng(lat,lon);
            googleMap.addMarker(new MarkerOptions().position(bradford).title(" Location Information ").snippet("Town Name" +Town));
            googleMap.addMarker(new MarkerOptions().position(bradford).title("Bradford City is Here"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(bradford));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(bradford.latitude,
                    bradford.longitude), 12f));
        }
    }
}
