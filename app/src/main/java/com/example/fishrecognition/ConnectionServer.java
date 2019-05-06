package com.example.fishrecognition;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectionServer extends AppCompatActivity {

    private static final String NOTIF_CHANNEL_ID = "Channel_Id";
    private Callback listener;

    public void registerCallback(Callback callback){
        listener = callback;
    }

    public static void getData(){
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL("http://51.83.73.150:3000/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            responseStreamReader.close();

            String response = stringBuilder.toString();
            Log.d("response" , response);
            JSONObject jObject = new JSONObject(response);
            String body = jObject.getString("message");
            Log.d("response" , body);
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void postData(Bitmap imageToSend){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL("http://51.83.73.150:3000/upload-image");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            //conn.setDoInput(true);
            //conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");

            conn.setReadTimeout(35000);
            conn.setConnectTimeout(35000);

            OutputStream os = conn.getOutputStream();
            imageToSend.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();

            System.out.println("Response Code: " + conn.getResponseCode());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            responseStreamReader.close();

            String response = stringBuilder.toString();
            JSONObject jObject = new JSONObject(response);
            //String body = jObject.getString("result");

            if(jObject.length() > 0 && conn.getResponseCode() == 200){
                Log.d("result" , String.valueOf(listener));
                if (listener != null) {
                    listener.onGetData(jObject);
                }
            }

            conn.disconnect();
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createNotification(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("unique_channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
        }
        /*Intent intent = new Intent(this, ConnectionServer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);*/


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Résultat")
                .setContentText("Voici le résultat !");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(123456, builder.build());




        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d("testNotif", "testNotif");
            NotificationChannel notificationChannel = new NotificationChannel("unique_channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{500, 500, 500, 500, 500});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "unique_channel_id")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Information")
                    .setContentText(" est à ")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ConnectionServer.class), PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(123456, mBuilder.build());
        }*/
    }

}
