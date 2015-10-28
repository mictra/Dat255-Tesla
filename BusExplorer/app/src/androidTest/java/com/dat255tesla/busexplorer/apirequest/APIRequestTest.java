package com.dat255tesla.busexplorer.apirequest;

import android.util.Base64;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

/**
 * NOTE: This test is dependent on Ericsson's API and might fail if they (Ericsson)
 * decide to stop receiving requests, and/or denies the key (authorization to call API).
 * Disable, and/or do not run this test if that is the case.
 */
public class APIRequestTest extends TestCase {

    private String userPwd;
    private String key;
    private long t1;
    private long t2;
    private String url;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userPwd = "grp42:v9aD7MvAOG"; // Authentication key from EIC
        key = Base64.encodeToString(userPwd.getBytes(), Base64.DEFAULT);
        // Set the time interval for the data that we want to receive (5 seconds)
        t2 = System.currentTimeMillis();
        t1 = t2 - (1000 * 5);
        // The url with a specific bus dgw (a simulated bus in this case) that we want to request
        url = "https://ece01.ericsson.net:4443/ecity?dgw=Ericsson$"
                + "Vin_Num_001" + "&t1=" + t1 + "&t2=" + t2;
    }

    /**
     * Test request with proper url and key to get a response with status OK.
     *
     * @throws IOException
     */
    public void testResponseOK() throws IOException {
        URL requestURL = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) requestURL
                .openConnection();
        // Set type of request and authorization key
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic " + key);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        assertTrue(responseCode == HttpsURLConnection.HTTP_OK);
    }

    /**
     * Test request with wrong key which should deny authentication (unauthorized).
     *
     * @throws IOException
     */
    public void testResponseUnAuth() throws IOException {
        URL requestURL = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) requestURL
                .openConnection();
        // Set type of request and authorization key (should fail with wrong key)
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic " + "abc" + key);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        assertTrue(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED);
    }

    /**
     * Test request with known host, but bad url (not known/found).
     *
     * @throws IOException
     */
    public void testResponseNotFound() throws IOException {
        String badUrl = "https://ece01.ericsson.net:4443/badUrl";
        URL requestURL = new URL(badUrl);
        HttpsURLConnection con = (HttpsURLConnection) requestURL
                .openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic " + key);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + badUrl);
        System.out.println("Response Code : " + responseCode);

        assertTrue(responseCode == HttpsURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Test to request an unknown host which should catch an UnknownHostException.
     *
     * @throws IOException
     */
    public void testUnknownHost() throws IOException {
        String unknownUrl = "https://unknownhost.ece01.ericsson.net:4443/";
        URL requestURL = new URL(unknownUrl);
        HttpsURLConnection con = (HttpsURLConnection) requestURL
                .openConnection();
        con.setRequestMethod("GET");

        try {
            con.getResponseCode();
            // We do not catch the exception and the test fails
            assertTrue(false);
        } catch (UnknownHostException e) {
            // We catch the exception and succeed with the test
            assertTrue(true);
        }
    }

}
