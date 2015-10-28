package com.dat255tesla.busexplorer.apirequest;

import android.os.AsyncTask;

import com.dat255tesla.busexplorer.explorercontent.MapUtils;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class CheckBusWIFI extends AsyncTask<Void, Void, Void> {

    private IBusWifiListener bwl;
    private String bus_system_id = "0";

    public CheckBusWIFI(IBusWifiListener bwl) {
        // Set the listener to notify
        this.bwl = bwl;
    }

    private void getBusSystemID() throws IOException {
        // The url we want to request when connected to one of the electric buses' wifi
        String url = "https://ombord.info/api/xml/system/";
        URL requestURL = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) requestURL
                .openConnection();
        con.setRequestMethod("GET");

        StringBuffer response = new StringBuffer();
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;

        // Read and save the response
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        String responseString = response.toString();

        // Try to parse the system_id of the bus
        try {
            bus_system_id = MapUtils.parseSystemIDXML(responseString);
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

    }

    /**
     * A background task that runs once this class instance gets executed.
     * Calls getBusSystemID() once executed and calls onPostExecute() when done
     * executing.
     *
     * @param params
     * @return null
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            getBusSystemID();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Notifies/Updates the listener with relevant parsed
     * data-values (system_id of the bus in this case).
     *
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        bwl.notifySystemId(bus_system_id);
    }
}
