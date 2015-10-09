package com.dat255tesla.busexplorer.apirequest;

import android.os.AsyncTask;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Michael on 2015-09-24.
 */
public class APIHelper extends AsyncTask<String, LatLng, String> {

    private IPositionChangedListener pcl;
    private boolean update;
    private final String userPwd = "grp42:v9aD7MvAOG";
    private String encoded;
    private String vin = "Vin_Num_001"; // Use numbers like 100021, see dgw
    private String sensor = "GPS2";

    public APIHelper(IPositionChangedListener pcl) {
        this.pcl = pcl;
        this.update = true;
        this.encoded = Base64.encodeToString(userPwd.getBytes(), Base64.DEFAULT); //Encode to Base64 format
    }

    private LatLng doGet() throws IOException, JSONException {
        long t2 = System.currentTimeMillis();
        long t1 = t2 - (1000 * 5);

        StringBuffer response = new StringBuffer();
        //TODO Enter your base64 encoded Username:Password
        String key = encoded;
        String url = "https://ece01.ericsson.net:4443/ecity?dgw=Ericsson$" + vin +
                "&sensorSpec=Ericsson$" + sensor + "&t1=" + t1 + "&t2=" + t2;

        URL requestURL = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) requestURL
                .openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic " + key);

        int responseCode = con.getResponseCode();
        System.out.println("\nThe key is: " + key);
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            System.out.println("inputLine printout: " + inputLine);
        }
        in.close();

        String printout = "";//TEMP TEST
        String responseString = response.toString();
        // Ugly solution for now
        double lat = 0;
        double lon = 0;

        if (!responseString.isEmpty()) {
            JSONArray jsonArray = new JSONArray(responseString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String spec = object.getString("resourceSpec");
                if (spec.equals("Latitude2_Value")) {
                    lat = object.getDouble("value");
                    printout = printout + "\nLatitude: " + lat + "\t Date: " + new Date(object.getLong("timestamp"));
                } else if (spec.equals("Longitude2_Value")) {
                    lon = object.getDouble("value");
                    printout = printout + "\nLongitude: " + lon + "\t Date: " + new Date(object.getLong("timestamp"));
                }
            }
        }

        System.out.println(printout);
        return new LatLng(lat, lon);
    }

    @Override
    protected String doInBackground(String... params) {
        while (update) {
            try {
                publishProgress(doGet());
                Thread.sleep(3000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return "FINISH ASYNCTASK!";
    }

    @Override
    protected void onProgressUpdate(LatLng... pos) {
        pcl.positionChanged(pos[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        // TODO: Do something (FINALLY) after the task is done?
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

}
