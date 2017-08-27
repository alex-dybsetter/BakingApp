package net.alexblass.bakingapp.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.alexblass.bakingapp.R;
import net.alexblass.bakingapp.models.RecipeStep;

/**
 * An Adapter to display the RecipeSteps in RecipeDetailFragment in a list.
 */

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.ViewHolder>{

    // An array to hold the RecipeSteps in a Recipe
    private RecipeStep[] mSteps;

    private LayoutInflater mInflator;

    // The context of the Adapter
    private Context mContext;

    // A ClickListener to launch a new detail view when the user selects a step
    private ItemClickListener mClickListener;

    // Construct a new RecipeStepAdapter
    public RecipeStepAdapter(Context context, RecipeStep[] steps){
        this.mInflator = LayoutInflater.from(context);
        this.mSteps = steps;
        this.mContext = context;
    }

    // Notify the Adapter about dataset changes to update the views
    public void setRecipeSteps(RecipeStep[] updatedSteps){
        mSteps = updatedSteps;
        notifyDataSetChanged();
    }

    // Catches clicks on the RecipeStep
    public void setClickListener(ItemClickListener itemClickListener){
        mClickListener = itemClickListener;
    }

    // Get a selected RecipeStep
    public RecipeStep getItem(int index){
        return mSteps[index];
    }

    // Get the number of RecipeSteps in the array
    @Override
    public int getItemCount() {
        return mSteps.length;
    }

    // Creates new views as needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.item_recipe_step, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Binds the RecipeStep data to the corresponding views
    @Override
    public void onBindViewHolder(RecipeStepAdapter.ViewHolder holder, int position) {
        // Get the selected RecipeStep
        RecipeStep currentStep = mSteps[position];

        // Set the RecipeStep data to the corresponding views
        if (currentStep != null){
            holder.shortDescription.setText(currentStep.getShortDescription());
        }
    }

    // The main activity will respond to clicks by implementing this method
    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }

    // Stores and recycles views to improve app performance and smoother scrolling
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView shortDescription;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            shortDescription = (TextView) itemView.findViewById(R.id.short_description_tv);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null){
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}

