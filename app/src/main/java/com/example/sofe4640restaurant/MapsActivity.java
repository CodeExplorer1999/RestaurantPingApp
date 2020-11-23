package com.example.sofe4640restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient APIClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    private static final int Request_User_Location_Code = 99;
    private double latitude, longitude;
    private int ProximityRadius = 10000;

    LatLng user_latLng;//User Location Var

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onClick(View v){

        String restaurant = "restaurant";
        Object transferData[] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();

        switch (v.getId()){
            case R.id.search_String:
                EditText searchField = (EditText) findViewById(R.id.search_bar);
                String address = searchField.getText().toString();

                List<Address> addressList = null;
                MarkerOptions userMarkerOptions = new MarkerOptions();

                if(!TextUtils.isEmpty(address)){
                    Geocoder geocoder = new Geocoder(this);

                    try {
                        addressList = geocoder.getFromLocationName(address, 6);

                        if(addressList != null){
                            for (int i =0; i<addressList.size(); i++){
                                Address userAddress = addressList.get(i);
                                LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());
//                                Toast.makeText(MapsActivity.this, "Lat: " + user_latLng.latitude + " Long: " + user_latLng.longitude, Toast.LENGTH_LONG).show();
                                //Call function here that is gonna take a given address and ping the user if they are nearby the address
                                if(nearUser(user_latLng, user_latLng)) {
                                    Toast.makeText(MapsActivity.this, "You're Near the restaurant!", Toast.LENGTH_LONG).show();

                                    userMarkerOptions.position(latLng);
                                    userMarkerOptions.title(address);
                                    userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                                    mMap.addMarker(userMarkerOptions);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                                }
                            }

                        }else{
                            Toast.makeText(this, "Location not found...", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(this, "Please Enter a Location...", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.FindCloseby:
                mMap.clear();
                String url = getUrl(latitude, longitude, restaurant);
                transferData[0] = mMap;
                transferData[1] = url;

                getNearbyPlaces.execute(transferData);
                Toast.makeText(this, "Searching for Nearby Restaurants...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing Nearby Restaurants...", Toast.LENGTH_SHORT).show();

                //TODO: Notify the User Here
                createNotificationChannel();    //Calling function that creates the notification channel
                /* Create notification to send to the user */
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "near_food")
                        .setSmallIcon(R.drawable.ic_baseline_fastfood_24)   //Get's custom made logo. Color very nice
                        .setContentTitle("I'd Eat There")
                        .setContentText("Hey there are Restaurants Nearby!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this); //Setting up notification manager
                notificationManagerCompat.notify(100, builder.build() );    //Notifying User
                break;
        }

    }

    /**
     * Function that creates the notification channel
     */
    private void createNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "restaurant_channel";
            String desc = "Channel for restaurant notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("near_food", name, importance);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * This function is to calculate the difference in kilometers between the user and the given restaurant
     * @param user_loc  The lattitude and longitude data of the user
     * @param food_loc  The lattitude and longitude data of the restaurant
     * @return True, if the restaurant is within 5 km and false if it is farther away
     */
    private Boolean nearUser(LatLng user_loc, LatLng food_loc) {

        //TODO: Calculate distance between user and restaurant locations
        /*Getting distance between the two lattitudes and longitudes using the Haversine formula*/
        //Saw this on https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
        //Explanation at https://en.wikipedia.org/wiki/Haversine_formula
        int R = 6371;   //Radius of the Earth (km)
        double lat_diff = deg2Rad(user_loc.latitude - food_loc.latitude);
        double lon_diff = deg2Rad(user_loc.longitude - food_loc.longitude);
        //Bunch of math to get the value of the haversine function
        double h = Math.sin(lat_diff / 2) * Math.sin(lat_diff / 2) +
                Math.cos(deg2Rad(food_loc.latitude)) * Math.cos(deg2Rad(user_loc.latitude)) *
                        Math.sin(lon_diff/2) * Math.sin(lon_diff/2);
        double dist = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));   //Getting the distance from the haversine function
        //Note: The dist var above actually gets a fraction of the actual distance over the earth's radius so to get the
        // actual distance we multiply it with earth's radius
        double dist_km = dist * R;  //Gets the distance in km
        /*Returning true if the distance is less than 5 km, otherwise returning false*/
        dist_km = Math.abs(dist_km);
        if(dist_km <= 5) {
            return true;
        }
        else {
            return false;
        }
    }

    private double deg2Rad(double deg) {
        return deg * (Math.PI/180);
    }

    private String getUrl(double latitude, double longitude, String restaurant){
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&type=" + restaurant);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyC0i6HrkgawswgC1nM9nue6siR9sDFmlsY");

        Log.d("GoogleMapsActivity", "url = " + googleURL.toString());

        return googleURL.toString();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            buildAPIclient();
            mMap.setMyLocationEnabled(true);

        }

    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;

        }
        else{
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Request_User_Location_Code:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(APIClient == null)
                        {
                            buildAPIclient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else{
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected synchronized void buildAPIclient(){
        APIClient = new GoogleApiClient.Builder( this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        APIClient.connect();
    }
    /*TODO: Funtion that gets User location */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLocation = location;

        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }

        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        user_latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(user_latLng); //latLng is the location of the User
        markerOptions.title("Users Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        currentLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(user_latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(8));

        if(APIClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(APIClient, this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(APIClient,locationRequest,this);
        }




    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}