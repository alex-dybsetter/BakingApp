package net.alexblass.bakingapp.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import net.alexblass.bakingapp.R;
import net.alexblass.bakingapp.models.Ingredient;
import net.alexblass.bakingapp.models.RecipeStep;

import java.util.List;

/**
 * An Adapter to display the Ingredients and the RecipeSteps in RecipeOverviewActivity in an
 * expandable recycler view.
 */

public class ExpandableRecipeAdapter extends ExpandableRecyclerViewAdapter<RecipeGroupViewHolder, RecipeChildViewHolder> {

    LayoutInflater mInflater;
    RecipeStep mSelectedStep;

    // A ClickListener so that when we click on a recipe step, we can launch a new fragment
    private ItemClickListener mClickListener;

    public ExpandableRecipeAdapter(Context context, List<? extends ExpandableGroup> groups) {
        super(groups);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecipeGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_group, parent, false);
        return new RecipeGroupViewHolder(view);
    }

    @Override
    public RecipeChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_detail_group, parent, false);
        return new RecipeChildViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(RecipeChildViewHolder holder, int flatPosition, ExpandableGroup group,
                                      int childIndex) {

        // If the child is a RecipeStep, display the RecipeStep data accordingly
        if (group.getItems().get(childIndex) instanceof RecipeStep){
            final RecipeStep childView = (RecipeStep) group.getItems().get(childIndex);
            holder.onBind(childView);
            holder.setClickListener(new RecipeChildViewHolder.ItemClickListener() {
                // If the child view is a RecipeStep, set a click listener
                @Override
                public void onItemClick(View view, int position) {
                    if (mClickListener != null) {
                        mSelectedStep = childView;
                        mClickListener.onChildItemClick(view, position);
                    }
                }
            });
            // If the child is an Ingredient, display the Ingredient data accordingly
        } else if (group.getItems().get(childIndex) instanceof Ingredient){
            Ingredient childView = (Ingredient) group.getItems().get(childIndex);
            holder.onBind(childView);
        }
    }

    @Override
    public void onBindGroupViewHolder(RecipeGroupViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {
        holder.setSectionTitle(group);
    }

    // Catches clicks on the child view
    public void setChildClickListener(ItemClickListener itemClickListener){
        mClickListener = itemClickListener;
    }

    // Will be implemented by the activity
    public interface ItemClickListener {
        void onChildItemClick(View view, int position);
    }

    public RecipeStep getStep(){
        return mSelectedStep;
    }
}