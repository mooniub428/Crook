package com.example.stathis.crookstore.Connection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Post_Request_Handler {

    private static final String TAG = "Post_Request_Handler";

    private static final String REQUEST_METHOD = "POST";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    public String performPostCall(String requestURL, HashMap<String, String> postDataParams){

        URL url;
        String response = "";
        try{
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod(REQUEST_METHOD); // Should check if GET method works too
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setDoInput(true); //default doInput flag is true
            conn.setDoOutput(false); //default doOutput flag is false

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8")
            );
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if(responseCode == HttpsURLConnection.HTTP_OK){
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while((line=br.readLine()) != null){
                    response+=line;
                }
            } else {
                response = "";
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        Log.d(TAG, "performPostCall: " + postDataParams.get("firstname"));
        Log.d(TAG, "performPostCall: " + response);
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if(first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
