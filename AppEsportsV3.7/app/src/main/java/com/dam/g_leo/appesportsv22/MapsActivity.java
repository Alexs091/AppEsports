package com.dam.g_leo.appesportsv22;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Jugador> listaAmigos;
    static Integer[] mThumbIds = {
            R.drawable.miniprofileicon10,
            R.drawable.miniprofileicon13,
            R.drawable.miniprofileicon23,
            R.drawable.miniprofileicon24old,
            R.drawable.miniprofileicon29old,
            R.drawable.miniavataricon
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle weeklyHumbleBundle = this.getIntent().getExtras();
        listaAmigos = weeklyHumbleBundle.getParcelableArrayList("amigos");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        for (Jugador amigo:listaAmigos) {
//            Toast.makeText(this, "código avatar: " + amigo.getAvatar(), Toast.LENGTH_SHORT).show();
            mMap.addMarker(new MarkerOptions().position(new LatLng(amigo.getLatitud(),
                    amigo.getLongitud())).title(amigo.getIDOnline()).icon(BitmapDescriptorFactory.fromResource(mThumbIds[amigo.getAvatar()])));
        }
        LatLng spain = new LatLng(40, -4);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(spain));
    }
}
