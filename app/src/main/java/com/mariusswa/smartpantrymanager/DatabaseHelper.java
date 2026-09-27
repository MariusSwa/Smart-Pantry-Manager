package com.mariusswa.smartpantrymanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "smart_pantry.db";
  private static final int DATABASE_VERSION = 1;

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

    String createPantryTable =
        "CREATE TABLE " + TABLE_PANTRY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_QUANTITY + " REAL NOT NULL, " +
            COLUMN_UNIT + " TEXT NOT NULL, " +
            COLUMN_EXPIRY_DATE + " TEXT" +
            ")";

    db.execSQL(createPantryTable);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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

  public Cursor getAllPantryItems() {
    SQLiteDatabase db = this.getReadableDatabase();
    return db.query(
        TABLE_PANTRY,
        null,
        null,
        null,
        null,
        null,
        COLUMN_NAME + " ASC"
    );
  }
}