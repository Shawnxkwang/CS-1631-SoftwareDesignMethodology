//package UpLoader;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.applet.*;
import java.security.*;

public class PostQuery {

    public static void PostToPHP(String UrlIn, String QueryIn){
        HttpURLConnection conn=null;
        try{
        URL url=new URL(UrlIn);
        String agent="Applet";
        String query="query=" + QueryIn;
        String type="application/x-www-form-urlencoded";
        conn=(HttpURLConnection)url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty( "User-Agent", agent );
        conn.setRequestProperty( "Content-Type", type );
        conn.setRequestProperty( "Content-Length", ""+query.length());
        OutputStream out=conn.getOutputStream();
        out.write(query.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        while((inputLine=in.readLine())!=null){
            //System.out.print(inputLine+"\n");
        }
        in.close();
        int grc = conn.getResponseCode();
        //System.out.print("ResponseCode = "+ grc +"\n");
        String grm=conn.getResponseMessage();
        //System.out.print("ResponseMessage = "+ grm +"\n");
        }catch(Exception e){
        e.printStackTrace();
        }finally{
        conn.disconnect();
        }
    }
    }