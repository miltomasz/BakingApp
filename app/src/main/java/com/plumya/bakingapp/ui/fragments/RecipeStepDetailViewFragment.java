package com.plumya.bakingapp.ui.fragments;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.model.Step;
import com.plumya.bakingapp.utils.VideoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by miltomasz on 24/04/18.
 */

public class RecipeStepDetailViewFragment extends Fragment implements ExoPlayer.EventListener {

    private static final String LOG_TAG = RecipeStepDetailViewFragment.class.getSimpleName();

    public static final String STEP_ID = "stepId";
    public static final String STEPS = "steps";
    public static final String APPLICATION_NAME = "BakingApp";
    public static final int DEFAULT_VALUE = -1;
    public static final String VIDEO_POSITION = "videoPosition";
    public static final int START_POSITION = 0;

    private static final String VIDEO_STATE = "videoState";

    private static MediaSessionCompat mediaSession;

    @Nullable
    @BindView(R.id.recipeStepInstructionTv)
    TextView recipeStepInstructionTv;

    @BindView(R.id.playerView)
    SimpleExoPlayerView playerView;

    @Nullable
    @BindView(R.id.backBtn)
    Button backBtn;

    @Nullable
    @BindView(R.id.nextBtn)
    Button nextBtn;

    private long stepId;
    private List<Step> steps;
    private ListIterator<Step> iterator;
    private SimpleExoPlayer exoPlayer;
    private PlaybackStateCompat.Builder stateBuilder;
    private MediaSource mediaSource;
    private long videoPosition;
    private boolean videoState;

    public RecipeStepDetailViewFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(
                R.layout.fragment_recipe_step_detail_view, container, false);
        ButterKnife.bind(this, rootView);

        // set question mark image if no video Url
        playerView.setDefaultArtwork(
                BitmapFactory.decodeResource(getResources(), R.drawable.question_mark)
        );

