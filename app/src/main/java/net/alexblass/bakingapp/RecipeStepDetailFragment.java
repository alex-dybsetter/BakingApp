package net.alexblass.bakingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.models.RecipeStep;

import static net.alexblass.bakingapp.MainActivityFragment.RECIPE_KEY;

/**
 * This Fragment allows users to view the detailed information about a RecipeStep.
 */

public class RecipeStepDetailFragment extends Fragment implements ExoPlayer.EventListener {
    // The key to pass and get RecipeSteps from Intents
    public static final String RECIPE_STEP_KEY = "recipe_step";

    // The selected RecipeStep
    private RecipeStep mSelectedStep;

    // The Recipe
    private Recipe mSelectedRecipe;

    // The views in the StepDetail layout
    private TextView mTitleTv, mDescriptionTv;
    private ImageView mThumbnailImageView;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private ProgressBar mLoadingIndicator;
    private Button mPrevBtn, mNextBtn;

    // Empty constructor
    public RecipeStepDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        // Clear any old views to avoid overlaying Fragment layouts
        if (container != null) {
            container.removeAllViews();
        }

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            // If there's a valid RecipeStep, get the data from the RecipeStep and display it
            if (bundle.getParcelable(RECIPE_STEP_KEY) != null) {
                mSelectedStep = bundle.getParcelable(RECIPE_STEP_KEY);

                mThumbnailImageView = (ImageView) rootView.findViewById(R.id.step_thumbnail_imageview);

                mTitleTv = (TextView) rootView.findViewById(R.id.step_description_title_tv);
                mDescriptionTv = (TextView) rootView.findViewById(R.id.step_description_tv);
                mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.step_video_exoplayer);
                mLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.loading_indicator_step_detail);

                mPrevBtn = (Button) rootView.findViewById(R.id.prev_step_btn);
                mNextBtn = (Button) rootView.findViewById(R.id.next_step_btn);

                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector =
                        new DefaultTrackSelector(videoTrackSelectionFactory);
                LoadControl loadControl = new DefaultLoadControl();

                mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);

                mPlayerView.setPlayer(mExoPlayer);
                mPlayerView.setKeepScreenOn(true);

                DataSource.Factory dataSourceFactory =
                        new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "ExoPlayer"));

                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

                mTitleTv.setText(mSelectedStep.getShortDescription());
                mDescriptionTv.setText(mSelectedStep.getDescription());

                ConnectivityManager cm = (ConnectivityManager) getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnected();


                // Only display the video or image if there is Internet connection
                if (isConnected) {

                    // Check if there is an image to the step
                    if (!mSelectedStep.getImageUrl().equals("")) {
                        mLoadingIndicator.setVisibility(View.GONE);
                        mThumbnailImageView.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity())
                                .load(mSelectedStep.getImageUrl())
                                .into(mThumbnailImageView);
                    }

                    // Check if there is a video to the step
                    if (!mSelectedStep.getVideoUrl().equals("")) {

                        // If the device is in landscape and is not a tablet, make the video full screen
                        if (!getResources().getBoolean(R.bool.isTablet)) {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                            } else {
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                            }
                        }

                        Uri vidUri = Uri.parse(mSelectedStep.getVideoUrl());

                        MediaSource videoSource = new ExtractorMediaSource(vidUri,
                                dataSourceFactory, extractorsFactory, null, null);

                        mExoPlayer.addListener(this);
                        mExoPlayer.prepare(videoSource);
                        mExoPlayer.setPlayWhenReady(true);
                    }

                    // If there's no video or image, hide the loading indicator
                    if (mSelectedStep.getImageUrl().equals("") && mSelectedStep.getVideoUrl().equals("")) {
                        mLoadingIndicator.setVisibility(View.GONE);
                    }
                } else {
                    mLoadingIndicator.setVisibility(View.GONE);
                }

                // If the RecipeStep is the first or last, hide the Previous or Next button
                final int stepId = mSelectedStep.getId();
                if (stepId == 0){
                    mPrevBtn.setVisibility(View.GONE);
                }
                if (bundle.getParcelable(RECIPE_KEY) != null) {
                    mSelectedRecipe = bundle.getParcelable(RECIPE_KEY);
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mSelectedRecipe.getName());

                    if (stepId == mSelectedRecipe.getSteps().size() - 1){
                        mNextBtn.setVisibility(View.GONE);
                    }
                }

                // Open a fragment with the previous step
                mPrevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (stepId > 0) {
                            mSelectedStep = mSelectedRecipe.getSteps().get(stepId - 1);
                            launchNewFragment(mSelectedRecipe, mSelectedStep);
                        }
                    }
                });

                // Open a fragment with the next step
                mNextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (stepId < mSelectedRecipe.getSteps().size() - 1) {
                            mSelectedStep = mSelectedRecipe.getSteps().get(stepId + 1);
                            launchNewFragment(mSelectedRecipe, mSelectedStep);
                        }
                    }
                });
            }
        }
        return rootView;
    }

    // Create a new StepDetailFragment
    private void launchNewFragment(Recipe recipe, RecipeStep step){
        int layoutId;
        // Inflate the layout in half the screen if it's a tablet in landscape
        if (getResources().getBoolean(R.bool.isTablet) &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            layoutId = R.id.recipe_step_container;
        } else { // Otherwise use the full screen
            layoutId = R.id.fragment_container;
        }

        RecipeStepDetailFragment stepDetailFragment = new RecipeStepDetailFragment();

        Bundle args = new Bundle();
        args.putParcelable(RECIPE_STEP_KEY, step);
        args.putParcelable(RECIPE_KEY, recipe);

        stepDetailFragment.setArguments(args);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(layoutId, stepDetailFragment)
                .commit();
    }

    // Pause the video when the player is not in focus
    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    // Required override method
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    // Required override method
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    // The ExoPlayer video states
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                break;
            case ExoPlayer.STATE_IDLE:
                break;
            case ExoPlayer.STATE_READY:
                // When the video is ready to play, make it visible
                mLoadingIndicator.setVisibility(View.GONE);
                mPlayerView.setVisibility(View.VISIBLE);
                break;
            case ExoPlayer.STATE_ENDED:
                break;
        }
    }

    // An error message if there's a problem streaming the video
    @Override
    public void onPlayerError(ExoPlaybackException error) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(R.string.video_error_title);
        dialog.setMessage(R.string.video_error_body);
        dialog.setPositiveButton(R.string.positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog ad = dialog.create();
        ad.show();
    }

    // Required Override method
    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExoPlayer.release();
    }
}