package edu.harvard.cs50.pokedex;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
// link the recycler view class by inheritance
public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> implements Filterable {
    /**
     * <p>Returns a filter that can be used to constrain data with a filtering
     * pattern.</p>
     *
     * <p>This method is usually implemented by {@link Adapter}
     * classes.</p>
     *
     * @return a filter used to constrain data
     */
    @Override
    public Filter getFilter() {
        return new PokemonFilter();
    }
    // new list of pokemon to store the searched value
    private List<Pokemon> filtered = new ArrayList<>();
    private List<Pokemon> pokemon = new ArrayList<>();
    private class PokemonFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // implement your search here!
            FilterResults results = new FilterResults();

            List<Pokemon> filteredPokemon = new ArrayList<>();
            constraint.toString();
            if (constraint.toString() == "") {
                filteredPokemon.addAll(pokemon);
            }

            for (Pokemon pokemonCharacter : pokemon) {
                // convert the character name to lowercase so that comparisons can be made
                String characterName = pokemonCharacter.getName().toLowerCase();
                if (characterName.contains(constraint.toString().toLowerCase())) {
                    filteredPokemon.add(pokemonCharacter);
                }
            }
            results.values = filteredPokemon;
            results.count = filteredPokemon.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered = (List<Pokemon>) results.values;
            notifyDataSetChanged();
        }
    }

        // link the viewHolder with a subclass recylerview.viewholder
    public static class PokedexViewHolder extends RecyclerView.ViewHolder {
        // public members so that the app is installed
        public LinearLayout containerView;
        public TextView textView;
        // constructor
        PokedexViewHolder(View view) {
            super(view);
            // container view-- arrange the organization between layouts and other subviews
            // this is how the containerview and the text view will have their ids
            containerView = view.findViewById(R.id.pokedex_row);
            // displays text to the user
            textView = view.findViewById(R.id.pokedex_row_text_view);

            // link the containerview with touch/click operations
            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // getTag returns an object so it needs to be casted
                    Pokemon current = (Pokemon) containerView.getTag();
                    // intent pass data from an activity to another activity. v.getContext() gets all
                    // data from the MainActivity or current view  class
                    Intent intent = new Intent(v.getContext() , PokemonActivity.class);
                    // add the url in the data
                    intent.putExtra("url", current.getUrl());

                    // it then re starts the PokemonActivity class
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
    // make a list of pokemons

    private RequestQueue requestQueue;

    PokedexAdapter(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        loadPokemon();
    }

    public void loadPokemon() {
        String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
        // present in volley library, request json format code
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // load json objects
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        // the api takes a name and a url
                        String name = result.getString("name");
                        // add also to filtered so that all pokemon data is displayed at launch
                        pokemon.add(new Pokemon(name.substring(0, 1).toUpperCase() + name.substring(1),
                                result.getString("url")));
                        filtered.add(new Pokemon(name.substring(0, 1).toUpperCase() + name.substring(1),
                                result.getString("url")));
                    }
                    // to notify the data has been changed
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("cs50", "Json Error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon list error");
            }
        });
        // to actually make the request
        requestQueue.add(request);
    }



    @NonNull
    @Override
    public PokedexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pokedex_row, parent, false);
        return new PokedexViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position) {
        Pokemon current = filtered.get(position);
        holder.textView.setText(current.getName());
        // view holder has access to pokemon
        holder.containerView.setTag(current);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return filtered.size();
    }
}
