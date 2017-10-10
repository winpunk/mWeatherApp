package mav.learn.jsonparsingtut;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private int temp;
    private int humidity;
    private int clouds;
    private double windsSpeed;
    private int windsDir;
    private String weather;
    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;

    // URL to get JSON
    private static String url = "http://api.openweathermap.org/data/2.5/weather?q=Vilnius&units=metric&appid=b836a69f365e3090e0f4b365736c2259";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetData().execute();

    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONObject temp1 = jsonObj.getJSONObject("main");
                    temp = temp1.getInt("temp");
                    humidity = temp1.getInt("humidity");

                    JSONObject cloud1 = jsonObj.getJSONObject("clouds");
                    clouds = cloud1.getInt("all");

                    JSONArray weather1 = jsonObj.getJSONArray("weather");
                    JSONObject main = weather1.getJSONObject(0);
                    weather = main.getString("description");

                    JSONObject wind1 = jsonObj.getJSONObject("wind");
                    windsSpeed = wind1.getDouble("speed");
                    windsDir = wind1.getInt("deg");


//all

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data
             * */

            TextView temperature = (TextView) findViewById(R.id.temp);
            temperature.setText(temp + " ℃");
            TextView humid = (TextView) findViewById(R.id.humidity);
            humid.setText(humidity + " %");
            TextView cloud = (TextView) findViewById(R.id.clouds);
            cloud.setText(clouds + " %");
            TextView wind = (TextView) findViewById(R.id.wind);
            wind.setText(windsSpeed + " m/s; " + windsDir + " deg");
            TextView weathe = (TextView) findViewById(R.id.weather);
            weathe.setText(weather +"!");
        }

    }
}