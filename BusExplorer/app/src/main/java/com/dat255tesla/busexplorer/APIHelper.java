package com.dat255tesla.busexplorer;

import android.os.AsyncTask;
import android.widget.TextView;

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
public class APIHelper extends AsyncTask<String, String, String> {

    private TextView tv;
    private boolean update;
    private String vin = "100021"; // Use numbers like 100020, see dgw
    private String sensor = "GPS2";

    public APIHelper(TextView tv) {
        this.tv = tv;
        this.update = true;
    }

    private String doGet(String encoded) throws IOException, JSONException {
        long t2 = System.currentTimeMillis();
        long t1 = t2 - (1000 * 10);

        StringBuffer response = new StringBuffer();
        //TODO Enter your base64 encoded Username:Password
        String key = encoded;
        String url = "https://ece01.ericsson.net:4443/ecity?dgw=Ericsson$" + vin +
                "&sensorSpec=Ericsson$" + sensor + "&t1=" + t1 + "&t2=" + t2;

        URL requestURL = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) requestURL
                .openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic " + key); //NICE WORK CYBERCOM FCKING SHIT!!!

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
        System.out.println(response.toString());

        String printout = "";//TEMP TEST

        /*
        JSONArray jsonArray = new JSONArray(response.toString());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            printout = printout + "\nNext stop: " + object.getString("value") + "\nTimestamp: " + new Date(object.getLong("timestamp"));
        }
        */

        return response.toString(); // TODO: Parse printout string and print it instead of this
    }

    @Override
    protected String doInBackground(String... params) {
        String info = "NO WORK :<";
        while (update) {
            try {
                info = doGet(params[0]);
                publishProgress(info);
                Thread.sleep(3000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return info;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        tv.setText(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        tv.setText(s);
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

}
