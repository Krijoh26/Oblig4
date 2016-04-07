package com.example.kristinesjnst.oblig4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap googleMap;
    private LocationManager locationManager;
    private ArrayList<LatLng> markerPoints = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private int count = 0;
    private MarkerOptions options = new MarkerOptions();
    private String zoom;
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_NORMAL, GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_SATELLITE, GoogleMap.MAP_TYPE_TERRAIN, GoogleMap.MAP_TYPE_NONE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = supportMapFragment.getMap();
        googleMap.setOnMapClickListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        sharedPreferences = getSharedPreferences("location", 0);
        count = sharedPreferences.getInt("locationCount", 0);
        zoom = sharedPreferences.getString("zoom", "0");

        onMapReady(googleMap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int MAP_TYPE_NORMAL = 0;
        int MAP_TYPE_HYBRID = 1;
        int MAP_TYPE_SATELLITE = 2;
        int MAP_TYPE_TERRAIN = 3;
        int MAP_TYPE_NONE = 4;

        switch (item.getItemId()) {
            case R.id.normal:
                mapTypeSelected(MAP_TYPE_NORMAL);
                isChecked(item);
                return true;
            case R.id.hybrid:
                mapTypeSelected(MAP_TYPE_HYBRID);
                isChecked(item);
                return true;
            case R.id.satellite:
                mapTypeSelected(MAP_TYPE_SATELLITE);
                isChecked(item);
                return true;
            case R.id.terrain:
                mapTypeSelected(MAP_TYPE_TERRAIN);
                isChecked(item);
                return true;
            case R.id.none:
                mapTypeSelected(MAP_TYPE_NONE);
                isChecked(item);
                return true;
            case R.id.delete:
                deleteDialog();
                return true;
            case R.id.quit:
                quitDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean mapTypeSelected(int itemPosition) {
        googleMap.setMapType(MAP_TYPES[itemPosition]);
        return true;
    }

    private void isChecked(MenuItem item) {
        if(item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        loadPreferences();
    }

    private void loadPreferences() {
        if(count!=0) {
            String lat = "";
            String lng = "";
            for(int i=0; i < count; i++) {
                lat = sharedPreferences.getString("lat"+i,"0");
                lng = sharedPreferences.getString("lng"+i,"0");
                drawLines(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
                options.position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
                options.icon(BitmapDescriptorFactory.defaultMarker());
                googleMap.addMarker(options);
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));
        }
    }

    private void drawLines(LatLng latLng) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(5);
        markerPoints.add(latLng);
        polylineOptions.addAll(markerPoints);
        googleMap.addPolyline(polylineOptions);
        polylines.add(googleMap.addPolyline(polylineOptions));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        count++;
        drawLines(latLng);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lat" + Integer.toString((count - 1)), Double.toString(latLng.latitude));
        editor.putString("lng" + Integer.toString((count - 1)), Double.toString(latLng.longitude));
        editor.putInt("locationCount", count);
        editor.putString("zoom", Float.toString(googleMap.getCameraPosition().zoom));
        editor.apply();
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.defaultMarker());
        googleMap.addMarker(options);
    }

    private void deleteDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Delete");
        alertDialog.setMessage("Are you sure you want to permanently delete all data? ");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        googleMap.clear();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        count=0;
                        for(Polyline polyline : polylines) {
                            polyline.remove();
                        }
                        polylines.clear();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void quitDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Quit");
        alertDialog.setMessage("Are you sure you want to quit? ");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Position");
            alertDialog.setMessage("You need to turn on GPS");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }
}