package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView textViewWeatherData;
    private RequestQueue requestQueue;
    private String weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewWeatherData = findViewById(R.id.textViewWeatherData);
        Button buttonFetchWeather = findViewById(R.id.buttonFetchWeather);
        Button buttonNavigate = findViewById(R.id.buttonNavigate);
        Button buttonOpenBrowser = findViewById(R.id.buttonOpenBrowser);
        Button buttonOpenMaps = findViewById(R.id.buttonOpenMaps);

        requestQueue = Volley.newRequestQueue(this);

        buttonFetchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchWeatherData();
            }
        });

        buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("weather_data", weatherData);
                startActivity(intent);
            }
        });

        buttonOpenBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri webpage = Uri.parse("http://www.example.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        buttonOpenMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=Helsinki");
                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        if (savedInstanceState != null) {
            weatherData = savedInstanceState.getString("weather_data");
            textViewWeatherData.setText(weatherData);
        }
    }

    private void fetchWeatherData() {
        String apiKey = BuildConfig.WEATHER_API_KEY;
        String city = "Helsinki,FI";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String cityName = response.getString("name");
                            String countryName = response.getJSONObject("sys").getString("country");
                            String weather = response.getJSONArray("weather")
                                    .getJSONObject(0).getString("description");
                            String temperature = response.getJSONObject("main")
                                    .getString("temp");
                            weatherData = getString(R.string.city) + ": " + cityName + "\n" +
                                    getString(R.string.country) + ": " + countryName + "\n" +
                                    getString(R.string.weather) + ": " + weather + "\n" +
                                    getString(R.string.temperature) + ": " + temperature;
                            textViewWeatherData.setText(weatherData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            textViewWeatherData.setText(R.string.error_parsing);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error fetching data: " + error.getMessage());
                textViewWeatherData.setText(R.string.error_fetching);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("weather_data", weatherData);
    }
}
