package net.alexblass.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A class to hold the information about the Ingredients in a Recipe.
 * Implements Parcelable to be passed between Intents.
 */

public class Ingredient implements Parcelable {
    private long mQuantity;
    private String mMeasurement;
    private String mIngredientName;

    public Ingredient(long quantity, String measurement, String name){
        this.mQuantity = quantity;
        this.mMeasurement = measurement;
        this.mIngredientName = name;
    }

    // Create an Ingredient from a Parcel
    protected Ingredient(Parcel in) {
        mQuantity = in.readLong();
        mMeasurement = in.readString();
        mIngredientName = in.readString();
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

    // Required overrride method for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    // Required override method for Parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mQuantity);
        dest.writeString(mMeasurement);
        dest.writeString(mIngredientName);
    }

    // Creator for Parcelable implementation
    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
