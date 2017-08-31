package net.alexblass.bakingapp.utilities;

/**
 * An Adapter to display the Ingredients and the RecipeSteps in RecipeDetailActivity in an
 * expandable list.
 */

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.alexblass.bakingapp.R;
import net.alexblass.bakingapp.models.Ingredient;
import net.alexblass.bakingapp.models.RecipeStep;

public class ExpandableListAdapter<T> extends BaseExpandableListAdapter {

    // The context of the Adapter
    private Context mContext;

    // A list to hold the section titles
    private List<String> mSectionTitles;

    // child data in format of header title, child title
    // Use a generic type T so we can use this Adapter class
    // for both Ingredients and RecipeSteps.
    private HashMap<String, List<T>> mSectionChildData;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<T>> listChildData) {
        this.mContext = context;
        this.mSectionTitles = listDataHeader;
        this.mSectionChildData = listChildData;
    }

    @Override
    public T getChild(int groupPosition, int childPosititon) {
        return this.mSectionChildData.get(this.mSectionTitles.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // Display the child data in the correct layout corresponding to its type
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_detail_group, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.step_description_imageview);
        TextView mainContent = // Either the ingredient name or the description of the recipe step
                (TextView) convertView.findViewById(R.id.detail_main_content_tv);
        TextView quantity = (TextView) convertView.findViewById(R.id.ingredient_quantity_tv);
        TextView measurement = (TextView) convertView.findViewById(R.id.ingredient_measurement_tv);

        // If the child is a RecipeStep, display the RecipeStep data accordingly
        if (getChild(groupPosition, childPosition) instanceof RecipeStep){
            RecipeStep childView = (RecipeStep) getChild(groupPosition, childPosition);

            icon.setVisibility(View.VISIBLE);
            mainContent.setText(childView.getShortDescription());
            quantity.setText("");
            measurement.setText("");

            // If the child is an Ingredient, display the Ingredient data accordingly
        } else if (getChild(groupPosition, childPosition) instanceof Ingredient){
            Ingredient childView = (Ingredient) getChild(groupPosition, childPosition);

            icon.setVisibility(View.GONE);
            mainContent.setText(childView.getIngredientName());
            quantity.setText(String.valueOf(childView.getQuantity()));
            measurement.setText(childView.getMeasurement());
        }


        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mSectionChildData.get(this.mSectionTitles.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mSectionTitles.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mSectionTitles.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // Display the title views for each section
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView titleTv = (TextView) convertView
                .findViewById(R.id.title_tv);
        titleTv.setTypeface(null, Typeface.BOLD);
        titleTv.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        // Ingredients are not selectable
        if (groupPosition == 0){
            return false;

            // Step descriptions are selectable
        } else {
            return true;
        }
    }
}