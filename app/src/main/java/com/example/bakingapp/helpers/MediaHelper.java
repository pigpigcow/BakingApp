package com.example.bakingapp.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageView;

import androidx.media.session.MediaButtonReceiver;

import com.example.bakingapp.R;
import com.example.bakingapp.recipe.Recipe;
import com.example.bakingapp.recipe.Step;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MediaHelper implements ExoPlayer.EventListener {
    public static final String POSITION_KEY = "position_key";
    public static final String STATE_KEY = "state_key";
    public static final String PLAY_WHEN_READY_KEY = "play_when_ready_key";
    private PlaybackStateCompat.Builder mStateBuilder;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

    private static MediaSessionCompat mMediaSession;

    Context context;

    public MediaHelper(Step step, SimpleExoPlayerView mPlayerView, ImageView imageView, Context context) {
        this(step.getVideoURL(), step.getThumbnailURL(), mPlayerView, imageView, context);
    }

    public MediaHelper(String mediaUrl, String imageUrl, SimpleExoPlayerView mPlayerView, ImageView imageView, Context context) {
        this(StringHelper.isValid(mediaUrl) ? Uri.parse(mediaUrl) : null, StringHelper.isValid(imageUrl) ? Uri.parse(imageUrl) : null, mPlayerView, imageView, context);
    }

    public MediaHelper(Uri mediaUri, Uri imageUri, SimpleExoPlayerView mPlayerView, ImageView imageView, Context context) {
        this.mPlayerView = mPlayerView;
        this.context = context;

        if (imageUri != null) {
            imageView.setVisibility(View.VISIBLE);
            mPlayerView.setVisibility(View.INVISIBLE);
            Picasso.with(context)
                    .load(imageUri)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher_round)
                    .into(imageView);
        } else if(mediaUri != null) {
            imageView.setVisibility(View.INVISIBLE);
            mPlayerView.setVisibility(View.VISIBLE);
            initializeMediaSession(this.getClass().getSimpleName());
            initializePlayer(mediaUri);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if(mExoPlayer != null) {
            if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
                mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                        mExoPlayer.getCurrentPosition(), 1f);
            } else if ((playbackState == ExoPlayer.STATE_READY)) {
                mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                        mExoPlayer.getCurrentPosition(), 1f);
            }
            mMediaSession.setPlaybackState(mStateBuilder.build());
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    context, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    public void setupPlayer(boolean playerWhenReady, int playerState, long playerPostion) {
        if(mediaExist()) {
            if(playerPostion > -1) {
                mExoPlayer.seekTo(playerPostion);
            }

            if(playerState > -1) {
                onPlayerStateChanged( playerWhenReady ,playerState);
            }
        }
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        SimpleExoPlayer mExoPlayer;

        public MySessionCallback(SimpleExoPlayer mExoPlayer) {
            super();
            this.mExoPlayer = mExoPlayer;
        }

        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    private void initializeMediaSession(String tag) {
        mMediaSession = new MediaSessionCompat(context, tag);
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);

        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback(mExoPlayer));

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    public static class MediaReceiver extends BroadcastReceiver {
        MediaSessionCompat mMediaSession;

        public MediaReceiver(MediaSessionCompat mMediaSession) {
            this.mMediaSession = mMediaSession;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    public void onDestroy() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mMediaSession.setActive(false);
        }
    }

    public boolean mediaExist() {
        return mExoPlayer != null;
    }

    public SimpleExoPlayer getPlayer() {
        return mExoPlayer;
    }
}
