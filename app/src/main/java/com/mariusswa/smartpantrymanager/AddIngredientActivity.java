package com.mariusswa.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.Locale;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText etIngredientName;
    private EditText etQuantity;
    private EditText etUnit;
    private EditText etExpiryDate;
    private int editingItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_ingredient);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            );
            return insets;
        });

        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etUnit = findViewById(R.id.etUnit);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        editingItemId = getIntent().getIntExtra("ITEM_ID", -1);
        etExpiryDate.setOnClickListener(v -> showDatePicker());

        // Multi option button for save and update
        Button btnSaveIngredient = findViewById(R.id.btnSaveIngredient);

        if (editingItemId != -1) {

            String name = getIntent().getStringExtra("ITEM_NAME");
            double quantity = getIntent().getDoubleExtra("ITEM_QUANTITY", 0);
            String unit = getIntent().getStringExtra("ITEM_UNIT");
            String expiryDate = getIntent().getStringExtra("ITEM_EXPIRY");

            etIngredientName.setText(name);
            etQuantity.setText(String.valueOf(quantity));
            etUnit.setText(unit);

            if (expiryDate != null) {
                etExpiryDate.setText(expiryDate);
            }
            btnSaveIngredient.setText("Update Ingredient");
        }


        Button btnCancel = findViewById(R.id.btnCancel);
        btnSaveIngredient.setOnClickListener(v -> validateIngredient());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog =
            new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    String selectedDate = String.format(
                        Locale.getDefault(),
                        "%04d-%02d-%02d",
                        year,
                        month + 1,
                        dayOfMonth
                    );

                    etExpiryDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );

        datePickerDialog.show();
    }

    private void validateIngredient() {
        String name = etIngredientName.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();

        if (name.isEmpty()) {
            etIngredientName.setError("Ingredient name is required");
            etIngredientName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid quantity");
            etQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            etQuantity.setError("Quantity must be greater than 0");
            etQuantity.requestFocus();
            return;
        }

        if (unit.isEmpty()) {
            etUnit.setError("Unit is required");
            etUnit.requestFocus();
            return;
        }

        String expiryDate = etExpiryDate.getText().toString().trim();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        if (editingItemId == -1) {
            long result = databaseHelper.addPantryItem(
                name,
                quantity,
                unit,
                expiryDate
            );

            if (result != -1) {
                Toast.makeText(
                    this,
                    "Ingredient saved",
                    Toast.LENGTH_SHORT
                ).show();
                finish();
            } else {
                Toast.makeText(
                    this,
                    "Could not save ingredient",
                    Toast.LENGTH_SHORT
                ).show();
            }
        } else {
            int result = databaseHelper.updatePantryItem(
                editingItemId,
                name,
                quantity,
                unit,
                expiryDate
            );

            if (result > 0) {
                Toast.makeText(
                    this,
                    "Ingredient updated",
                    Toast.LENGTH_SHORT
                ).show();
                finish();
            } else {
                Toast.makeText(
                    this,
                    "Could not update ingredient",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}