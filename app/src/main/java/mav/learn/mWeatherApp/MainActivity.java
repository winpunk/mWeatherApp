package mav.learn.mWeatherApp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    public String city = "vilnius";
    private int temp;
    private int humidity;
    private int clouds;
    private double windsSpeed;
    private int windsDir;
    private String weather;
    private String TAG = MainActivity.class.getSimpleName();

    // URL to get JSON
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetData().execute();

    }

    public void chan_city(View view) {

        //city = "Kaunas";
        //change_city.setText(city);
        // new GetData().execute();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                city = input.getText().toString();
                Button change_city = (Button) findViewById(R.id.ch_city);
                change_city.setText(city);
                new GetData().execute();
            }
        });
        builder.show();


    }

    public void refresh() {

        new GetData().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.refresh) {
            refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            pDialog.setMessage("Updating weather info...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response

            String jsonStr = sh.makeServiceCall(HttpHandler.makeUrl(city).toString());

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
            humid.setText("Humid: " + humidity + " %");
            TextView cloud = (TextView) findViewById(R.id.clouds);
            cloud.setText("Cloud cov.: " + clouds + " %");
            TextView wind = (TextView) findViewById(R.id.wind);
            wind.setText(windsSpeed + " m/s; " + windsDir + " deg");
            //TextView weathe = (TextView) findViewById(R.id.weather);
            //weathe.setText("Cond.: " + weather + "!");
            ImageView cond = (ImageView) findViewById(R.id.condition);
            cond.setImageResource(R.drawable.partly_cloudy_rain);

        }


    }
}