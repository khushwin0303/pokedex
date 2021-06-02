package edu.harvard.cs50.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    // recycler view activities
    private RecyclerView recyclerView;
    private PokedexAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    // called when an activity is creating a menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // the activity specifies the XML file to be used
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // a reference to item is created by using the menu's id
        MenuItem searchItem = menu.findItem(R.id.action_search);
        // implement the search
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        // argument passed will be a string which is being searched
        adapter.getFilter().filter(newText);
        return false;
    }
    @Override
    // called when user press the submit button
    public boolean onQueryTextSubmit(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // each recycler view has its own id
        recyclerView = findViewById(R.id.recycler_view);
        // getApplicationContext() returns all the activities of the app
        adapter = new PokedexAdapter(getApplicationContext());
        // display the layout in linear view
        layoutManager = new LinearLayoutManager(this);

        // set the adapter and layout of the recycler view
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }
}
