package com.namrata.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV,temperatureTV,conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV,iconIV,searcIV;
    private ArrayList<weatherRVModal> weatherRVModalArrayList;
    private weatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.TVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRvWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.backImage);
        iconIV = findViewById(R.id.idIVIcon);
        searcIV = findViewById(R.id.idIVSearch);
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new weatherRVAdapter(this,weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);
       // locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
     /*   if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((MainActivity.this),new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,4000,5, (LocationListener) getApplicationContext());
        Location location;

        Log.d("Network", "Network");
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                cityName = getCityName(location.getLongitude(), location.getLatitude());
                getWeatherInfo(cityName);

            }
        }*/
       locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      //  locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},101);

        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location != null) {
            Toast.makeText(this, "location is not null", Toast.LENGTH_SHORT).show();
            cityName = getCityName(location.getLongitude(), location.getLatitude());
            Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show();
            getWeatherInfo(cityName);
            Toast.makeText(this, "weather info called", Toast.LENGTH_SHORT).show();
        }
        if(location == null){
            Toast.makeText(MainActivity.this, "location is null", Toast.LENGTH_SHORT).show();
            cityName = "London";
            getWeatherInfo(cityName);
        }

      //  cityName = getCityName(location.getLongitude(), location.getLatitude());
       // getWeatherInfo(cityName);*/


        searcIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"city is empty",Toast.LENGTH_SHORT).show();
                }else{
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted...", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    private String getCityName(double longitude, double lattitude){
        String cityName = cityEdt.getText().toString();
        Toast.makeText(this, "cityName is called", Toast.LENGTH_SHORT).show();
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> address = gcd.getFromLocation(lattitude,longitude,10);
            for(Address adr :address){
                if(adr != null){
                    String city = adr.getLocality();
                    if(city != null && !city.equals("")){
                        cityName = city;
                     //   Toast.makeText(this, "cityName is called", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d("TAG","CITY NOT FOUND");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }
    private void getWeatherInfo(String cityName){

       // String url = "http://api.weatherapi.com/v1/forecast.json?key=f689f248ee3d4bdf99141328222506&q=" +" cityName " +"&days=1&aqi=yes&alerts=yes";
     //   "http://api.weatherapi.com/v1/forecast.json?key=f689f248ee3d4bdf99141328222506&q=cityName=London&days=1&aqi=yes&alerts=yes";
        String url = "http://api.weatherapi.com/v1/forecast.json?key=f689f248ee3d4bdf99141328222506&q="+"cityName"+"&days=1&aqi=yes&alerts=yes";
        cityNameTV.setText(cityName);
        Toast.makeText(this, "get info with cityName is called"+cityName, Toast.LENGTH_SHORT).show();
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            Toast.makeText(MainActivity.this, "volley using", Toast.LENGTH_SHORT).show();
         //   loadingPB.setVisibility(View.);
          //  homeRL.setVisibility(View.VISIBLE);
            weatherRVModalArrayList.clear();
            try {
                String temperature = response.getJSONObject("current").getString("temp_c");
                temperatureTV.setText(temperature+"c");
                int isDay = response.getJSONObject("current").getInt("is_day");
                String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");

                Picasso.with(MainActivity.this).load("http:".concat(conditionIcon)).into(iconIV);
                conditionTV.setText(condition);

                if(isDay == 1){
                    Picasso.with(MainActivity.this).load("https://images.unsplash.com/photo-1622396481328-9b1b78cdd9fd?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80").into(backIV);
                    Toast.makeText(MainActivity.this, "day time", Toast.LENGTH_SHORT).show();
                }else{
                    Picasso.with(MainActivity.this).load("https://images.unsplash.com/photo-1590418606746-018840f9cd0f?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1887&q=80").into(backIV);
                    Toast.makeText(MainActivity.this, "night time", Toast.LENGTH_SHORT).show();
                }

                JSONObject forecastObj = response.getJSONObject("forecast");
                JSONObject forcastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                JSONArray hourArray = forcastO.getJSONArray("hour");

                for(int i=0;i<hourArray.length();i++){
                    JSONObject hourObj = hourArray.getJSONObject(i);
                    String time = hourObj.getString("time");
                    String temper = hourObj.getString( "temp_c");
                    String img = hourObj.getJSONObject("condition").getString("icon");
                    String wind = hourObj.getString("wind_kph");
                    weatherRVModalArrayList.add(new weatherRVModal(time,temper,img,wind));
                }
                weatherRVAdapter.notifyDataSetChanged();
            } catch (JSONException e){
                e.printStackTrace();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter the valid city name", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}