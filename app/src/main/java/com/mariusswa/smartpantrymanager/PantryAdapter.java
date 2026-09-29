package com.mariusswa.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

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

    TextView tvName =
        convertView.findViewById(R.id.tvPantryName);

    TextView tvQuantity =
        convertView.findViewById(R.id.tvPantryQuantity);

    TextView tvExpiry =
        convertView.findViewById(R.id.tvPantryExpiry);

    if (pantryItem != null) {

      tvName.setText(pantryItem.getName());

      tvQuantity.setText(
          formatQuantity(pantryItem.getQuantity())
              + " "
              + pantryItem.getUnit()
      );

      String expiryDate = pantryItem.getExpiryDate();

      if (expiryDate != null && !expiryDate.trim().isEmpty()) {
        tvExpiry.setText("Expires: " + expiryDate);
        tvExpiry.setVisibility(View.VISIBLE);
      } else {
        tvExpiry.setVisibility(View.GONE);
      }
    }

    return convertView;
  }

  private String formatQuantity(double quantity) {

    if (quantity == Math.floor(quantity)) {
      return String.valueOf((int) quantity);
    }

    return String.valueOf(quantity);
  }
}