package com.dat255tesla.busexplorer.apirequest;

import android.os.AsyncTask;
import android.util.Base64;

import com.dat255tesla.busexplorer.explorercontent.MapUtils;
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
public class APIHelper extends AsyncTask<Void, Void, Void> {

    private IBusDataListener bdl;
    private final String userPwd = "grp42:v9aD7MvAOG";
    private String encoded;
    private String dgw; // Use numbers like 100021, see dgw
    //private String sensor = "GPS2";
    private LatLng position = new LatLng(0, 0);
    private String nextStop = "";

    public APIHelper(IBusDataListener bdl, String dgw) {
        this.bdl = bdl;
        this.dgw = dgw;
        this.encoded = Base64.encodeToString(userPwd.getBytes(), Base64.DEFAULT); //Encode to Base64 format
    }

    /*
    Send URL request to receive all possible data from specific bus (vin) sensor.
     */
    private void getBusData() throws IOException, JSONException {
        long t2 = System.currentTimeMillis();
        long t1 = t2 - (1000 * 5);

        StringBuffer response = new StringBuffer();
        String key = encoded;
        String url = "https://ece01.ericsson.net:4443/ecity?dgw=Ericsson$"
                + dgw + "&t1=" + t1 + "&t2=" + t2;

        URL requestURL = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) requestURL
                .openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic " + key);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            System.out.println("\ninputLine printout: " + inputLine);
        }
        in.close();

        String responseString = response.toString();

        parseBusNextStop(responseString);
        parseBusGPS(responseString);
    }

    private void parseBusGPS(String responseString) throws JSONException {
        /*
        String printout = ""; // TEMP TEST PRINTOUT
        // Ugly solution for now
        double lat = 0;
        double lon = 0;
        */

        if (!responseString.isEmpty()) {
            JSONArray jsonArray = new JSONArray(responseString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String spec = object.getString("resourceSpec");
                if (spec.equals("RMC_Value")) {
                    position = MapUtils.ParseRMC(object.getString("value"));
                }
                /*
                if (spec.equals("Latitude2_Value")) {
                    lat = object.getDouble("value");
                    printout = printout + "\n************Latitude: " + lat + "\t Date: " + new Date(object.getLong("timestamp"));
                } else if (spec.equals("Longitude2_Value")) {
                    lon = object.getDouble("value");
                    printout = printout + "\n************Longitude: " + lon + "\t Date: " + new Date(object.getLong("timestamp"));
                }
                */
            }
        }

        /*
        System.out.println(printout);
        position = new LatLng(lat, lon);
        */
    }

    private void parseBusNextStop(String responseString) throws JSONException {
        String printout = ""; // TEMP TEST PRINTOUT
        if (!responseString.isEmpty()) {
            JSONArray jsonArray = new JSONArray(responseString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String spec = object.getString("resourceSpec");
                if (spec.equals("Bus_Stop_Name_Value")) {
                    nextStop = object.getString("value");
                    printout = printout + "\n************Next_Stop: " + nextStop + "\t Date: " + new Date(object.getLong("timestamp"));
                }
            }
        }

        System.out.println(printout);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (!isCancelled()) {
            try {
                getBusData();
                publishProgress();
                Thread.sleep(2000);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... voids) {
        bdl.positionChanged(position);
        //bdl.nextStopChanged(nextStop); // TODO: Enable when we got real bus stops in server database!
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // TODO: Do something (FINALLY) after the task is done?
    }

}