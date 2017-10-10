package net.alexblass.bakingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    // TODO: Verify localization requirements
    // TODO: Verify accessibility requirements
    // TODO: Add butterknife
    // TODO: Use esspresso tests
    // TODO: On video full screen swipe, show app bars
    // TODO: Enable video full screen and shrink screen buttons
    // TODO: Enable view rest of the content on landscape

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Inflate our options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    // Determine what action to take based on the menu item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_add_recipe){
            Intent addActivityIntent = new Intent(MainActivity.this, AddRecipeActivity.class);
            startActivity(addActivityIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
