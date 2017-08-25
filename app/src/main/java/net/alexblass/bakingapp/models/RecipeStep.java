package net.alexblass.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A class to hold the information of a particular step in a Recipe.
 * Implements Parcelable to be passed between Intents.
 */

public class RecipeStep implements Parcelable {
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

    // Create a RecipeStep from a Parcel
    protected RecipeStep(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mDescription = in.readString();
        mVideoUrl = in.readString();
        mImageUrl = in.readString();
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

    // Required override method for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    // Required override method for Parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mVideoUrl);
        dest.writeString(mImageUrl);
    }

    // Creator for Parcelable implementation
    public static final Creator<RecipeStep> CREATOR = new Creator<RecipeStep>() {
        @Override
        public RecipeStep createFromParcel(Parcel in) {
            return new RecipeStep(in);
        }

        @Override
        public RecipeStep[] newArray(int size) {
            return new RecipeStep[size];
        }
    };
}
