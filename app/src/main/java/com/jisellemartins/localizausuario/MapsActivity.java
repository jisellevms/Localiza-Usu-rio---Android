package com.jisellemartins.localizausuario;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Permissoes.validarPermissoes(permissoes, this, 1);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                mMap.clear();
                LatLng localUsuario = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(localUsuario).title("Meu local"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localUsuario, 15));


                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {

                    /* REVERSE GEOCODING */

                    /*List<Address> listaEndereco = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                    if (listaEndereco != null && listaEndereco.size() > 0){
                        Address endereco = listaEndereco.get(0);
                        Log.i("END", endereco.toString());
                    }*/

                    /* GEOCODING */

                    String StrEndereco = "Vila do Mar - Barra do Ceará - Rua José Roberto Sáles - Barra do Ceará, Fortaleza - CE";
                    List<Address> listaEndereco = geocoder.getFromLocationName(StrEndereco, 1);
                    if (listaEndereco != null && listaEndereco.size() > 0){
                        Address endereco = listaEndereco.get(0);
                        Log.i("END", endereco.toString());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    10,
                    locationListener
            );
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int p : grantResults) {
            if (p == PackageManager.PERMISSION_DENIED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permissões negadas")
                        .setMessage("Para utlizar o app é necessário aceitar as permissões")
                        .setCancelable(false).setPositiveButton("Confirmar", (dialog, which) -> {
                    finish();
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            } else if (p == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            0,
                            locationListener
                    );
                }

            }
        }
    }
}