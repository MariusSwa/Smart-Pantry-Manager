package com.mariusswa.smartpantrymanager;

// Imports
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Button;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    // On creeate when the window opens
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            );
            return insets;
        });

        // Add ingredient button
        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnAddIngredient.setOnClickListener(v -> {
            Intent intent =
                    new Intent(MainActivity.this, AddIngredientActivity.class);
            startActivity(intent);
        });

        // Suggested recipes button
        Button btnSuggestedRecipes =
            findViewById(R.id.btnSuggestedRecipes);
        btnSuggestedRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(
                MainActivity.this,
                SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        // Settings button
        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {

            // intents to move to new window
            Intent intent = new Intent(
                MainActivity.this,
                SettingsActivity.class
            );
            startActivity(intent);
        });
    }

    // When we come back to the main screen
    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    // Load the pantry items
    private void loadPantryItems() {
        TextView tvEmptyPantry = findViewById(R.id.tvEmptyPantry);
        ListView listPantry = findViewById(R.id.listPantry);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<PantryItem> pantryItems =
            databaseHelper.getAllPantryItems();

        // If there is no pantry items
        if (pantryItems.isEmpty()) {
            tvEmptyPantry.setVisibility(View.VISIBLE);
            listPantry.setVisibility(View.GONE);
        } else {
            tvEmptyPantry.setVisibility(View.GONE);
            listPantry.setVisibility(View.VISIBLE);
            PantryAdapter adapter =
                new PantryAdapter(this, pantryItems);
            listPantry.setAdapter(adapter);

            // Tap to edit item
            listPantry.setOnItemClickListener((parent, view, position, id) -> {
                PantryItem selectedItem = pantryItems.get(position);
                Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
                );

                intent.putExtra("ITEM_ID", selectedItem.getId());
                intent.putExtra("ITEM_NAME", selectedItem.getName());
                intent.putExtra("ITEM_QUANTITY", selectedItem.getQuantity());
                intent.putExtra("ITEM_UNIT", selectedItem.getUnit());
                intent.putExtra("ITEM_EXPIRY", selectedItem.getExpiryDate());
                startActivity(intent);
            });

            // Long click to open the delete option
            listPantry.setOnItemLongClickListener((parent, view, position, id) -> {
                PantryItem selectedItem = pantryItems.get(position);

                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete Ingredient")
                    .setMessage("Delete " + selectedItem.getName() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        DatabaseHelper dbHelper =
                            new DatabaseHelper(MainActivity.this);
                        int result =
                            dbHelper.deletePantryItem(selectedItem.getId());

                        if (result > 0) {
                            Toast.makeText(
                                MainActivity.this,
                                "Ingredient deleted",
                                Toast.LENGTH_SHORT
                            ).show();
                            loadPantryItems();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
                return true;
            });
        }
    }
}