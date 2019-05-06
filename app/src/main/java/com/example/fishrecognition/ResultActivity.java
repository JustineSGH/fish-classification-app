package com.example.fishrecognition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class ResultActivity extends AppCompatActivity {

    private JSONObject object;
    private static DecimalFormat df = new DecimalFormat("#.######");
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        //textView.setText(object);
    }
}
