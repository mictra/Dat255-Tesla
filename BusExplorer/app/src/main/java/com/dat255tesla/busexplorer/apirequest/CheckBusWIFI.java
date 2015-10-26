package com.dat255tesla.busexplorer.apirequest;

import android.os.AsyncTask;

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
            bus_system_id = parseSystemIDXML(responseString);
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

    }

    /**
     * Parse the received response in XML format to get system_id of current bus
     * when connected to its wifi. If response is not in XML format or the XML-element
     * "system_id" does not exist, return string "0".
     *
     * @param xml
     * @return system_id
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private String parseSystemIDXML(String xml) throws ParserConfigurationException, IOException, SAXException {
        String system_id = "0";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document doc = builder.parse(is);
        NodeList nodeList = doc.getElementsByTagName("system");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element e = (Element) nodeList.item(i);
            NodeList systemId = e.getElementsByTagName("system_id");
            Element line = (Element) systemId.item(0);
            if (line != null) {
                system_id = getCharacterDataFromElement(line);
            }
        }

        System.out.println("----------------SystemID: " + system_id);
        return system_id;
    }

    /**
     * Helper method used by parseSystemIDXML() to parse CharacterData from Element.
     *
     * @param e
     * @return String
     */
    private String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "0";
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
