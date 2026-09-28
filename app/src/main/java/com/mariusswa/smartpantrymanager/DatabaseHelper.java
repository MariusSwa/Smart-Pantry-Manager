package com.mariusswa.smartpantrymanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

  //  Database name
  private static final String DATABASE_NAME = "smart_pantry.db";
  // Use versioning to add table later and keep the data present
  private static final int DATABASE_VERSION = 3;

  public static final String TABLE_PANTRY = "pantry";

  public static final String COLUMN_ID = "id";
  public static final String COLUMN_NAME = "name";
  public static final String COLUMN_QUANTITY = "quantity";
  public static final String COLUMN_UNIT = "unit";
  public static final String COLUMN_EXPIRY_DATE = "expiry_date";

  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
  //  Patnry table
    String createPantryTable =
        "CREATE TABLE " + TABLE_PANTRY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_QUANTITY + " REAL NOT NULL, " +
            COLUMN_UNIT + " TEXT NOT NULL, " +
            COLUMN_EXPIRY_DATE + " TEXT" +
            ")";

    db.execSQL(createPantryTable);
    //    Recipe Table
    String createRecipesTable =
        "CREATE TABLE " + TABLE_RECIPES + " (" +
            COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
            COLUMN_INSTRUCTIONS + " TEXT NOT NULL" +
            ")";

    db.execSQL(createRecipesTable);

    //  Ingredients Table
    String createRecipeIngredientsTable =
        "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
            COLUMN_RECIPE_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_RECIPE_FOREIGN_ID + " INTEGER NOT NULL, " +
            COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, " +
            COLUMN_REQUIRED_QUANTITY + " REAL NOT NULL, " +
            COLUMN_REQUIRED_UNIT + " TEXT NOT NULL, " +
            "FOREIGN KEY (" + COLUMN_RECIPE_FOREIGN_ID + ") REFERENCES " +
            TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ")" +
            ")";

    db.execSQL(createRecipeIngredientsTable);

    seedRecipes(db);
  }

  //  Runs the upgrade for the DB
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
    onCreate(db);
  }

  public long addPantryItem(String name, double quantity, String unit, String expiryDate) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_NAME, name);
    values.put(COLUMN_QUANTITY, quantity);
    values.put(COLUMN_UNIT, unit);

    if (expiryDate == null || expiryDate.trim().isEmpty()) {
      values.putNull(COLUMN_EXPIRY_DATE);
    } else {
      values.put(COLUMN_EXPIRY_DATE, expiryDate);
    }
    long result = db.insert(TABLE_PANTRY, null, values);
    db.close();
    return result;
  }

  public static final String TABLE_RECIPES = "recipes";
  public static final String COLUMN_RECIPE_ID = "id";
  public static final String COLUMN_RECIPE_NAME = "name";
  public static final String COLUMN_INSTRUCTIONS = "instructions";

  public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
  public static final String COLUMN_RECIPE_INGREDIENT_ID = "id";
  public static final String COLUMN_RECIPE_FOREIGN_ID = "recipe_id";
  public static final String COLUMN_INGREDIENT_NAME = "ingredient_name";
  public static final String COLUMN_REQUIRED_QUANTITY = "quantity";
  public static final String COLUMN_REQUIRED_UNIT = "unit";

  public List<PantryItem> getAllPantryItems() {
    List<PantryItem> pantryItems = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.query(
        TABLE_PANTRY,
        null,
        null,
        null,
        null,
        null,
        COLUMN_NAME + " ASC"
    );

    if (cursor.moveToFirst()) {
      do {
        int id = cursor.getInt(
            cursor.getColumnIndexOrThrow(COLUMN_ID)
        );

        String name = cursor.getString(
            cursor.getColumnIndexOrThrow(COLUMN_NAME)
        );

        double quantity = cursor.getDouble(
            cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)
        );

        String unit = cursor.getString(
            cursor.getColumnIndexOrThrow(COLUMN_UNIT)
        );

        String expiryDate = cursor.getString(
            cursor.getColumnIndexOrThrow(COLUMN_EXPIRY_DATE)
        );

        PantryItem item = new PantryItem(
            id,
            name,
            quantity,
            unit,
            expiryDate
        );

        pantryItems.add(item);
      } while (cursor.moveToNext());
    }

    cursor.close();
    return pantryItems;
  }

  public int deletePantryItem(int id) {
    SQLiteDatabase db = this.getWritableDatabase();
    int result = db.delete(
        TABLE_PANTRY,
        COLUMN_ID + " = ?",
        new String[]{String.valueOf(id)}
    );
    db.close();
    return result;
  }

  public int updatePantryItem(int id, String name, double quantity,
                              String unit, String expiryDate) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_NAME, name);
    values.put(COLUMN_QUANTITY, quantity);
    values.put(COLUMN_UNIT, unit);

    if (expiryDate == null || expiryDate.trim().isEmpty()) {
      values.putNull(COLUMN_EXPIRY_DATE);
    } else {
      values.put(COLUMN_EXPIRY_DATE, expiryDate);
    }

    int result = db.update(
        TABLE_PANTRY,
        values,
        COLUMN_ID + " = ?",
        new String[]{String.valueOf(id)}
    );
    db.close();
    return result;
  }

  private long addRecipe(SQLiteDatabase db, String name, String instructions) {

    ContentValues values = new ContentValues();
    values.put(COLUMN_RECIPE_NAME, name);
    values.put(COLUMN_INSTRUCTIONS, instructions);

    return db.insert(TABLE_RECIPES, null, values);
  }

  private void addRecipeIngredient(SQLiteDatabase db, long recipeId,
                                   String ingredientName, double quantity,
                                   String unit) {

    ContentValues values = new ContentValues();
    values.put(COLUMN_RECIPE_FOREIGN_ID, recipeId);
    values.put(COLUMN_INGREDIENT_NAME, ingredientName);
    values.put(COLUMN_REQUIRED_QUANTITY, quantity);
    values.put(COLUMN_REQUIRED_UNIT, unit);

    db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
  }

  private void seedRecipes(SQLiteDatabase db) {

    long recipeId;

    // 1. Scrambled Eggs
    recipeId = addRecipe(db,
        "Scrambled Eggs",
        "Beat the eggs with milk. Melt butter in a pan. " +
            "Add the egg mixture and cook while stirring until set.");

    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "milk", 50, "ml");
    addRecipeIngredient(db, recipeId, "butter", 10, "g");


    // 2. Cheese Omelette
    recipeId = addRecipe(db,
        "Cheese Omelette",
        "Beat the eggs. Melt butter in a pan and add the eggs. " +
            "Add cheese, fold the omelette and cook until done.");

    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "cheese", 50, "g");
    addRecipeIngredient(db, recipeId, "butter", 10, "g");


    // 3. Pancakes
    recipeId = addRecipe(db,
        "Pancakes",
        "Mix flour, milk and eggs into a smooth batter. " +
            "Cook portions of batter in a lightly buttered pan.");

    addRecipeIngredient(db, recipeId, "flour", 200, "g");
    addRecipeIngredient(db, recipeId, "milk", 300, "ml");
    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "butter", 20, "g");


    // 4. French Toast
    recipeId = addRecipe(db,
        "French Toast",
        "Beat eggs and milk together. Dip bread into the mixture " +
            "and fry in butter until golden on both sides.");

    addRecipeIngredient(db, recipeId, "bread", 4, "slice");
    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "milk", 100, "ml");
    addRecipeIngredient(db, recipeId, "butter", 20, "g");


    // 5. Grilled Cheese
    recipeId = addRecipe(db,
        "Grilled Cheese",
        "Place cheese between slices of bread. Butter the outside " +
            "and fry until golden and the cheese has melted.");

    addRecipeIngredient(db, recipeId, "bread", 2, "slice");
    addRecipeIngredient(db, recipeId, "cheese", 50, "g");
    addRecipeIngredient(db, recipeId, "butter", 10, "g");


    // 6. Tomato Pasta
    recipeId = addRecipe(db,
        "Tomato Pasta",
        "Cook the pasta. Fry onion in oil, add tomato and simmer. " +
            "Combine the sauce with the cooked pasta.");

    addRecipeIngredient(db, recipeId, "pasta", 200, "g");
    addRecipeIngredient(db, recipeId, "tomato", 2, "item");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");
    addRecipeIngredient(db, recipeId, "oil", 15, "ml");


    // 7. Cheese Pasta
    recipeId = addRecipe(db,
        "Cheese Pasta",
        "Cook the pasta. Stir in milk and grated cheese over low heat " +
            "until the cheese melts and forms a sauce.");

    addRecipeIngredient(db, recipeId, "pasta", 200, "g");
    addRecipeIngredient(db, recipeId, "cheese", 100, "g");
    addRecipeIngredient(db, recipeId, "milk", 100, "ml");


    // 8. Egg Fried Rice
    recipeId = addRecipe(db,
        "Egg Fried Rice",
        "Fry onion in oil. Add cooked rice and stir well. " +
            "Add beaten eggs and cook until the eggs are set.");

    addRecipeIngredient(db, recipeId, "rice", 250, "g");
    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");
    addRecipeIngredient(db, recipeId, "oil", 15, "ml");


    // 9. Tomato Rice
    recipeId = addRecipe(db,
        "Tomato Rice",
        "Cook the rice. Fry onion and tomato in oil, then mix " +
            "with the cooked rice.");

    addRecipeIngredient(db, recipeId, "rice", 250, "g");
    addRecipeIngredient(db, recipeId, "tomato", 2, "item");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");
    addRecipeIngredient(db, recipeId, "oil", 15, "ml");


    // 10. Mashed Potatoes
    recipeId = addRecipe(db,
        "Mashed Potatoes",
        "Boil potatoes until soft. Drain and mash with milk " +
            "and butter until smooth.");

    addRecipeIngredient(db, recipeId, "potato", 4, "item");
    addRecipeIngredient(db, recipeId, "milk", 100, "ml");
    addRecipeIngredient(db, recipeId, "butter", 30, "g");


    // 11. Potato and Egg Hash
    recipeId = addRecipe(db,
        "Potato and Egg Hash",
        "Dice the potatoes and fry with onion in oil until tender. " +
            "Add eggs and cook until set.");

    addRecipeIngredient(db, recipeId, "potato", 3, "item");
    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");
    addRecipeIngredient(db, recipeId, "oil", 20, "ml");


    // 12. Tomato Omelette
    recipeId = addRecipe(db,
        "Tomato Omelette",
        "Beat the eggs. Fry chopped tomato briefly in butter, " +
            "add eggs and cook until set.");

    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "tomato", 1, "item");
    addRecipeIngredient(db, recipeId, "butter", 10, "g");


    // 13. Cheese Toast
    recipeId = addRecipe(db,
        "Cheese Toast",
        "Place grated cheese on bread and toast until the bread " +
            "is crisp and the cheese has melted.");

    addRecipeIngredient(db, recipeId, "bread", 2, "slice");
    addRecipeIngredient(db, recipeId, "cheese", 60, "g");


    // 14. Tomato Cheese Toast
    recipeId = addRecipe(db,
        "Tomato Cheese Toast",
        "Top bread with sliced tomato and cheese. Toast until " +
            "the cheese is melted.");

    addRecipeIngredient(db, recipeId, "bread", 2, "slice");
    addRecipeIngredient(db, recipeId, "tomato", 1, "item");
    addRecipeIngredient(db, recipeId, "cheese", 60, "g");


    // 15. Simple Rice
    recipeId = addRecipe(db,
        "Simple Rice",
        "Rinse the rice and cook in water until tender. " +
            "Drain any excess water before serving.");

    addRecipeIngredient(db, recipeId, "rice", 200, "g");


    // 16. Boiled Eggs
    recipeId = addRecipe(db,
        "Boiled Eggs",
        "Place eggs in water, bring to the boil and cook until " +
            "the desired firmness is reached.");

    addRecipeIngredient(db, recipeId, "egg", 2, "item");


    // 17. Buttered Pasta
    recipeId = addRecipe(db,
        "Buttered Pasta",
        "Cook pasta until tender, drain and stir through butter.");

    addRecipeIngredient(db, recipeId, "pasta", 200, "g");
    addRecipeIngredient(db, recipeId, "butter", 20, "g");


    // 18. Fried Potatoes
    recipeId = addRecipe(db,
        "Fried Potatoes",
        "Slice the potatoes and fry in oil until golden and tender.");

    addRecipeIngredient(db, recipeId, "potato", 3, "item");
    addRecipeIngredient(db, recipeId, "oil", 30, "ml");


    // 19. Tomato and Onion Salad
    recipeId = addRecipe(db,
        "Tomato and Onion Salad",
        "Slice the tomatoes and onion. Combine and drizzle with oil.");

    addRecipeIngredient(db, recipeId, "tomato", 2, "item");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");
    addRecipeIngredient(db, recipeId, "oil", 10, "ml");


    // 20. Cheese Scrambled Eggs
    recipeId = addRecipe(db,
        "Cheese Scrambled Eggs",
        "Beat the eggs with milk. Cook gently in butter, " +
            "then stir in cheese before serving.");

    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "milk", 50, "ml");
    addRecipeIngredient(db, recipeId, "butter", 10, "g");
    addRecipeIngredient(db, recipeId, "cheese", 40, "g");
  }

  public List<Recipe> getAllRecipes() {

    List<Recipe> recipes = new ArrayList<>();

    SQLiteDatabase db = this.getReadableDatabase();

    Cursor cursor = db.query(
        TABLE_RECIPES,
        null,
        null,
        null,
        null,
        null,
        COLUMN_RECIPE_NAME + " ASC"
    );

    if (cursor.moveToFirst()) {
      do {
        int id = cursor.getInt(
            cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID)
        );

        String name = cursor.getString(
            cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME)
        );

        String instructions = cursor.getString(
            cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTIONS)
        );

        recipes.add(new Recipe(id, name, instructions));

      } while (cursor.moveToNext());
    }
    cursor.close();
    return recipes;
  }

  public List<RecipeIngredient> getRecipeIngredients(int recipeId) {

    List<RecipeIngredient> ingredients = new ArrayList<>();

    SQLiteDatabase db = this.getReadableDatabase();

    Cursor cursor = db.query(
        TABLE_RECIPE_INGREDIENTS,
        null,
        COLUMN_RECIPE_FOREIGN_ID + " = ?",
        new String[]{String.valueOf(recipeId)},
        null,
        null,
        null
    );

    if (cursor.moveToFirst()) {

      do {

        int id = cursor.getInt(
            cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INGREDIENT_ID)
        );

        String ingredientName = cursor.getString(
            cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_NAME)
        );

        double quantity = cursor.getDouble(
            cursor.getColumnIndexOrThrow(COLUMN_REQUIRED_QUANTITY)
        );

        String unit = cursor.getString(
            cursor.getColumnIndexOrThrow(COLUMN_REQUIRED_UNIT)
        );

        ingredients.add(
            new RecipeIngredient(
                id,
                recipeId,
                ingredientName,
                quantity,
                unit
            )
        );

      } while (cursor.moveToNext());
    }
    cursor.close();
    return ingredients;
  }

  public List<Recipe> getSuggestedRecipes() {
    List<Recipe> suggestedRecipes = new ArrayList<>();
    List<Recipe> allRecipes = getAllRecipes();
    List<PantryItem> pantryItems = getAllPantryItems();

    for (Recipe recipe : allRecipes) {
      List<RecipeIngredient> requiredIngredients =
          getRecipeIngredients(recipe.getId());
      boolean canMake = RecipeMatcher.canMakeRecipe(
          recipe,
          requiredIngredients,
          pantryItems
      );

      if (canMake) {
        suggestedRecipes.add(recipe);
      }
    }
    return suggestedRecipes;
  }
}