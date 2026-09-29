package com.mariusswa.smartpantrymanager;
// Imports
import java.util.List;

public class RecipeMatcher {

  public static boolean canMakeRecipe(
      Recipe recipe,
      List<RecipeIngredient> requiredIngredients,
      List<PantryItem> pantryItems) {

    for (RecipeIngredient required : requiredIngredients) {

      boolean ingredientFound = false;

      for (PantryItem pantryItem : pantryItems) {

        if (namesMatch(
            pantryItem.getName(),
            required.getIngredientName())) {

          double pantryQuantity = convertQuantity(
              pantryItem.getQuantity(),
              pantryItem.getUnit(),
              required.getUnit()
          );

          if (pantryQuantity >= required.getQuantity()) {
            ingredientFound = true;
            break;
          }
        }
      }

      if (!ingredientFound) {
        return false;
      }
    }

    return true;
  }

  private static boolean namesMatch(String pantryName, String requiredName) {

    String pantry = normalizeName(pantryName);
    String required = normalizeName(requiredName);

    return pantry.equals(required);
  }

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
    if ((from.equals("l") || from.equals("litre") || from.equals("liter"))
        && to.equals("ml")) {

      return quantity * 1000;
    }

    // Millilitres to litres
    if (from.equals("ml")
        && (to.equals("l")
        || to.equals("litre")
        || to.equals("liter"))) {
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