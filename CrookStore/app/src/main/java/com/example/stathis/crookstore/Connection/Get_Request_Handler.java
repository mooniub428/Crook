package com.example.stathis.crookstore.Connection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Get_Request_Handler {

    private static final String TAG = "Get_Request_Handler";

    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;


    public String performGetCall(String requestURL) {

        String result = "";
        String inputLine;

        try{

            //Create a URL object holding the url
            URL url = new URL(requestURL);
            Log.d(TAG, "performGetCall: " + requestURL);
            //Create a connection
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            //Set methods and timeouts
            conn.setRequestMethod(REQUEST_METHOD);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to url
            conn.connect();

            //Create a new InputStreamReader, buffered reader and String builder to read the response
            InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }

            //Close InputStream and Buffered reader
            reader.close();
            streamReader.close();

            result = stringBuilder.toString();


            Log.d(TAG, "performGetCall: " + result);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }

}