        if (savedInstanceState == null) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                stepId = arguments.getLong(STEP_ID, DEFAULT_VALUE);
                steps = (ArrayList<Step>) arguments.getSerializable(STEPS);
            }
        } else {
            stepId = savedInstanceState.getLong(STEP_ID);
            steps = (ArrayList<Step>) savedInstanceState.getSerializable(STEPS);
            videoPosition = savedInstanceState.getLong(VIDEO_POSITION);
            videoState = savedInstanceState.getBoolean(VIDEO_STATE);
        }

        if (steps != null) {
            initializeIterator();
            setIteratorCursor();
            setInitialButtonsVisibility();
            // Initialize the Media Session.
            initializeMediaSession();
            // Initialize the player.
            Step step = prepareSelectedStep();
            initializePlayer(step);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STEP_ID, stepId);
        outState.putSerializable(STEPS, (ArrayList<Step>) steps);
        if (exoPlayer != null) {
            outState.putLong(VIDEO_POSITION, exoPlayer.getCurrentPosition());
            outState.putBoolean(VIDEO_STATE, exoPlayer.getPlayWhenReady());
        }
    }

    /**
     * Initialize ExoPlayer.
     * @param step The Step object with URI of the sample to play.
     */
    private void initializePlayer(Step step) {
        if (exoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            playerView.setPlayer(exoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            exoPlayer.addListener(this);

            // Prepare the MediaSource.
            setStepVideo(step);
            // Set playback position
            if (videoPosition == START_POSITION) {
                exoPlayer.setPlayWhenReady(true);
            } else {
                exoPlayer.seekTo(videoPosition);
                exoPlayer.setPlayWhenReady(videoState);
            }
        }
    }

    /**
     * Set new media Uri for media source.
     * @param mediaUri The URI of the sample to play.
     */
    private void setMediaUri(Uri mediaUri) {
        String userAgent = Util.getUserAgent(getActivity(), APPLICATION_NAME);
        mediaSource = new ExtractorMediaSource(
                mediaUri,
                new DefaultDataSourceFactory(getActivity(), userAgent),
                new DefaultExtractorsFactory(),
                null,
                null
        );
        exoPlayer.prepare(mediaSource);
    }

    /**
     * Prepare URI for step's video
     * @param step
     */
    private void setStepVideo(Step step) {
        String videoUrl = VideoUtil.urlForPlayback(step);
        if (TextUtils.isEmpty(videoUrl)) {
            showSnackbar(getString(R.string.no_video_available_msg));
            // Remove playback controllers
            playerView.setUseController(false);
        } else {
            playerView.setUseController(true);
        }
        setMediaUri(Uri.parse(videoUrl));
    }

    private void showSnackbar(String message) {
        if (getActivity() == null) return;
        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar snackBar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackBar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER;
        snackBarView.setLayoutParams(params);
        snackBarView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        snackBar.show();
    }

    // ExoPlayer Event Listeners
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync, and post the media notification.
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    exoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    exoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_ENDED)) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    exoPlayer.getCurrentPosition(), 1f);
            exoPlayer.seekTo(START_POSITION);
            exoPlayer.setPlayWhenReady(false);
        }
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d(LOG_TAG, "Error occurred while playing: " + error.getLocalizedMessage());
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mediaSession = new MediaSessionCompat(getActivity(), LOG_TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession.setPlaybackState(stateBuilder.build());

        // Start the Media Session since the activity is active.
        mediaSession.setActive(true);
    }

    /**
     * Initialize iterator to iterate through steps collection
     */
    private void initializeIterator() {
        this.iterator = steps.listIterator();
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    private void setIteratorCursor() {
        while (iterator.hasNext()) {
            Step step = iterator.next();
            if (stepId == step.id) {
                setStepInstructions(step);
                stepId = step.id;
                break;
            }
        }
    }

    @Optional
    @OnClick(R.id.backBtn)
    public void onBack() {
        if (iterator.hasPrevious()) {
            Step step = iterator.previous();
            if (step.id == stepId) {
                if (iterator.hasPrevious()) {
                    step = iterator.previous();
                }
            }
            setStepInstructions(step);
            setStepVideo(step);
            resetVideoState();
            stepId = step.id;
            setButtonsVisibility();
        }
    }

    @Optional
    @OnClick(R.id.nextBtn)
    public void onNext() {
        if (iterator.hasNext()) {
            Step step = iterator.next();
            if (step.id == stepId) {
                if (iterator.hasNext()) {
                    step = iterator.next();
                }
            }
            setStepInstructions(step);
            setStepVideo(step);
            resetVideoState();
            stepId = step.id;
            setButtonsVisibility();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private Step prepareSelectedStep() {
        Step step = null;
        for (Step s : steps) {
            if (s.id == stepId) {
                step = s;
                break;
            }
        }
        return step;
    }

    private void setStepInstructions(Step step) {
        String instructions = step.description;
        if (recipeStepInstructionTv != null) {
            recipeStepInstructionTv.setText(instructions);
        }
    }

    private void resetVideoState() {
        exoPlayer.seekTo(START_POSITION);
        exoPlayer.setPlayWhenReady(true);
    }

    private void setInitialButtonsVisibility() {
        if (iterator.hasPrevious()) {
            if (backBtn != null) {
                iterator.previous();
                backBtn.setVisibility(iterator.hasPrevious() ? View.VISIBLE : View.GONE);
                iterator.next();
            }
        } else {
            if (backBtn != null) {
                backBtn.setVisibility(View.GONE);
            }
        }
        if (nextBtn != null) {
            nextBtn.setVisibility(iterator.hasNext() ? View.VISIBLE : View.GONE);
        }
    }

    private void setButtonsVisibility() {
        if (backBtn != null) {
            backBtn.setVisibility(iterator.hasPrevious() ? View.VISIBLE : View.GONE);
        }
        if (nextBtn != null) {
            nextBtn.setVisibility(iterator.hasNext() ? View.VISIBLE : View.GONE);
        }
    }
}
