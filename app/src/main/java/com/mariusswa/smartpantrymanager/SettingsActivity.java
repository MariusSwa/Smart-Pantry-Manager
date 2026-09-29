package com.mariusswa.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

  public static final String PREFS_NAME = "SmartPantrySettings";
  public static final String KEY_SHOW_EXPIRY = "showExpiry";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_settings);

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

    Switch switchShowExpiry =
        findViewById(R.id.switchShowExpiry);

    SharedPreferences preferences =
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

    boolean showExpiry =
        preferences.getBoolean(KEY_SHOW_EXPIRY, true);

    switchShowExpiry.setChecked(showExpiry);

    switchShowExpiry.setOnCheckedChangeListener(
        (buttonView, isChecked) ->
            preferences.edit()
                .putBoolean(KEY_SHOW_EXPIRY, isChecked)
                .apply()
    );

    Button btnBack = findViewById(R.id.btnBack);

    btnBack.setOnClickListener(v -> finish());
  }
}