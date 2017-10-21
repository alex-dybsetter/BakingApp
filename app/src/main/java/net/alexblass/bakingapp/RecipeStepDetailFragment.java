package net.alexblass.bakingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import net.alexblass.bakingapp.models.Recipe;
import net.alexblass.bakingapp.models.RecipeStep;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.alexblass.bakingapp.data.constants.Keys.RECIPE_KEY;
import static net.alexblass.bakingapp.data.constants.Keys.RECIPE_STEP_KEY;

/**
 * This Fragment allows users to view the detailed information about a RecipeStep.
 */

public class RecipeStepDetailFragment extends Fragment
        implements ExoPlayer.EventListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    // Keys for our saved instance state
    public String SELECTED_POSITION_KEY = "mPlayerPosition";

    // The selected RecipeStep
    private RecipeStep mSelectedStep;

    // The Recipe
    private Recipe mSelectedRecipe;

    // The views in the StepDetail layout
    @BindView(R.id.rich_media) LinearLayout mRichMedia;
    @BindView(R.id.step_description_title_tv) TextView mTitleTv;
    @BindView(R.id.step_description_tv) TextView mDescriptionTv;
    @BindView(R.id.step_thumbnail_imageview) ImageView mThumbnailImageView;
    @BindView(R.id.loading_indicator_step_detail) ProgressBar mLoadingIndicator;
    @BindView(R.id.prev_step_btn) Button mPrevBtn;
    @BindView(R.id.next_step_btn) Button mNextBtn;
    @BindView(R.id.player_layout) LinearLayout mPlayerLayout;
    private SimpleExoPlayerView mPlayerView;

    private SimpleExoPlayer mExoPlayer;
    private Uri mStepVideoUri;
    private long mPlayerPosition;

    // To detect when the player flings the full screen player
    private GestureDetector mGestureDetector;

    // Empty constructor
    public RecipeStepDetailFragment() {
    }

    // Static factory method to initialize the fragment with the correct arguments
    public static RecipeStepDetailFragment newInstance(Recipe recipe, RecipeStep step) {
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(RECIPE_STEP_KEY, step);
        args.putParcelable(RECIPE_KEY, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        ButterKnife.bind(this, rootView);
        mPlayerView = ButterKnife.findById(mPlayerLayout, R.id.step_video_exoplayer);

        // Clear any old views to avoid overlaying Fragment layouts
        if (container != null) {
            container.removeAllViews();
        }

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            // If there's a valid RecipeStep, get the data from the RecipeStep and display it
            if (bundle.getParcelable(RECIPE_STEP_KEY) != null) {
                mSelectedStep = bundle.getParcelable(RECIPE_STEP_KEY);

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
                        mStepVideoUri = Uri.parse(mSelectedStep.getVideoUrl());
                        initializePlayer(mStepVideoUri);
                        mGestureDetector = new GestureDetector(getActivity(), this);

                        // If the device is in landscape and is not a tablet, make the video full screen
                        if (!getResources().getBoolean(R.bool.isTablet)) {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                hideAppBar();

                                mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

                                mPlayerView.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, final MotionEvent event) {
                                        mGestureDetector.onTouchEvent(event);
                                        return true;
                                    }
                                });
                            } else {
                                showAppBar();
                            }
                        }
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

    // Hide the app bar
    private void hideAppBar(){
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    // Show the app bar
    private void showAppBar(){
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
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

    // Initialize the player
    private void initializePlayer(Uri vidUri){
        if (mExoPlayer == null){
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "ExoPlayer"));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            MediaSource videoSource = new ExtractorMediaSource(vidUri, dataSourceFactory, extractorsFactory, null, null);

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mExoPlayer.addListener(this);


            if (mPlayerPosition != C.TIME_UNSET) {
                mExoPlayer.seekTo(mPlayerPosition);
            }
            mExoPlayer.prepare(videoSource);
            mExoPlayer.setPlayWhenReady(true);

            mPlayerView.setPlayer(mExoPlayer);
            mPlayerView.setKeepScreenOn(true);
        }
    }

    // Release the player but save the position
    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mPlayerPosition = mExoPlayer.getCurrentPosition();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    // Start the player
    @Override
    public void onResume() {
        super.onResume();
        if (mStepVideoUri != null) {
            initializePlayer(mStepVideoUri);
        }
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
                mRichMedia.setVisibility(View.VISIBLE);
                mPlayerLayout.setVisibility(View.VISIBLE);
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

    @Override // Required Override method
    public void onPositionDiscontinuity() {

    }

    @Override // Required override method
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override // Required override method
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override // Required override method
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override // Required override method
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(SELECTED_POSITION_KEY, mPlayerPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_POSITION_KEY)) {
                mPlayerPosition = savedInstanceState.getLong(SELECTED_POSITION_KEY);
            }
        }
    }

    // When the user double taps, make the player full screen again
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        hideAppBar();

        mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

        // Resize the layout containing the player
        ViewGroup.LayoutParams params = mPlayerLayout.getLayoutParams();

        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mPlayerLayout.setLayoutParams(params);

        return true;
    }

    // When the user flings the player (swipes in any direction) the player should go from
    // full screen to within the fragment.
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // Show the app bar
        showAppBar();

        // Resize the layout containing the player
        ViewGroup.LayoutParams params = mPlayerLayout.getLayoutParams();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        params.height = height / 2;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mPlayerLayout.setLayoutParams(params);
        mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        return true;
    }

    @Override // Required override method
    public void onLongPress(MotionEvent e) {

    }

    @Override // Required override method
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override // Required override method
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override // Required override method
    public void onShowPress(MotionEvent e) {

    }

    @Override // Required override method
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override // Required override method
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override // Required override method
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }
}