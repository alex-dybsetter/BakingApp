package net.alexblass.bakingapp.models;

import android.os.Parcelable;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Section grouping for the expandable recycler view.
 */

public class Group<T> extends ExpandableGroup<Parcelable> {

    public Group(String title, List<Parcelable> items) {
        super(title, items);
    }
}