package com.example.triptrooperapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Activities screen.
 */
public class PlacesActivity extends AppCompatActivity implements LocationListener {

    private LinearLayout placesBoxContainer;

    private TextView textHeader;
    private Toolbar toolbar;
    private int checkLocationUpdateTime = 1000;
    private LocationManager locationManager;

    private String longitude;
    private String latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        Intent intentFrom= getIntent();
        String context = intentFrom.getStringExtra("context");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        placesBoxContainer = findViewById(R.id.list_layout_container);

        textHeader = findViewById(R.id.group_header);

        textHeader.setText(R.string.place_list_header);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, checkLocationUpdateTime, 0, this);

            // Get last known location immediately
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                longitude = String.valueOf(lastKnownLocation.getLongitude());
                latitude = String.valueOf(lastKnownLocation.getLatitude());
                Log.d("HELL", "Latitude: " + lastKnownLocation.getLatitude());
                Log.d("HELL", "Longitude: " + lastKnownLocation.getLongitude());
            } else {
                Log.d("HELL", "Last known location is null");
            }
        }

        if(context.equals("nearby")){
            retrievePlaces();
        }
        else {
            retrievePlacesByDestination();
        }



        Log.d("PLACES", "Starting up places activity");
    }

    private void retrievePlaces() {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(PlacesActivity.this);
        String url = "places/currLocation?latitude=" + latitude + "&longitude=" + longitude;
        BackendServiceClass backendService = new BackendServiceClass(url, "authorization", account.getIdToken());
        Request request = backendService.getGetRequestWithHeaderOnly();

        new Thread(() -> {
            Response response = backendService.getResponseFromRequest(request);
            if (response.isSuccessful()){
                try {
                    String responseBody = response.body().string();
                    Log.d("TAG",responseBody);
                    // displayName, shortFormattedAddress, rating
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray places = jsonResponse.getJSONArray("places");
                    for (int i=0; i<places.length(); i++){
                        JSONObject place = places.getJSONObject(i);
                        runOnUiThread(()-> {
                            ListBoxComponentView listBox = new ListBoxComponentView(PlacesActivity.this);
                            try {
                                String placeName = place.getString("displayName");
                                String address = place.getString("shortFormattedAddress");
                                String rating = place.optString("rating", "--");
                                listBox.setMainTitleText(placeName);
                                listBox.setSubTitleText(address);
                                listBox.setSideTitleText("     Rating: "+ rating+ "/5");

                                placesBoxContainer.addView(listBox);


                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
            else {
                try {
                    Log.d("TAG", response.body().string());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void retrievePlacesByDestination(){
        Intent intentFrom = getIntent();
        String destination = intentFrom.getStringExtra("destination");

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(PlacesActivity.this);
        String url = "places/destination?textQuery=" +destination + "&category=";
        BackendServiceClass backendService = new BackendServiceClass(url, "authorization", account.getIdToken());
        Request request = backendService.getGetRequestWithHeaderOnly();

        new Thread(() -> {
            Response response = backendService.getResponseFromRequest(request);
            if (response.isSuccessful()){
                try {
                    String responseBody = response.body().string();
                    Log.d("TAG",responseBody);
                    // displayName, shortFormattedAddress, rating
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray places = jsonResponse.getJSONArray("places");
                    for (int i=0; i<places.length(); i++){
                        JSONObject place = places.getJSONObject(i);
                        runOnUiThread(()-> {
                            ListBoxComponentView listBox = new ListBoxComponentView(PlacesActivity.this);
                            try {
                                String placeName = place.getString("displayName");
                                String address = place.getString("shortFormattedAddress");
                                String rating = place.optString("rating", "--");
                                listBox.setMainTitleText(placeName);
                                listBox.setSubTitleText(address);
                                listBox.setSideTitleText("     Rating: "+ rating+ "/5");

                                placesBoxContainer.addView(listBox);

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
            else {
                try {
                    Log.d("TAG", response.body().string());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, ActivitiesActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("HELL", String.valueOf(location.getLatitude()));
        Log.d("HELL", String.valueOf(location.getLongitude()));
    }

}
