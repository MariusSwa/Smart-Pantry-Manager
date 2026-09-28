package com.mariusswa.smartpantrymanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Button;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import android.util.Log;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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
        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnAddIngredient.setOnClickListener(v -> {
            Intent intent =
                    new Intent(MainActivity.this, AddIngredientActivity.class);
            startActivity(intent);
        });

        Button btnSuggestedRecipes =
            findViewById(R.id.btnSuggestedRecipes);
        btnSuggestedRecipes.setOnClickListener(v -> {

            Intent intent = new Intent(
                MainActivity.this,
                SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        List<Recipe> recipes = databaseHelper.getAllRecipes();

        Log.d("RECIPE_TEST", "Number of recipes: " + recipes.size());

        for (Recipe recipe : recipes) {
            Log.d("RECIPE_TEST", recipe.getName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        List<Recipe> suggestedRecipes =
            databaseHelper.getSuggestedRecipes();

        Log.d(
            "MATCH_TEST",
            "Suggested recipes: " + suggestedRecipes.size()
        );

        for (Recipe recipe : suggestedRecipes) {
            Log.d("MATCH_TEST", recipe.getName());
        }

    }

    private void loadPantryItems() {
        TextView tvEmptyPantry = findViewById(R.id.tvEmptyPantry);
        ListView listPantry = findViewById(R.id.listPantry);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<PantryItem> pantryItems =
            databaseHelper.getAllPantryItems();

        if (pantryItems.isEmpty()) {
            tvEmptyPantry.setVisibility(View.VISIBLE);
            listPantry.setVisibility(View.GONE);
        } else {
            tvEmptyPantry.setVisibility(View.GONE);
            listPantry.setVisibility(View.VISIBLE);
            ArrayAdapter<PantryItem> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                pantryItems
            );

            listPantry.setAdapter(adapter);

//          Tap to edit item
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