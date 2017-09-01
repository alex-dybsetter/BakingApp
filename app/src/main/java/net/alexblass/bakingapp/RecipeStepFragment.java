package net.alexblass.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.alexblass.bakingapp.models.RecipeStep;

import static net.alexblass.bakingapp.RecipeDetailFragment.RECIPE_STEP_KEY;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {RecipeStepFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the { RecipeStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//public class RecipeStepFragment extends Fragment {
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;
//
//    public RecipeStepFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment RecipeStepFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static RecipeStepFragment newInstance(String param1, String param2) {
//        RecipeStepFragment fragment = new RecipeStepFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_recipe_step, container, false);
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
//}
public class RecipeStepFragment extends Fragment {

    // The selected RecipeStep
    private RecipeStep mSelectedStep;

    TextView mTitleTv, mDescriptionTv;
    ImageView mThumbnailImageView;

    // Empty constructor
    public RecipeStepFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        Intent intentThatStartedThisActivity = getActivity().getIntent();

        if (intentThatStartedThisActivity != null) {
            // If there's a valid RecipeStep, get the data from the RecipeStep and display it
            if (intentThatStartedThisActivity.hasExtra(RECIPE_STEP_KEY)) {
                mSelectedStep = intentThatStartedThisActivity.getParcelableExtra(RECIPE_STEP_KEY);

                mThumbnailImageView = (ImageView) rootView.findViewById(R.id.step_thumbnail_imageview);

                mTitleTv = (TextView) rootView.findViewById(R.id.step_description_title_tv);
                mDescriptionTv = (TextView) rootView.findViewById(R.id.step_description_tv);

                mTitleTv.setText(mSelectedStep.getShortDescription());
                mDescriptionTv.setText(mSelectedStep.getDescription());

                // Check if there is an image to the step
                if (!mSelectedStep.getImageUrl().equals("")){
                    mThumbnailImageView.setVisibility(View.VISIBLE);
                    Picasso.with(getActivity())
                            .load(mSelectedStep.getImageUrl())
                            .into(mThumbnailImageView);
                }
            }
        }
        return rootView;
    }
}