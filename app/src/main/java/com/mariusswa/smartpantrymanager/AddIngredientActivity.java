package com.mariusswa.smartpantrymanager;

// Imports to use
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
import android.widget.TextView;

// This page is where we can load items into our pantry that we have left or bought

public class AddIngredientActivity extends AppCompatActivity {

    // Declare our text inputs
    private EditText etIngredientName;
    private EditText etQuantity;
    private EditText etUnit;
    private EditText etExpiryDate;
    private int editingItemId = -1;

    // On create creates the layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view of the page
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_ingredient);

        // Leave a space for the system bars
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

        // Use the elements we created in the xml
        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        editingItemId = getIntent().getIntExtra("ITEM_ID", -1);
        etExpiryDate.setOnClickListener(v -> showDatePicker());
        Button btnSaveIngredient = findViewById(R.id.btnSaveIngredient);
        TextView tvTitle = findViewById(R.id.tvTitle);

        // Save / Update ingredient button

        // If we add an ingredient
        if (editingItemId != -1) {
            // Update the screen for edit
            tvTitle.setText("Edit Ingredient");
            btnSaveIngredient.setText("Update Ingredient");
            String name = getIntent().getStringExtra("ITEM_NAME");
            double quantity = getIntent().getDoubleExtra("ITEM_QUANTITY", 0);
            String unit = getIntent().getStringExtra("ITEM_UNIT");
            String expiryDate = getIntent().getStringExtra("ITEM_EXPIRY");

            // Get the ingredient details
            etIngredientName.setText(name);
            etQuantity.setText(String.valueOf(quantity));
            etUnit.setText(unit);

            // Set the expiry date when it is present
            if (expiryDate != null) {
                etExpiryDate.setText(expiryDate);
            }
            btnSaveIngredient.setText(R.string.update_ingredient);
        }

        // Cancel button
        Button btnCancel = findViewById(R.id.btnCancel);
        // Click listeners
        btnSaveIngredient.setOnClickListener(v -> validateIngredient());
        btnCancel.setOnClickListener(v -> finish());
    }

    // A date picker for the expiry date
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

    // Validation on the input fields
    private void validateIngredient() {
        // Trim white spaces
        String name = etIngredientName.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();

        // If the name is empty
        if (name.isEmpty()) {
            etIngredientName.setError("Ingredient name is required");
            etIngredientName.requestFocus();
            return;
        }

        // If the qty is empty
        if (quantityText.isEmpty()) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return;
        }

        // Number check on qty
        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid quantity");
            etQuantity.requestFocus();
            return;
        }

        // Qty must be more than 0
        if (quantity <= 0) {
            etQuantity.setError("Quantity must be greater than 0");
            etQuantity.requestFocus();
            return;
        }

        // Unit must be entered
        if (unit.isEmpty()) {
            etUnit.setError("Unit is required");
            etUnit.requestFocus();
            return;
        }

        // Trim expiry date
        String expiryDate = etExpiryDate.getText().toString().trim();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Add ingredient to DB
        if (editingItemId == -1) {
            long result = databaseHelper.addPantryItem(
                name,
                quantity,
                unit,
                expiryDate
            );

            // If it saved
            if (result != -1) {
                Toast.makeText(
                    this,
                    "Ingredient saved",
                    Toast.LENGTH_SHORT
                ).show();
                finish();
                // It did not save
            } else {
                Toast.makeText(
                    this,
                    "Could not save ingredient",
                    Toast.LENGTH_SHORT
                ).show();
            }
            // Update the ingredient
        } else {
            int result = databaseHelper.updatePantryItem(
                editingItemId,
                name,
                quantity,
                unit,
                expiryDate
            );

            // If it saved
            if (result > 0) {
                Toast.makeText(
                    this,
                    "Ingredient updated",
                    Toast.LENGTH_SHORT
                ).show();
                finish();
                //If it did not save
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