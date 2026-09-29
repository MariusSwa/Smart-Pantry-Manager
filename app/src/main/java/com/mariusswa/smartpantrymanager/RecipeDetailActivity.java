package com.mariusswa.smartpantrymanager;

// Imports to use
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_recipe_detail);

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

    TextView tvRecipeName = findViewById(R.id.tvRecipeName);
    TextView tvIngredients = findViewById(R.id.tvIngredients);
    TextView tvInstructions = findViewById(R.id.tvInstructions);

    Button btnBack = findViewById(R.id.btnBack);
    btnBack.setOnClickListener(v -> finish());

    int recipeId = getIntent().getIntExtra("RECIPE_ID", -1);
    String recipeName = getIntent().getStringExtra("RECIPE_NAME");
    String instructions = getIntent().getStringExtra("RECIPE_INSTRUCTIONS");

    tvRecipeName.setText(recipeName);
    tvInstructions.setText(instructions);


    DatabaseHelper databaseHelper = new DatabaseHelper(this);

    List<RecipeIngredient> ingredients =
        databaseHelper.getRecipeIngredients(recipeId);

    StringBuilder ingredientText = new StringBuilder();

    for (RecipeIngredient ingredient : ingredients) {

      ingredientText
          .append("• ")
          .append(ingredient.getIngredientName())
          .append(" - ")
          .append(ingredient.getQuantity())
          .append(" ")
          .append(ingredient.getUnit())
          .append("\n");
    }
    tvIngredients.setText(ingredientText.toString());
  }

}