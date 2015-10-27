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

import javax.net.ssl.HttpsURLConnection;

public class APIHelper extends AsyncTask<Void, Void, Void> {

    private IBusDataListener bdl;
    private final String userPwd = "grp42:v9aD7MvAOG"; // Authentication key from EIC
    private String encoded;
    private String dgw; // Bus dgw dumber, identifier for bus-data from API
    private LatLng position = new LatLng(0, 0);
    private String nextStop = "";

    public APIHelper(IBusDataListener bdl, String dgw) {
        // Set the listener to notify
        this.bdl = bdl;
        this.dgw = dgw;
        //Encode to Base64 format
        this.encoded = Base64.encodeToString(userPwd.getBytes(), Base64.DEFAULT);
    }

    /**
     * Sends URL request to receive all possible data from specific bus (dgw) sensors.
     * Later calls parseBusNextStop() and parseBusGPS() to parse relevant data needed
     * from the received response (as a JSONArray).
     *
     * @throws IOException
     * @throws JSONException
     */
    private void getBusData() throws IOException, JSONException {
        // Set the time interval for the data that we want to receive
        long t2 = System.currentTimeMillis();
        long t1 = t2 - (1000 * 5);

        StringBuffer response = new StringBuffer();
        String key = encoded;
        // The url with a specific bus dgw that we want to request
        String url = "https://ece01.ericsson.net:4443/ecity?dgw=Ericsson$"
                + dgw + "&t1=" + t1 + "&t2=" + t2;

        URL requestURL = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) requestURL
                .openConnection();
        // Set type of request and authorization key
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic " + key);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;

        // Read the response and save
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            System.out.println("\ninputLine printout: " + inputLine);
        }
        in.close();

        String responseString = response.toString();

        // Parse and store relevant data
        parseBusNextStop(responseString);
        parseBusGPS(responseString);
    }

    /**
     * Parse the received response (as a JSONArray) from Ericsson to get a RMC String value,
     * later parse the RMC String value to a LatLng value and store it to #position.
     *
     * @param responseString
     * @throws JSONException
     */
    private void parseBusGPS(String responseString) throws JSONException {
        if (!responseString.isEmpty()) {
            JSONArray jsonArray = new JSONArray(responseString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String spec = object.getString("resourceSpec");
                if (spec.equals("RMC_Value")) {
                    position = MapUtils.ParseRMC(object.getString("value"));
                    break;
                }
            }
        }
    }

    /**
     * Parse the received response (as a JSONArray) from Ericsson to
     * get a next bus stop String value and store it to #nextStop.
     *
     * @param responseString
     * @throws JSONException
     */
    private void parseBusNextStop(String responseString) throws JSONException {
        if (!responseString.isEmpty()) {
            JSONArray jsonArray = new JSONArray(responseString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String spec = object.getString("resourceSpec");
                if (spec.equals("Bus_Stop_Name_Value")) {
                    nextStop = object.getString("value");
                    break;
                }
            }
        }
    }

    /**
     * A background task that runs once this class instance gets executed.
     * It runs and loops with an interval of #callInterval milliseconds,
     * until the task gets cancelled. Calls getBusData() and publishProgress()
     * (which calls onProgressUpdate()) for each loop.
     *
     * @param voids
     * @return null
     */
    @Override
    protected Void doInBackground(Void... voids) {
        long callInterval = 2000;
        while (!isCancelled()) {
            try {
                getBusData();
                publishProgress();
                Thread.sleep(callInterval);
            } catch (JSONException | IOException | InterruptedException e) {
                e.printStackTrace();
                cancel(true);
            }
        }
        return null;
    }

    /**
     * Notifies/Updates the listener with relevant parsed data-values.
     *
     * @param voids
     */
    @Override
    protected void onProgressUpdate(Void... voids) {
        bdl.positionChanged(position);
        bdl.nextStopChanged(nextStop);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // We don't do anything when the AsyncTask has been executed.
    }

}