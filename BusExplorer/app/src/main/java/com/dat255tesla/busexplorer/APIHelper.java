package com.dat255tesla.busexplorer;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Michael on 2015-09-24.
 */
public class APIHelper extends AsyncTask<String, String, String> {

    private TextView tv;

    public APIHelper(TextView tv){
        this.tv = tv;
    }

    private String doGet(String encoded) throws IOException {
        long t2 = System.currentTimeMillis();
        long t1 = t2 - (1000 * 120);

        StringBuffer response = new StringBuffer();
        //TODO Enter your base64 encoded Username:Password
        String key = encoded;
        String url = "https://ece01.ericsson.net:4443/ecity?dgw=Ericsson$Vin_Num_001&sensorSpec=Ericsson$Next_Stop&t1="
                + t1 + "&t2=" + t2;

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
        }
        in.close();
        System.out.println(response.toString());
        return response.toString();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return doGet(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "NO WORK :<";
    }

    @Override
    protected void onProgressUpdate(String... values) {
        tv.setText(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        tv.setText(s);
    }

}
