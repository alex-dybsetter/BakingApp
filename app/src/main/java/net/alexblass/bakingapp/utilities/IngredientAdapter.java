package net.alexblass.bakingapp.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.alexblass.bakingapp.R;
import net.alexblass.bakingapp.models.Ingredient;

/**
 * An Adapter to display the Ingredients in RecipeDetailActivity in a list.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder>{

    // An array to hold the Ingredients in a Recipe
    private Ingredient[] mIngredients;

    private LayoutInflater mInflator;

    // Construct a new IngredientAdapter
    public IngredientAdapter(Context context, Ingredient[] ingredients){
        this.mInflator = LayoutInflater.from(context);
        this.mIngredients = ingredients;
    }

    // Notify the Adapter about dataset changes to update the views
    public void setIngredients(Ingredient[] ingredients){
        mIngredients = ingredients;
        notifyDataSetChanged();
    }

    // Get a selected Ingredient
    public Ingredient getItem(int index){
        return mIngredients[index];
    }

    // Get the number of Ingredients in the array
    @Override
    public int getItemCount() {
        return mIngredients.length;
    }

    // Creates new views as needed
    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.item_ingredient, parent, false);
        IngredientAdapter.ViewHolder viewHolder = new IngredientAdapter.ViewHolder(view);
        return viewHolder;
    }

    // Binds the Ingredient data to the corresponding views
    @Override
    public void onBindViewHolder(IngredientAdapter.ViewHolder holder, int position) {
        // Get the selected Ingredient
        Ingredient ingredient = mIngredients[position];

        // Set the Ingredient data
        if (ingredient != null){
            holder.name.setText(ingredient.getIngredientName());
            holder.quantity.setText(String.valueOf(ingredient.getQuantity()));
            holder.measurement.setText(ingredient.getMeasurement());
        }
    }

    // Stores and recycles views to improve app performance and smoother scrolling
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView name;
        private TextView quantity;
        private TextView measurement;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);

            name = (TextView) itemView.findViewById(R.id.ingredient_name_tv);
            quantity = (TextView) itemView.findViewById(R.id.ingredient_quantity_tv);
            measurement = (TextView) itemView.findViewById(R.id.ingredient_measurement_tv);
        }

        // Required override method
        @Override
        public void onClick(View v) {
            // On click, do nothing
        }
    }
}
