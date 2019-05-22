package com.example.fishrecognition;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectionServer extends AsyncTask<Bitmap, Void, JSONObject> {

    //private static final String NOTIF_CHANNEL_ID = "Channel_Id";
    private Context context;
    ProgressBar progressBar;
    private Bitmap image;
    private  byte[] byteArray;

    public ConnectionServer(Context context){
        this.context = context.getApplicationContext();
    }

    public void setProgressBar(ProgressBar bar) {
        this.progressBar = bar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    @Override
    protected JSONObject doInBackground(Bitmap... imageToSend) {
        image = imageToSend[0];
        image = scaleDownBitmap(imageToSend[0], 100, context);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byteArray = stream.toByteArray();
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
            imageToSend[0].compress(Bitmap.CompressFormat.JPEG, 100, os);
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

            conn.disconnect();
            return jObject;
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (this.progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        Intent intent = new Intent(context, ResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("image", byteArray);
        intent.putExtra("ResultObject", result.toString());
        context.startActivity(intent);
    }

    /*public static void getData(){
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
    }*/

    /* public void createNotification(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("unique_channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
        }
        Intent intent = new Intent(this, ConnectionServer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Résultat")
                .setContentText("Voici le résultat !");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(123456, builder.build());




        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
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
        }
    }*/

}
