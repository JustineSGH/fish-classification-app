package com.example.fishrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    public JSONObject object;
    public Bitmap image;
    private static DecimalFormat df = new DecimalFormat("#.######");
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Context context;
    public TextView textViewName, textViewScore, textViewIntroduction, textViewReproduction;
    public TextView textViewNage, textViewSociabilite, textViewTerritorial;
    public ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("fishObject");

        setContentView(R.layout.activity_result);
        textViewName = findViewById(R.id.textViewName);
        textViewScore = findViewById(R.id.textViewScore);
        imageView = findViewById(R.id.imageView);
        textViewReproduction = findViewById(R.id.textViewReproduction);
        textViewIntroduction = findViewById(R.id.textViewIntroduction);
        textViewNage = findViewById(R.id.textViewNage);
        textViewSociabilite = findViewById(R.id.textViewSociabilite);
        textViewTerritorial = findViewById(R.id.textViewTerritorial);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageView.setImageBitmap(image);



        try {
            object = new JSONObject(getIntent().getStringExtra("ResultObject"));
            JSONArray jArray = object.getJSONArray("result");
            //First element in JsonArray
            JSONObject result = jArray.getJSONObject(0);
            String fish_name = result.getString("fish_name");
            double percentage = result.getDouble("percentage");
            getFishObject(fish_name);

            textViewName.setText(fish_name);
            textViewScore.setText(df.format(percentage) + "%");
            //textViewName.setText(String.format("Nom : %s - Pourcentage : %s", fish_name, + "%"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //loadJSONFromAsset(context);
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

    public void getFishObject(final String name){
        Log.d("fish_name", name);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) dataSnapshot.getValue();
                JSONArray jsArray = new JSONArray(arrayList);
                Log.d("result", String.valueOf(jsArray));
                switch (name){
                    case "guppy":
                        try {
                            JSONObject object = (JSONObject) jsArray.get(0);
                            textViewReproduction.setText(object.getString("reproduction"));
                            textViewIntroduction.setText(object.getString("introduction"));
                            JSONObject comportement = object.getJSONObject("comportement");
                            textViewNage.setText(comportement.getString("type_de_nage"));
                            textViewSociabilite.setText(comportement.getString("sociabilité"));
                            textViewTerritorial.setText(comportement.getString("territorial"));
                            Log.d("result", String.valueOf(comportement.getString("type_de_nage")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "neon":
                        try {
                            JSONObject object = (JSONObject) jsArray.get(1);
                            textViewReproduction.setText(object.getString("reproduction"));
                            textViewIntroduction.setText(object.getString("introduction"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "danio_rerio":
                        JSONObject object = null;
                        try {
                            object = (JSONObject) jsArray.get(2);
                            textViewReproduction.setText(object.getString("reproduction"));
                            textViewIntroduction.setText(object.getString("introduction"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "scalaire":
                        try {
                            object = (JSONObject) jsArray.get(3);
                            textViewReproduction.setText(object.getString("reproduction"));
                            textViewIntroduction.setText(object.getString("introduction"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("result", String.valueOf(arrayList.get(3)));
                        break;
                    case "betta":
                        try {
                            object = (JSONObject) jsArray.get(4);
                            textViewReproduction.setText(object.getString("reproduction"));
                            textViewIntroduction.setText(object.getString("introduction"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("result", String.valueOf(arrayList.get(4)));
                        break;
                    case "botia":
                        try {
                            object = (JSONObject) jsArray.get(5);
                            textViewReproduction.setText(object.getString("reproduction"));
                            textViewIntroduction.setText(object.getString("introduction"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("result", String.valueOf(arrayList.get(5)));
                        break;
                    case "gourami":
                        try {
                            object = (JSONObject) jsArray.get(6);
                            textViewReproduction.setText(object.getString("reproduction"));
                            textViewIntroduction.setText(object.getString("introduction"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("result", String.valueOf(arrayList.get(6)));
                        break;
                    case "pangio_kuhli":
                        try {
                            object = (JSONObject) jsArray.get(7);
                            textViewReproduction.setText(object.getString("reproduction"));
                            textViewIntroduction.setText(object.getString("introduction"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("result", String.valueOf(arrayList.get(7)));
                        break;
                    case "ancistrus":
                        try {
                            object = (JSONObject) jsArray.get(8);
                            textViewReproduction.setText(object.getString("reproduction"));
                            textViewIntroduction.setText(object.getString("introduction"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("result", String.valueOf(arrayList.get(8)));
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("result", databaseError.toException());
            }
        });
    }
}
