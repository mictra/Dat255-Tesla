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

/**
 * Created by Michael on 2015-10-13.
 */
public class CheckBusWIFI extends AsyncTask<Void, Void, Void> {

    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <system version=\"-1.0\"> <system_id type=\"integer\">10062621</system_id> </system>";

    private void getBusSystemID() throws IOException {
        /*
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

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            System.out.println("\ninputLine printout: " + inputLine);
        }
        in.close();

        String responseString = response.toString();
        System.out.println("!!!!!!!!!RESPONSE FROM ICOMERA API!!!!!!!!!!:\n" + responseString);
        */

        try {
            parseSystemIDXML(xml);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    private String parseSystemIDXML(String xml) throws ParserConfigurationException, IOException, SAXException {
        String system_id = "";
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
            system_id = getCharacterDataFromElement(line);
            System.out.println("----------------SystemID: " + system_id);
        }

        return system_id;
    }

    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            getBusSystemID();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
