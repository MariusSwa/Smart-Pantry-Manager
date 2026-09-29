package com.mariusswa.smartpantrymanager;

// used to store one pantry ingedient
public class PantryItem {

  private int id;
  private String name;
  private double quantity;
  private String unit;
  private String expiryDate;

  // Pantry item creation
  public PantryItem(int id, String name, double quantity, String unit, String expiryDate) {
    this.id = id;
    this.name = name;
    this.quantity = quantity;
    this.unit = unit;
    this.expiryDate = expiryDate;
  }

  // Get pantry id
  public int getId() {
    return id;
  }

  //Get pantry name
  public String getName() {
    return name;
  }

  //Get pantry qty
  public double getQuantity() {
    return quantity;
  }

  // Get pantry units
  public String getUnit() {
    return unit;
  }

  // Get pantry expiry date
  public String getExpiryDate() {
    return expiryDate;
  }

  // Override the string
  @Override
  public String toString() {
    return name + " - " + quantity + " " + unit;
  }
}