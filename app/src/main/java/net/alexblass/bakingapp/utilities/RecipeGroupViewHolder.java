package net.alexblass.bakingapp.utilities;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import net.alexblass.bakingapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A holder class for the group views in our ExpandableRecyclerView.
 */

public class RecipeGroupViewHolder extends GroupViewHolder {

    @BindView(R.id.title_tv) TextView mGroupTitle;

    public RecipeGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setSectionTitle(ExpandableGroup group) {
        mGroupTitle.setText(group.getTitle());
        mGroupTitle.setTypeface(null, Typeface.BOLD);
    }
}