package net.alexblass.bakingapp.utilities;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import net.alexblass.bakingapp.R;
import net.alexblass.bakingapp.RecipeStepDetailFragment;
import net.alexblass.bakingapp.models.Ingredient;
import net.alexblass.bakingapp.models.RecipeStep;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A holder class for the child views in our ExpandableRecyclerView.
 */

public class RecipeChildViewHolder<T> extends ChildViewHolder {

    @BindView(R.id.step_description_imageview) ImageView mIcon;
    // Either the ingredient name or the description of the recipe step
    @BindView(R.id.detail_main_content_tv) TextView mMainContent;
    @BindView(R.id.ingredient_quantity_tv) TextView mQuantity;
    @BindView(R.id.ingredient_measurement_tv) TextView mMeasurement;

    // A ClickListener so that when we click on a recipe step, we can launch a new fragment
    private ItemClickListener mClickListener;

    public RecipeChildViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        });
        ButterKnife.bind(this, itemView);
    }

    public void onBind(T item) {
        // If the child is a RecipeStep, display the RecipeStep data accordingly
        if (item instanceof RecipeStep){
            RecipeStep childView = (RecipeStep) item;

            mIcon.setVisibility(View.VISIBLE);
            mMainContent.setText(childView.getShortDescription());
            mQuantity.setText("");
            mMeasurement.setText("");

            // If the child is an Ingredient, display the Ingredient data accordingly
        } else if (item instanceof Ingredient){
            Ingredient childView = (Ingredient) item;

            mIcon.setVisibility(View.GONE);
            mMainContent.setText(childView.getIngredientName());
            mQuantity.setText(String.valueOf(childView.getQuantity()));
            mMeasurement.setText(childView.getMeasurement());
        }
    }

    // Catches clicks on the child view
    public void setClickListener(ItemClickListener itemClickListener){
        mClickListener = itemClickListener;
    }

    // Will be implemented by the adapter
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}