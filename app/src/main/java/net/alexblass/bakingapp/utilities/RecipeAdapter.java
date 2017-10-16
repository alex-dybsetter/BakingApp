package net.alexblass.bakingapp.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.alexblass.bakingapp.R;
import net.alexblass.bakingapp.models.Ingredient;
import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.models.RecipeStep;

/**
 * An Adapter to display the Recipes in MainActivityFragment in a CardView list.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>{

    // An array to hold the recipes obtained from the JSON file
    private Recipe[] mAllRecipies;

    private LayoutInflater mInflator;

    // The context of the Adapter
    private Context mContext;

    // A ClickListener to launch a new activity when the user clicks a card
    private ItemClickListener mClickListener;

    // Construct a new RecipeAdapter
    public RecipeAdapter(Context context, Recipe[] allRecipes){
        this.mInflator = LayoutInflater.from(context);
        this.mAllRecipies = allRecipes;
        this.mContext = context;
    }

    // Notify the Adapter about dataset changes to update the views
    public void setAllRecipies(Recipe[] updatedRecipes){
        mAllRecipies = updatedRecipes;
        notifyDataSetChanged();
    }

    // Catches clicks on the Recipe cards
    public void setClickListener(ItemClickListener itemClickListener){
        mClickListener = itemClickListener;
    }

    // Get a selected Recipe
    public Recipe getItem(int index){
        return mAllRecipies[index];
    }

    // Get the number of Recipes in the array
    @Override
    public int getItemCount() {
        return mAllRecipies.length;
    }

    // Creates new views as needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.item_recipe_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Binds the Recipe data to the corresponding views
    @Override
    public void onBindViewHolder(RecipeAdapter.ViewHolder holder, int position) {
        // Get the selected Recipe
        Recipe selectedRecipe = mAllRecipies[position];

        // Set the Recipe data to the CardView
        if (selectedRecipe != null){
            holder.name.setText(selectedRecipe.getName());
            holder.servings.setText(mContext.getString(
                    R.string.servings, selectedRecipe.getServings()));

            if (!selectedRecipe.getImageUrl().equals("")){
                holder.image.setVisibility(View.VISIBLE);
                Picasso.with(mContext)
                        .load(selectedRecipe.getImageUrl())
                        .into(holder.image);
            }

            int imgId;
            if (selectedRecipe.getIsFavorite()){
                imgId = R.drawable.ic_favorite_white_24dp;
            } else {
                imgId = R.drawable.ic_favorite_border_white_24dp;
            }

            holder.favoriteBtn.setImageResource(imgId);
        }
    }

    // The main activity will respond to clicks by implementing this method
    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }

    // Stores and recycles views to improve app performance and smoother scrolling
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView image;
        private TextView name;
        private TextView servings;
        private ImageButton favoriteBtn;
        private ImageButton shareBtn;
        private Recipe selectedRecipe;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            image = (ImageView) itemView.findViewById(R.id.recipe_image);
            name = (TextView) itemView.findViewById(R.id.recipe_name_tv);
            servings = (TextView) itemView.findViewById(R.id.recipe_servings_tv);
            favoriteBtn = (ImageButton) itemView.findViewById(R.id.favorite_btn);
            shareBtn = (ImageButton) itemView.findViewById(R.id.share_btn);

            favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRecipe = mAllRecipies[getAdapterPosition()];

                // We are changing the existing value to the opposite
                boolean isFavorite = !selectedRecipe.getIsFavorite();

                selectedRecipe.setIsFavorite(isFavorite);
                favoriteBtn.setImageResource(
                        isFavorite ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp);

                RecipeQueryUtils.updateFavorite(mContext, selectedRecipe.getDbId(), isFavorite);
            }});

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedRecipe = mAllRecipies[getAdapterPosition()];

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");

                    String shareBody = mContext.getString(R.string.share_subject, selectedRecipe.getName())
                            +"\n\n"+ mContext.getString(R.string.ingredients_title) +"\n";

                    for (Ingredient i : selectedRecipe.getIngredients()){
                        shareBody += i.getIngredientName() +" "+ i.getQuantity() +" "+
                                i.getMeasurement() +"\n";
                    }

                    shareBody += "\n"+ mContext.getString(R.string.steps_title) +"\n";

                    for (RecipeStep r : selectedRecipe.getSteps()){
                        shareBody += r.getDescription() +"\n";
                    }

                    shareBody += "\n\n"+ mContext.getString(R.string.share_attribution);

                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                            mContext.getString(R.string.share_subject, selectedRecipe.getName()));
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                    mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null){
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
