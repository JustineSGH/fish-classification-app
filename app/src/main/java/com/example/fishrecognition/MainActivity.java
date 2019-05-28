package com.example.fishrecognition;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    ImageView idImage;
    Integer SELECT_FILE = 0;
    public Uri imageUri;
    public ContentValues values;
    public Bitmap thumbnail;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;
    public static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public Context context;
    public ProgressBar progressBar;
    public Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context =  getApplicationContext();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        idImage = (ImageView) findViewById(R.id.idImage);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setVisibility(View.INVISIBLE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        checkPermission();
    }

    private void selectImage(){
        values = new ContentValues();
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        values.put(MediaStore.Images.Media.TITLE, "New picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Caméra");

        final CharSequence[] items = {"Caméra", "Gallerie", "Annuler"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Ajouter une image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(items[which].equals("Caméra")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        buttonSend.setVisibility(View.VISIBLE);
                    }
                }else if(items[which].equals("Gallerie")){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selectionner un fichier"), SELECT_FILE);
                    buttonSend.setVisibility(View.VISIBLE);
                }else if(items[which].equals("Annuler")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void checkPermission(){
       if(!(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
               && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
               ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
           }
           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
       }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    idImage.setImageBitmap(thumbnail);
                    String imageurl = getRealPathFromURI(imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(requestCode == SELECT_FILE){
                Uri selectedImageUri = data.getData();
                thumbnail = null;
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                idImage.setImageBitmap(thumbnail);
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickSendToServer(View view) {
        progressBar.setVisibility(View.VISIBLE);
        ConnectionServer connectionServer = new ConnectionServer(context);
        connectionServer.setProgressBar(progressBar);
        connectionServer.execute(thumbnail);
    }
}
