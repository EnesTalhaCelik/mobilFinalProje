package com.example.finalproje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
    private ProgressBar LoadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV; private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        
        setContentView(R.layout.activity_main);
        homeRL = findViewById(R.id.idRLHome);
        LoadingPB =findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idVSearch);
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this,weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
        &&ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET},PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        try {
            cityName = getCityName(location.getLongitude(), location.getLatitude());
        }catch (Exception x){
            x.printStackTrace();
        }
        getWeatherInfo(cityName);
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Lütfen bir konum giriniz.", Toast.LENGTH_SHORT).show();


                }else {

                    cityNameTV.setText(cityName); //burada mantık hatası mı var ?
                    getWeatherInfo(city);
                }

            }
        });




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==  PackageManager.PERMISSION_GRANTED){



                Toast.makeText(MainActivity.this,"İşlem Başarılı.", Toast.LENGTH_SHORT).show();

            }else{

                Toast.makeText(MainActivity.this,"İşlem Başarısız.", Toast.LENGTH_SHORT).show();
                finish();
            }


        }
    }

    private String getCityName(double longitude, double latitude){
            String cityName = "Not Found";
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List <Address>  addresses= gcd.getFromLocation(latitude,longitude,10);
                for(Address adr : addresses){
                    if(adr!= null){
                        String city = adr.getLocality();
                        if(city != null && !city.equals("")){
                            cityName = city;
                            Log.d("TAG",cityName);

                        }else {
                            Log.d("TAG","Şehir Bulunamadı");
                            Toast.makeText(this,"Konum Bulunamadı.", Toast.LENGTH_SHORT).show();
                        }


                    }

                }



            }catch (IOException e){
                e.printStackTrace();


            }
            return cityName;
    }



    private void getWeatherInfo(String cityName){
       String url = "http://api.weatherapi.com/v1/forecast.json?key=7f1ba5d6980e49d7ae9165011242605&q="+cityName+"&days=1&aqi=no&alerts=yes";
        //String url = "http://api.weatherapi.com/v1/forecast.json?key=7f1ba5d6980e49d7ae9165011242605&q=Balikesir&days=1&aqi=no&alerts=yes";
        //test için yazdım
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


                @Override
                public void onResponse(JSONObject response) {
                    LoadingPB.setVisibility(View.GONE);
                    homeRL.setVisibility(View.VISIBLE);
                    weatherRVModalArrayList.clear();


                    try {
                        String temperature = response.getJSONObject("current").getString("temp_c");
                        temperatureTV.setText(temperature+"°c");
                        int is_day = response.getJSONObject("current").getInt("is_day");
                        String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                        String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                        Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                        conditionTV.setText(condition);
                        if(is_day==1){//gündüz
                            Picasso.get().load("https://w0.peakpx.com/wallpaper/596/666/HD-wallpaper-sky-clouds-partly-cloudy-sunshine-weather.jpg").into(backIV);
                        }else {
                            Picasso.get().load("https://i.pinimg.com/736x/3f/04/9a/3f049ab9ac59d6ba38d2bd017455e3b7.jpg").into(backIV);
                        }
                        JSONObject forecastObj = response.getJSONObject("forecast");
                        JSONObject forcast0 =forecastObj.getJSONArray("forecastday").getJSONObject(0);
                        JSONArray hourArray = forcast0.getJSONArray("hour");

                        for (int i = 0; i<hourArray.length();i++){
                                JSONObject hourObj = hourArray.getJSONObject(i);
                                String time = hourObj.getString("time");
                            String temp = hourObj.getString("temp_c");
                            String img = hourObj.getJSONObject("condition").getString("icon");
                            String wind = hourObj.getString("wind_kph");
                            weatherRVModalArrayList.add(new WeatherRVModal(time,temp,img,wind));

                        }
                        weatherRVAdapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                }
                },new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error){
                    Toast.makeText(MainActivity.this,"Lütfen geçerli bir şehir ismi giriniz.", Toast.LENGTH_SHORT).show();

                }

            });
            requestQueue.add(jsonObjectRequest);
        }

    }


