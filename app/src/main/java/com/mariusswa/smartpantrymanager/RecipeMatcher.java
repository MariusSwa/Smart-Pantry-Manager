package com.mariusswa.smartpantrymanager;
// Imports
import java.util.List;

// File used for strict matching
public class RecipeMatcher {

  // Checks every ingredient in the pantry against the recipes
  public static boolean canMakeRecipe(
      Recipe recipe,
      List<RecipeIngredient> requiredIngredients,
      List<PantryItem> pantryItems) {

    // Check ingredients required by a recipe
    for (RecipeIngredient required : requiredIngredients) {
      boolean ingredientFound = false;
      for (PantryItem pantryItem : pantryItems) {
        // check if the ingredient name matches
        if (namesMatch(
            pantryItem.getName(),
            required.getIngredientName())) {
          // simplifies ingredient quantity
          double pantryQuantity = convertQuantity(
              pantryItem.getQuantity(),
              pantryItem.getUnit(),
              required.getUnit()
          );

          // check if there is enough of the ingredient
          if (pantryQuantity >= required.getQuantity()) {
            ingredientFound = true;
            break;
          }
        }
      }

      // rejects the recipe if a ingredient is not enough
      if (!ingredientFound) {
        return false;
      }
    }

    // the ingredient is there and has enough qty
    return true;
  }

  // normalise ingredient names for plurals
  private static boolean namesMatch(String pantryName, String requiredName) {
    String pantry = normalizeName(pantryName);
    String required = normalizeName(requiredName);
    return pantry.equals(required);
  }

  // convert to lower case, remove spaces
  private static String normalizeName(String name) {
    String normalized = name
        .trim()
        .toLowerCase();

    if (normalized.endsWith("es") && normalized.length() > 2) {
      normalized =
          normalized.substring(0, normalized.length() - 2);
    } else if (normalized.endsWith("s") && normalized.length() > 1) {
      normalized =
          normalized.substring(0, normalized.length() - 1);
    }

    return normalized;
  }

  // Check for compatible units
  private static double convertQuantity(
      double quantity,
      String fromUnit,
      String toUnit) {

    String from = fromUnit.trim().toLowerCase();
    String to = toUnit.trim().toLowerCase();

    if (from.equals(to)) {
      return quantity;
    }

    // Kilograms to grams
    if (from.equals("kg") && to.equals("g")) {
      return quantity * 1000;
    }

    // Grams to kilograms
    if (from.equals("g") && to.equals("kg")) {
      return quantity / 1000;
    }

    // Litres to millilitres
    if ((from.equals("l") || from.equals("litre") || from.equals("liter")) && to.equals("ml")) {
      return quantity * 1000;
    }

    // Millilitres to litres
    if (from.equals("ml") && (to.equals("l") || to.equals("litre") || to.equals("liter"))) {
      return quantity / 1000;
    }

    // Common item terminology
    if ((from.equals("item")
        || from.equals("items")
        || from.equals("piece")
        || from.equals("pieces"))
        && to.equals("item")) {
      return quantity;
    }

    // Common slice terminology
    if ((from.equals("slice") || from.equals("slices"))
        && to.equals("slice")) {
      return quantity;
    }

    // Units cannot safely be converted
    return -1;
  }
}