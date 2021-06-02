package edu.harvard.cs50.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;


public class PokemonActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TextView;
    private TextView type2TextView;
    private String url;
    private RequestQueue requestQueue;
    private ImageView sprite;
    private Button button;
    private TextView descriTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        // get the extra string from get intent method
        url = getIntent().getStringExtra("url");

        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);
        button = findViewById(R.id.pokemon_button);
        sprite = findViewById(R.id.image);
        descriTextView = findViewById(R.id.description);

        load();
    }

    // each time Catch/release button is pressed, this is activated

    public void toggleCatch(View view) {
        String pokeName = nameTextView.getText().toString();
        boolean State = getPreferences(Context.MODE_PRIVATE).getBoolean(pokeName, false);
        Button isCatch = findViewById(R.id.pokemon_button);
        if (State) {
            isCatch.setText("Catch");
            getPreferences(Context.MODE_PRIVATE).edit().putBoolean(pokeName, false).commit();
        }
        else {
            isCatch.setText("Release");
            getPreferences(Context.MODE_PRIVATE).edit().putBoolean(pokeName, true).commit();
        }
    }


    public void load() {
        // initialize the string
        type1TextView.setText("");
        type2TextView.setText("");
        descriTextView.setText("");
        // present in volley library, request json format code
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // after clicking on the pokemon name, this is the data shown
                    nameTextView.setText(response.getString("name"));
                    numberTextView.setText(String.format("#%03d", response.getInt("id")));


                    if (getPreferences(Context.MODE_PRIVATE).getBoolean(nameTextView.getText().toString(), false)) {
                        button.setText("Release");
                    }
                    // put all the types in the JSONArray
                    // JSONArray is an array of JSONObjects of types
                    JSONArray typeEntries = response.getJSONArray("types");
                    // sprites is an object
                    JSONObject sprite = response.getJSONObject("sprites");
                    String urlSprite = sprite.getString("front_default");
                    //go through the array to put slot and name of type
                    for (int i = 0; i < typeEntries.length(); i++) {
                        // get the object using getJSONObject
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");
                        if (slot == 1) {
                            type1TextView.setText(type);
                        } else if (slot == 2) {
                            type2TextView.setText(type);
                        }
                    }
                    // species is an object
                    JSONObject species = response.getJSONObject("species");
                    String speciesUrl = species.getString("url");
                    JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, speciesUrl, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response2) {
                            try {
                                JSONArray text_entries = response2.getJSONArray("flavor_text_entries");
                                JSONObject text_entry = text_entries.getJSONObject(0);
                                descriTextView.setText(text_entry.getString("flavor_text"));

                            } catch (JSONException e) {
                                Log.e("cs50", "Pokemon JSONsprite error");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e("cs50", "Cannot load species", volleyError);
                        }
                    });
                    requestQueue.add(request1);
                    new DownloadSpriteTask().execute(urlSprite);
                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon Json Error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon detail error", error);
            }
        });
        requestQueue.add(request);
    }


    // class to download sprite
    private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openStream());
            }
            catch (IOException e) {
                Log.e("cs50", "Download sprite error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // load the bitmap into the ImageView!
            sprite.setImageBitmap(bitmap);
        }
    }

}