package com.example.fishrecognition;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    public JSONObject object;
    private String NomDeBaseFirestore = "fishrecognition-d9b0e";
    private static DecimalFormat df = new DecimalFormat("#.######");
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Context context;
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("fish");

        setContentView(R.layout.activity_result);
        textView = findViewById(R.id.textView);

        try {
            object = new JSONObject(getIntent().getStringExtra("ResultObject"));
            JSONArray jArray = object.getJSONArray("result");
            //First element in JsonArray
            JSONObject result = jArray.getJSONObject(0);
            String fish_name = result.getString("fish_name");
            double percentage = result.getDouble("percentage");

            textView.setText(String.format("Nom : %s - Pourcentage : %s", fish_name, df.format(percentage) + "%"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        loadJSONFromAsset(context);
    }
    
    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("fishObject.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            Log.d("json", String.valueOf(obj));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;

    }
}
