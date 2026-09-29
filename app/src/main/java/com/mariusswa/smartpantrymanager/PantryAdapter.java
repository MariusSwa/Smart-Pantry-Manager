package com.mariusswa.smartpantrymanager;
// Imports
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

// Custom pantry adapter
public class PantryAdapter extends ArrayAdapter<PantryItem> {
  public PantryAdapter(Context context, List<PantryItem> pantryItems) {
    super(context, 0, pantryItems);
  }


  @NonNull
  @Override
  public View getView(
      int position,
      @Nullable View convertView,
      @NonNull ViewGroup parent) {

    PantryItem pantryItem = getItem(position);

    if (convertView == null) {
      convertView = LayoutInflater.from(getContext())
          .inflate(R.layout.item_pantry, parent, false);
    }

    // Item name
    TextView tvName =
        convertView.findViewById(R.id.tvPantryName);

    // Item qty
    TextView tvQuantity =
        convertView.findViewById(R.id.tvPantryQuantity);

    // Item expiry date
    TextView tvExpiry =
        convertView.findViewById(R.id.tvPantryExpiry);

    // If an item get unit and qty
    if (pantryItem != null) {
      tvName.setText(pantryItem.getName());
      tvQuantity.setText(
          formatQuantity(pantryItem.getQuantity())
              + " "
              + pantryItem.getUnit()
      );

      SharedPreferences preferences =
          getContext().getSharedPreferences(
              SettingsActivity.PREFS_NAME,
              Context.MODE_PRIVATE
          );

      // Show exipry date from settings
      boolean showExpiry =
          preferences.getBoolean(
              SettingsActivity.KEY_SHOW_EXPIRY,
              true
          );

      String expiryDate = pantryItem.getExpiryDate();
      // Show expiry date if it has one
      if (showExpiry
          && expiryDate != null
          && !expiryDate.trim().isEmpty()) {
        tvExpiry.setText("Expires: " + expiryDate);
        tvExpiry.setVisibility(View.VISIBLE);
      } else {
        tvExpiry.setVisibility(View.GONE);
      }
    }
    // Return the view to the page
    return convertView;
  }

  // Format the qty of an item
  private String formatQuantity(double quantity) {

    if (quantity == Math.floor(quantity)) {
      return String.valueOf((int) quantity);
    }
    return String.valueOf(quantity);
  }
}