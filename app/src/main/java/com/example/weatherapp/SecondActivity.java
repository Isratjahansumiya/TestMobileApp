package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SecondActivity extends AppCompatActivity {

    private TextView textViewAllCityWeatherData;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        textViewAllCityWeatherData = findViewById(R.id.textViewAllCityWeatherData);

        requestQueue = Volley.newRequestQueue(this);

        fetchAllCityWeatherData();
    }

    private void fetchAllCityWeatherData() {
        String apiKey = BuildConfig.WEATHER_API_KEY;
        String[] cityIds = {"658225", "660158", "634964", "632453", "643492"}; // Example city IDs for Helsinki, Espoo, Tampere, Vantaa, Oulu
        StringBuilder urlBuilder = new StringBuilder("https://api.openweathermap.org/data/2.5/group?id=");

        for (String cityId : cityIds) {
            urlBuilder.append(cityId).append(",");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove the last comma
        urlBuilder.append("&appid=").append(apiKey);

        String url = urlBuilder.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray citiesArray = response.getJSONArray("list");
                            StringBuilder weatherData = new StringBuilder();
                            for (int i = 0; i < citiesArray.length(); i++) {
                                JSONObject city = citiesArray.getJSONObject(i);
                                String cityName = city.getString("name");
                                String weather = city.getJSONArray("weather")
                                        .getJSONObject(0).getString("description");
                                String temperature = city.getJSONObject("main")
                                        .getString("temp");
                                weatherData.append(getString(R.string.city)).append(": ").append(cityName).append("\n")
                                        .append(getString(R.string.weather)).append(": ").append(weather).append("\n")
                                        .append(getString(R.string.temperature)).append(": ").append(temperature).append("\n\n");
                            }
                            textViewAllCityWeatherData.setText(weatherData.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            textViewAllCityWeatherData.setText(R.string.error_parsing);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewAllCityWeatherData.setText(R.string.error_fetching);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("weather_data", textViewAllCityWeatherData.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String weatherData = savedInstanceState.getString("weather_data");
        textViewAllCityWeatherData.setText(weatherData);
    }
}




