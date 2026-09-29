package com.mariusswa.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_suggested_recipes);

    ViewCompat.setOnApplyWindowInsetsListener(
        findViewById(R.id.main),
        (v, insets) -> {

          Insets systemBars =
              insets.getInsets(
                  WindowInsetsCompat.Type.systemBars()
              );

          v.setPadding(
              systemBars.left,
              systemBars.top,
              systemBars.right,
              systemBars.bottom
          );

          return insets;
        }
    );
    loadSuggestedRecipes();

    Button btnBack = findViewById(R.id.btnBack);

    btnBack.setOnClickListener(v -> finish());
  }

  private void loadSuggestedRecipes() {
    TextView tvNoRecipes =
        findViewById(R.id.tvNoRecipes);
    ListView listRecipes =
        findViewById(R.id.listRecipes);
    DatabaseHelper databaseHelper =
        new DatabaseHelper(this);
    List<Recipe> recipes =
        databaseHelper.getSuggestedRecipes();
    if (recipes.isEmpty()) {
      tvNoRecipes.setVisibility(View.VISIBLE);
      listRecipes.setVisibility(View.GONE);
    } else {
      tvNoRecipes.setVisibility(View.GONE);
      listRecipes.setVisibility(View.VISIBLE);

      ArrayAdapter<Recipe> adapter =
          new ArrayAdapter<>(
              this,
              android.R.layout.simple_list_item_1,
              recipes
          );

      listRecipes.setAdapter(adapter);
      listRecipes.setOnItemClickListener((parent, view, position, id) -> {

        Recipe selectedRecipe = recipes.get(position);

        Intent intent = new Intent(
            SuggestedRecipesActivity.this,
            RecipeDetailActivity.class
        );

        intent.putExtra("RECIPE_ID", selectedRecipe.getId());
        intent.putExtra("RECIPE_NAME", selectedRecipe.getName());
        intent.putExtra(
            "RECIPE_INSTRUCTIONS",
            selectedRecipe.getInstructions()
        );

        startActivity(intent);
      });
    }
  }
}