package com.mariusswa.smartpantrymanager;

public class Recipe {

// store a recipe to the database
  private int id;
  private String name;
  private String instructions;

  // create a recipe with its id
  public Recipe(int id, String name, String instructions) {
    this.id = id;
    this.name = name;
    this.instructions = instructions;
  }

  // return recipe details
  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getInstructions() {
    return instructions;
  }

  @Override
  public String toString() {
    return name;
  }
}