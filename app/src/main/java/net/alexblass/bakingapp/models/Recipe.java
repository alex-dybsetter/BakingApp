package net.alexblass.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  A class to hold the info related to a particular Recipe.
 *  Implements Parcelable to be passed between Intents.
 */

public class Recipe implements Parcelable{
    private int mId;
    private String mName;
    private int mServings;
    private String mImageUrl;
    private Ingredient[] mIngredients;
    private RecipeStep[] mSteps;

    public Recipe(int id, String name, Ingredient[] ingredients,
                  RecipeStep[] steps, int servings, String imageUrl){
        this.mId = id;
        this.mName = name;
        this.mIngredients = ingredients;
        this.mServings = servings;
        this.mImageUrl = imageUrl;
        this.mSteps = steps;
    }

    // Create a Recipe from a Parcel
    protected Recipe(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mServings = in.readInt();
        mImageUrl = in.readString();
        mIngredients = in.createTypedArray(Ingredient.CREATOR);
        mSteps = in.createTypedArray(RecipeStep.CREATOR);
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Ingredient[] getIngredients() {
        return mIngredients;
    }

    public RecipeStep[] getSteps() {
        return mSteps;
    }

    public int getServings() {
        return mServings;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    // Required overrride method for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    // Required override method for Parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeInt(mServings);
        dest.writeString(mImageUrl);
        dest.writeTypedArray(mIngredients, 0);
        dest.writeTypedArray(mSteps, 0);
    }

    // Creator for Parcelable implementation
    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
