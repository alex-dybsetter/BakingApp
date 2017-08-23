package net.alexblass.bakingapp.models;

/**
 *  A class to hold the info related to a particular Recipe.
 */

public class Recipe {
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
}
