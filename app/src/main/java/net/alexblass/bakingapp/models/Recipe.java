package net.alexblass.bakingapp.models;

/**
 *  A class to hold the info related to a particular Recipe.
 */

public class Recipe {
    private String mName;
    private int mServings;

    public Recipe(String name, int servings){
        this.mName = name;
        this.mServings = servings;
    }

    public String getName() {
        return mName;
    }

    public int getServings(){
        return mServings;
    }
}
