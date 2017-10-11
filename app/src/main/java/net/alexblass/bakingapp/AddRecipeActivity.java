package net.alexblass.bakingapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * An activity to add new user-entered Recipes to the database.
 */

public class AddRecipeActivity extends AppCompatActivity {

    private EditText mIngredientName, mIngredientQty, mIngredientMeas, mStepShortDesc, mStepDetailDesc;
    private Button mAddIngredientBtn, mAddStepBtn;
    private LinearLayout mIngredientContainer, mStepContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        mIngredientName = (EditText) findViewById(R.id.add_ingredient_name);
        mIngredientQty = (EditText) findViewById(R.id.add_ingredient_quantity);
        mIngredientMeas = (EditText) findViewById(R.id.add_ingredient_measurement);

        mAddIngredientBtn = (Button) findViewById(R.id.add_ingredient_btn);
        mIngredientContainer = (LinearLayout) findViewById(R.id.ingredient_container);

        mStepShortDesc = (EditText) findViewById(R.id.add_step_short_desc);
        mStepDetailDesc = (EditText) findViewById(R.id.add_step_detail_desc);

        mAddStepBtn = (Button) findViewById(R.id.add_steps_btn);
        mStepContainer = (LinearLayout) findViewById(R.id.step_container);

        mAddIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.item_added_ingredient, null);

                if (!mIngredientName.getText().toString().equals("") &&
                        !mIngredientQty.getText().toString().equals("") &&
                        !mIngredientMeas.getText().toString().equals("")) {
                    TextView name = (TextView) addView.findViewById(R.id.added_ingredient_name);
                    name.setText(mIngredientName.getText().toString());
                    mIngredientName.setText("");

                    TextView qty = (TextView) addView.findViewById(R.id.added_ingredient_qty);
                    qty.setText(mIngredientQty.getText().toString());
                    mIngredientQty.setText("");

                    TextView meas = (TextView) addView.findViewById(R.id.added_ingredient_meas);
                    meas.setText(mIngredientMeas.getText().toString());
                    mIngredientMeas.setText("");

                    Button buttonRemove = (Button) addView.findViewById(R.id.delete_ingredient_btn);
                    buttonRemove.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ((LinearLayout) addView.getParent()).removeView(addView);
                        }
                    });

                    mIngredientContainer.addView(addView);
                }
            }});

        mAddStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.item_added_step, null);

                if (!mStepShortDesc.getText().toString().equals("") &&
                        !mStepDetailDesc.getText().toString().equals("")) {
                    TextView shortDesc = (TextView) addView.findViewById(R.id.added_step_short_desc);
                    shortDesc.setText(mStepShortDesc.getText().toString());
                    mStepShortDesc.setText("");

                    TextView detailDesc = (TextView) addView.findViewById(R.id.added_step_detail_desc);
                    detailDesc.setText(mStepDetailDesc.getText().toString());
                    mStepDetailDesc.setText("");

                    Button buttonRemove = (Button) addView.findViewById(R.id.delete_step_btn);
                    buttonRemove.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ((LinearLayout) addView.getParent()).removeView(addView);
                        }
                    });

                    mStepContainer.addView(addView);
                }
            }});
    }
}
