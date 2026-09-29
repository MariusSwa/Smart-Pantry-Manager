package com.mariusswa.smartpantrymanager;
// Load all imports
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

// Database helper class to store and fetch data from the database
public class DatabaseHelper extends SQLiteOpenHelper {
  //  Database name
  private static final String DATABASE_NAME = "smart_pantry.db";
  // Use versioning to add table later and keep the data present
  private static final int DATABASE_VERSION = 5;

  // Pantry table to store ingredients
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
  //  Pantry table
    String createPantryTable =
        "CREATE TABLE " + TABLE_PANTRY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_QUANTITY + " REAL NOT NULL, " +
            COLUMN_UNIT + " TEXT NOT NULL, " +
            COLUMN_EXPIRY_DATE + " TEXT" +
            ")";
    // Execute the creation table
    db.execSQL(createPantryTable);

    //    Recipe Table
    String createRecipesTable =
        "CREATE TABLE " + TABLE_RECIPES + " (" +
            COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
            COLUMN_INSTRUCTIONS + " TEXT NOT NULL" +
            ")";
    // Create recipe table
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
    // Create Recipe ingredients table
    db.execSQL(createRecipeIngredientsTable);

    // Add the table seeds
    seedRecipes(db);
    seedPantry(db);
  }

  // Run the upgrade for the DB
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
    onCreate(db);
  }

  // Add pantry items
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

  // Recipe table strings
  public static final String TABLE_RECIPES = "recipes";
  public static final String COLUMN_RECIPE_ID = "id";
  public static final String COLUMN_RECIPE_NAME = "name";
  public static final String COLUMN_INSTRUCTIONS = "instructions";

  // Recipe ingredient table strings
  public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
  public static final String COLUMN_RECIPE_INGREDIENT_ID = "id";
  public static final String COLUMN_RECIPE_FOREIGN_ID = "recipe_id";
  public static final String COLUMN_INGREDIENT_NAME = "ingredient_name";
  public static final String COLUMN_REQUIRED_QUANTITY = "quantity";
  public static final String COLUMN_REQUIRED_UNIT = "unit";

  // Get all items from the pantry
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

  // Delete a pantry item
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

  // Update a pantry items
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

  // Add a recipe
  private long addRecipe(SQLiteDatabase db, String name, String instructions) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_RECIPE_NAME, name);
    values.put(COLUMN_INSTRUCTIONS, instructions);
    return db.insert(TABLE_RECIPES, null, values);
  }

  // Add recipe ingredients
  private void addRecipeIngredient(SQLiteDatabase db, long recipeId,
   String ingredientName, double quantity, String unit) {
      ContentValues values = new ContentValues();
      values.put(COLUMN_RECIPE_FOREIGN_ID, recipeId);
      values.put(COLUMN_INGREDIENT_NAME, ingredientName);
      values.put(COLUMN_REQUIRED_QUANTITY, quantity);
      values.put(COLUMN_REQUIRED_UNIT, unit);
      db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
  }

  // The database recipe seeds
  private void seedRecipes(SQLiteDatabase db) {
    long recipeId;

    // 1. Cheese and Ham Tart
    recipeId = addRecipe(
        db,
        "Cheese and Ham Tart",
        "Mix the butter, flour, salt, mustard and pepper. "
            + "Cook while stirring until golden. Add the milk gradually "
            + "and continue stirring. Remove from the heat and stir in "
            + "the remaining ingredients. Spoon into an ovenproof dish "
            + "and bake at 190°C for about 30 minutes."
    );

    addRecipeIngredient(db, recipeId, "butter", 12.5, "ml");
    addRecipeIngredient(db, recipeId, "flour", 25, "ml");
    addRecipeIngredient(db, recipeId, "milk", 250, "ml");
    addRecipeIngredient(db, recipeId, "ham", 250, "g");
    addRecipeIngredient(db, recipeId, "cheese", 250, "g");
    addRecipeIngredient(db, recipeId, "egg", 2, "item");

    // 2. Cheese and Bacon Tart
    recipeId = addRecipe(
        db,
        "Cheese and Bacon Tart",
        "Mix the butter, flour, salt, pepper and mustard over low heat. "
            + "Add the milk gradually while stirring. Add the remaining "
            + "ingredients, pour into an ovenproof dish and bake at "
            + "180°C for about 45 minutes."
    );

    addRecipeIngredient(db, recipeId, "butter", 50, "g");
    addRecipeIngredient(db, recipeId, "flour", 25, "g");
    addRecipeIngredient(db, recipeId, "milk", 500, "ml");
    addRecipeIngredient(db, recipeId, "bacon", 200, "g");
    addRecipeIngredient(db, recipeId, "cheese", 100, "g");
    addRecipeIngredient(db, recipeId, "egg", 2, "item");

    // 3. Cheese Meat Tart
    recipeId = addRecipe(
        db,
        "Cheese Meat Tart",
        "Mix the ingredients together and spread the mixture into a "
            + "greased ovenproof dish. Bake until cooked through "
            + "and golden."
    );

    addRecipeIngredient(db, recipeId, "minced meat", 500, "g");
    addRecipeIngredient(db, recipeId, "cheese", 250, "g");
    addRecipeIngredient(db, recipeId, "milk", 500, "ml");
    addRecipeIngredient(db, recipeId, "egg", 2, "item");

    // 4. Meat Pizza
    recipeId = addRecipe(
        db,
        "Meat Pizza",
        "Mix the minced meat, breadcrumbs, egg and seasoning. "
            + "Press into a greased baking dish to form the base. "
            + "Arrange the tomato and onion over the meat base, "
            + "top with cheese and bake until cooked and golden."
    );

    addRecipeIngredient(db, recipeId, "minced meat", 500, "g");
    addRecipeIngredient(db, recipeId, "breadcrumbs", 125, "ml");
    addRecipeIngredient(db, recipeId, "egg", 1, "item");
    addRecipeIngredient(db, recipeId, "tomato", 2, "item");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");
    addRecipeIngredient(db, recipeId, "cheese", 125, "g");

    // 5. Meat Pie
    recipeId = addRecipe(
        db,
        "Meat Pie",
        "Prepare the meat mixture and place it in an ovenproof dish. "
            + "Mix the topping ingredients, spread over the meat "
            + "and bake until the topping is golden."
    );

    addRecipeIngredient(db, recipeId, "minced meat", 500, "g");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");
    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "milk", 250, "ml");
    addRecipeIngredient(db, recipeId, "cheese", 100, "g");

    // 6. Cheese Pie
    recipeId = addRecipe(
        db,
        "Cheese Pie",
        "Mix the ingredients together. Pour into a greased ovenproof "
            + "dish and bake until set and golden brown."
    );

    addRecipeIngredient(db, recipeId, "cheese", 250, "g");
    addRecipeIngredient(db, recipeId, "milk", 250, "ml");
    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "flour", 125, "ml");

    // 7. Chicken Pie
    recipeId = addRecipe(
        db,
        "Chicken Pie",
        "Combine the cooked chicken with the remaining filling "
            + "ingredients. Place into an ovenproof dish and bake "
            + "until hot and golden."
    );

    addRecipeIngredient(db, recipeId, "chicken", 500, "g");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");
    addRecipeIngredient(db, recipeId, "milk", 250, "ml");
    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "cheese", 100, "g");

    // 8. Cheese and Onion Pie
    recipeId = addRecipe(
        db,
        "Cheese and Onion Pie",
        "Combine the cheese and onion mixture. Place into an "
            + "ovenproof dish and bake until cooked through "
            + "and golden."
    );

    addRecipeIngredient(db, recipeId, "cheese", 250, "g");
    addRecipeIngredient(db, recipeId, "onion", 2, "item");
    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "milk", 250, "ml");

    // 9. Mackerel Dish
    recipeId = addRecipe(
        db,
        "Mackerel Dish",
        "Combine the mackerel with the remaining ingredients. "
            + "Place the mixture in an ovenproof dish and bake "
            + "until heated through and set."
    );

    addRecipeIngredient(db, recipeId, "mackerel", 400, "g");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");
    addRecipeIngredient(db, recipeId, "egg", 2, "item");
    addRecipeIngredient(db, recipeId, "milk", 250, "ml");

    // 10. Ham Rolls
    recipeId = addRecipe(
        db,
        "Ham Rolls",
        "Prepare the filling and divide it between the slices of ham. "
            + "Roll the ham around the filling, arrange in an "
            + "ovenproof dish and bake until heated through."
    );

    addRecipeIngredient(db, recipeId, "ham", 250, "g");
    addRecipeIngredient(db, recipeId, "cheese", 100, "g");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");

    // 11. Meat Fritters
    recipeId = addRecipe(
        db,
        "Meat Fritters",
        "Mix the ingredients into a batter. Drop spoonfuls of the "
            + "mixture into hot oil and fry until cooked and "
            + "golden on both sides."
    );

    addRecipeIngredient(db, recipeId, "minced meat", 250, "g");
    addRecipeIngredient(db, recipeId, "flour", 125, "ml");
    addRecipeIngredient(db, recipeId, "egg", 1, "item");
    addRecipeIngredient(db, recipeId, "milk", 125, "ml");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");

    // 12. Cheese Fritters
    recipeId = addRecipe(
        db,
        "Cheese Fritters",
        "Mix the ingredients well. Spoon portions of the mixture "
            + "into hot oil and fry until golden brown."
    );

    addRecipeIngredient(db, recipeId, "cheese", 250, "g");
    addRecipeIngredient(db, recipeId, "flour", 125, "ml");
    addRecipeIngredient(db, recipeId, "egg", 1, "item");
    addRecipeIngredient(db, recipeId, "milk", 125, "ml");

    // 13. Chicken Fritters
    recipeId = addRecipe(
        db,
        "Chicken Fritters",
        "Combine the chicken with the batter ingredients. "
            + "Drop spoonfuls into hot oil and fry until "
            + "golden and cooked through."
    );

    addRecipeIngredient(db, recipeId, "chicken", 250, "g");
    addRecipeIngredient(db, recipeId, "flour", 125, "ml");
    addRecipeIngredient(db, recipeId, "egg", 1, "item");
    addRecipeIngredient(db, recipeId, "milk", 125, "ml");

    // 14. Cheese and Ham Fritters
    recipeId = addRecipe(
        db,
        "Cheese and Ham Fritters",
        "Mix all the ingredients together. Spoon portions into "
            + "hot oil and fry until golden brown."
    );

    addRecipeIngredient(db, recipeId, "cheese", 125, "g");
    addRecipeIngredient(db, recipeId, "ham", 125, "g");
    addRecipeIngredient(db, recipeId, "flour", 125, "ml");
    addRecipeIngredient(db, recipeId, "egg", 1, "item");
    addRecipeIngredient(db, recipeId, "milk", 125, "ml");

    // 15. Cheese and Bacon Fritters
    recipeId = addRecipe(
        db,
        "Cheese and Bacon Fritters",
        "Combine all ingredients. Drop spoonfuls into hot oil "
            + "and fry until crisp and golden."
    );

    addRecipeIngredient(db, recipeId, "cheese", 125, "g");
    addRecipeIngredient(db, recipeId, "bacon", 125, "g");
    addRecipeIngredient(db, recipeId, "flour", 125, "ml");
    addRecipeIngredient(db, recipeId, "egg", 1, "item");
    addRecipeIngredient(db, recipeId, "milk", 125, "ml");

    // 16. Savoury Meatballs
    recipeId = addRecipe(
        db,
        "Savoury Meatballs",
        "Combine the meat, egg and seasoning. Shape into balls "
            + "and cook until browned and cooked through."
    );

    addRecipeIngredient(db, recipeId, "minced meat", 500, "g");
    addRecipeIngredient(db, recipeId, "egg", 1, "item");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");
    addRecipeIngredient(db, recipeId, "breadcrumbs", 100, "ml");

    // 17. Cheese Meatballs
    recipeId = addRecipe(
        db,
        "Cheese Meatballs",
        "Mix the ingredients together, shape into small balls "
            + "and cook until browned and cooked through."
    );

    addRecipeIngredient(db, recipeId, "minced meat", 500, "g");
    addRecipeIngredient(db, recipeId, "cheese", 100, "g");
    addRecipeIngredient(db, recipeId, "egg", 1, "item");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");

    // 18. Bacon Meatballs
    recipeId = addRecipe(
        db,
        "Bacon Meatballs",
        "Combine the minced meat, bacon and remaining ingredients. "
            + "Shape into balls and cook until browned."
    );

    addRecipeIngredient(db, recipeId, "minced meat", 500, "g");
    addRecipeIngredient(db, recipeId, "bacon", 125, "g");
    addRecipeIngredient(db, recipeId, "egg", 1, "item");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");

    // 19. Weense Sausage Tart
    recipeId = addRecipe(
        db,
        "Vienna Sausage Tart",
        "Fry the onion in oil and add the milk. Bring to the boil. "
            + "Add the remaining ingredients, mix well and pour "
            + "into a greased ovenproof dish. Bake until set."
    );

    addRecipeIngredient(db, recipeId, "vienna sausage", 375, "g");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");
    addRecipeIngredient(db, recipeId, "milk", 285, "ml");
    addRecipeIngredient(db, recipeId, "tomato", 2, "item");
    addRecipeIngredient(db, recipeId, "cheese", 125, "g");
    addRecipeIngredient(db, recipeId, "egg", 1, "item");

    // 20. Aspic Meat Tart
    recipeId = addRecipe(
        db,
        "Aspic Meat Tart",
        "Mix the gelatin with cold water and dissolve it in boiling "
            + "water. Add the remaining ingredients. Pour into "
            + "a mould and refrigerate until set."
    );

    addRecipeIngredient(db, recipeId, "gelatin", 20, "g");
    addRecipeIngredient(db, recipeId, "water", 375, "ml");
    addRecipeIngredient(db, recipeId, "cooked meat", 500, "g");
    addRecipeIngredient(db, recipeId, "tomato", 2, "item");
    addRecipeIngredient(db, recipeId, "onion", 1, "item");
  }

  // Seed pantry items
  private void seedPantry(SQLiteDatabase db) {
    addSeedPantryItem(db, "Egg", 6, "item");
    addSeedPantryItem(db, "Milk", 2, "L");
    addSeedPantryItem(db, "Cheese", 500, "g");
    addSeedPantryItem(db, "Onion", 4, "item");
    addSeedPantryItem(db, "Minced meat", 1000, "g");
    addSeedPantryItem(db, "Bacon", 300, "g");
    addSeedPantryItem(db, "Tomato", 4, "item");
  }

  // Method to add pantry seeds
  private void addSeedPantryItem(
      SQLiteDatabase db,
      String name,
      double quantity,
      String unit) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_NAME, name);
    values.put(COLUMN_QUANTITY, quantity);
    values.put(COLUMN_UNIT, unit);
    db.insert(TABLE_PANTRY, null, values);
  }

  // Get all the recipes from the db
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

  // Get the recipe ingredients
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

  // Get suggested recipe list
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