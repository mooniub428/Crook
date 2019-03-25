package com.example.stathis.crookstore.Connection;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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

public class Delete_Request_Handler {

    private static final String TAG = "Delete_Request_Handler";

    private static final String REQUEST_METHOD = "DELETE";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    public String performDeleteCall(String requestURL, HashMap<String, String> delDataParams){
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
            writer.write(getDeleteDataString(delDataParams));
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

        return response;
    }

    /**
     * Php file for delete gets parameters with file_get_contents("php://input")
     * so we convert parameters to JSON
     * @param params
     * @return result
     */
    private String getDeleteDataString(HashMap<String, String> params) {
        String result;
        JSONObject jsonObject = new JSONObject();

        try{
            for(Map.Entry<String, String> entry : params.entrySet()){
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        result = jsonObject.toString();
        return result;
    }
}
