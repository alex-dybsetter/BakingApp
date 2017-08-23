package net.alexblass.bakingapp.models;

/**
 * A class to hold the information about the Ingredients in a Recipe.
 */

public class Ingredient {
    private long mQuantity;
    private String mMeasurement;
    private String mIngredientName;

    public Ingredient(long quantity, String measurement, String name){
        this.mQuantity = quantity;
        this.mMeasurement = measurement;
        this.mIngredientName = name;
    }

    public long getQuantity() {
        return mQuantity;
    }

    public String getMeasurement() {
        return mMeasurement;
    }

    public String getIngredientName() {
        return mIngredientName;
    }
}
