package net.alexblass.bakingapp.models;

/**
 * A class to hold the information of a particular step in a Recipe.
 */

public class RecipeStep {
    private int mId;
    private String mTitle;
    private String mDescription;
    private String mVideoUrl;
    private String mImageUrl;

    public RecipeStep(int id, String title, String description, String videoUrl, String imageUrl){
        this.mId = id;
        this.mTitle = title;
        this.mDescription = description;
        this.mVideoUrl = videoUrl;
        this.mImageUrl = imageUrl;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getVideoUrl() {
        return mVideoUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
}
