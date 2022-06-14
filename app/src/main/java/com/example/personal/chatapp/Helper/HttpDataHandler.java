package com.example.personal.chatapp.Helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Personal on 24-04-2019.
 */

public class HttpDataHandler {
    static String stream=null;

    public HttpDataHandler(){

    }

    public String GetHttpData(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();

            if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                InputStream in=new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader br=new BufferedReader(new InputStreamReader(in));
                StringBuilder sb=new StringBuilder();
                String line;
                while((line= br.readLine())!=null)
                    sb.append(line);

                stream=sb.toString();
                urlConnection.disconnect();
            }
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {

        }
        return stream;
    }
}
