package com.example.weatherapp;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText city;
    TextView main;
    TextView description;
    TextView temperature;
    TextView humidity;

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                JSONObject mainInfo = new JSONObject(jsonObject.getString("main"));

                double temp = Double.parseDouble(mainInfo.getString("temp")) - 273.15;

                temperature.setText(String.format("Temperature: %.2f C",temp));
                humidity.setText("Humidity: " + mainInfo.getString("humidity") + "%");

                JSONArray weatherArray = new JSONArray(weatherInfo);

                for(int i = 0; i < weatherArray.length(); i++){
                    JSONObject jsonPart = weatherArray.getJSONObject(i);
                    main.setText(jsonPart.getString("main"));
                    description.setText("Description: " + jsonPart.getString("description"));
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Could not find the weather", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            String result ="";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return  result;

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Could not find the weather", Toast.LENGTH_SHORT).show();
                return  null;
            }

        }
    }

    public void onClick(View view){
        String cityText = null;
        try {
            cityText = URLEncoder.encode(city.getText().toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Could not find the weather", Toast.LENGTH_SHORT).show();
        }
        String link = "http://api.openweathermap.org/data/2.5/weather?q=";
        String key = "&APPID=9de0d0efdb7052cf7dffdb0fa6fce96f";

        link += cityText;

        link += key;

        Log.i("LINK", link);

        DownloadTask task = new DownloadTask();

        try {
            task.execute(link).get();
        } catch (Exception e){
            Toast.makeText(MainActivity.this, "Could not find the weather", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(city.getWindowToken(),0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = findViewById(R.id.cityTextField);
        main = findViewById(R.id.mainTextView);
        description = findViewById(R.id.descriptionTextView);
        temperature = findViewById(R.id.temperatureTextView);
        humidity = findViewById(R.id.humidityTextView);
    }
}