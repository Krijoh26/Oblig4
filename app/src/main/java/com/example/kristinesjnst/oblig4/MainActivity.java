package com.example.kristinesjnst.oblig4;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {


//    implements OnMapReadyCallback, GoogleMap.OnMapClickListener

    private GoogleMap mMap;
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID, GoogleMap.MAP_TYPE_SATELLITE,
             GoogleMap.MAP_TYPE_TERRAIN, GoogleMap.MAP_TYPE_NONE};
    private final int MAP_TYPE_NORMAL = 0;
    private final int MAP_TYPE_HYBRID = 1;
    private final int MAP_TYPE_SATELLITE = 2;
    private final int MAP_TYPE_TERRAIN = 3;
    private final int MAP_TYPE_NONE = 4;

    //    private int currMapTypeIndex = 0;
//    private GoogleApiClient mGoogleApiClient;
//    private Location mCurrentLocation;
//    private GoogleMap map;
    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543, -73.998585);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
    private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = supportMapFragment.getMap();
        mMap.setOnMapClickListener(this);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        String serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(serviceString);

        onMapReady(mMap);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:
                onNavigationItemSelected(MAP_TYPE_NORMAL);
                isChecked(item);
                return true;
            case R.id.hybrid:
                onNavigationItemSelected(MAP_TYPE_HYBRID);
                isChecked(item);
                return true;
            case R.id.satellite:
                onNavigationItemSelected(MAP_TYPE_SATELLITE);
                isChecked(item);
                return true;
            case R.id.terrain:
                onNavigationItemSelected(MAP_TYPE_TERRAIN);
                isChecked(item);
                return true;
            case R.id.none:
                onNavigationItemSelected(MAP_TYPE_NONE);
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

    private void isChecked(MenuItem item) {
        if(item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MarkerOptions options = new MarkerOptions();
        options.position(LOWER_MANHATTAN);
        options.position(BROOKLYN_BRIDGE);
        options.position(WALL_STREET);
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BROOKLYN_BRIDGE, 13));
        googleMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE).title("Brooklyn bridge"));
        googleMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN).title("Lower manhattan"));
        googleMap.addMarker(new MarkerOptions().position(WALL_STREET).title("Wall street"));
    }


    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        markerOptions.title(getAddressFromLatLng(latLng));

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(markerOptions);
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            return addresses.get(0).getAddressLine(0);
        }
        return null;
    }

    //    @Override
    public boolean onNavigationItemSelected(int itemPosition) {
        mMap.setMapType(MAP_TYPES[itemPosition]);

        return (true);

    }

    public void deleteDialog(){
//TODO: delete data from file.
    }

    public void quitDialog(){
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
            //TODO: lag en dialogboks som spør om man ønsker å slå på gps.
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }
}

//    private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
//            -73.998585);
//    private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
//    private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);
//
//    GoogleMap googleMap;
//    final String TAG = "PathGoogleMapActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        googleMap = fm.getMap();
//        UiSettings uiSettings = mMap.getUiSettings();
//        uiSettings.setZoomControlsEnabled(true);
//
//        MarkerOptions options = new MarkerOptions();
//        options.position(LOWER_MANHATTAN);
//        options.position(BROOKLYN_BRIDGE);
//        options.position(WALL_STREET);
//        googleMap.addMarker(options);
//        String url = getMapsApiDirectionsUrl();
//        ReadTask downloadTask = new ReadTask();
//        downloadTask.execute(url);
//
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BROOKLYN_BRIDGE,
//                13));
//        addMarkers();
//
//    }
//
//    private String getMapsApiDirectionsUrl() {
//        String waypoints = "waypoints=optimize:true|"
//                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
//                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
//                + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
//                + WALL_STREET.longitude;
//
//        String sensor = "sensor=false";
//        String params = waypoints + "&" + sensor;
//        String output = "json";
//        String url = "https://maps.googleapis.com/maps/api/directions/"
//                + output + "?" + params;
//        return url;
//    }
//
//    private void addMarkers() {
//        if (googleMap != null) {
//            googleMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
//                    .title("First Point"));
//            googleMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
//                    .title("Second Point"));
//            googleMap.addMarker(new MarkerOptions().position(WALL_STREET)
//                    .title("Third Point"));
//        }
//    }
//
//    private class ReadTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... url) {
//            String data = "";
//            try {
//                HttpConnection http = new HttpConnection();
//                data = http.readUrl(url[0]);
//            } catch (Exception e) {
//                Log.d("Background Task", e.toString());
//            }
//            return data;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            new ParserTask().execute(result);
//        }
//    }
//
//    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
//
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(
//                String... jsonData) {
//
//            JSONObject jObject;
//            List<List<HashMap<String, String>>> routes = null;
//
//            try {
//                jObject = new JSONObject(jsonData[0]);
//                PathJSONParser parser = new PathJSONParser();
//                routes = parser.parse(jObject);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
//            ArrayList<LatLng> points = null;
//            PolylineOptions polyLineOptions = null;
//
//            // traversing through routes
//            for (int i = 0; i < routes.size(); i++) {
//                points = new ArrayList<LatLng>();
//                polyLineOptions = new PolylineOptions();
//                List<HashMap<String, String>> path = routes.get(i);
//
//                for (int j = 0; j < path.size(); j++) {
//                    HashMap<String, String> point = path.get(j);
//
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lng = Double.parseDouble(point.get("lng"));
//                    LatLng position = new LatLng(lat, lng);
//
//                    points.add(position);
//                }
//
//                polyLineOptions.addAll(points);
//                polyLineOptions.width(2);
//                polyLineOptions.color(Color.BLUE);
//            }
//
//            googleMap.addPolyline(polyLineOptions);
//        }
//    }