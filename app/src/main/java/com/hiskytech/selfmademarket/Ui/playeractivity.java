package com.hiskytech.selfmademarket.Ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.hiskytech.selfmademarket.Model.TrackSelectionDialog;
import com.hiskytech.selfmademarket.R;

public class playeractivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DefaultTrackSelector trackSelector;
    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView playerView;
    private TextView qualityTxt;
    private final String[] speedOptions = {"0.25x", "0.5x", "Normal", "1.5x", "2x"};
    private String videoUrl = "";
    private long currentPosition = 0; // To store current playback position
    private Handler handler;
    private Runnable updatePositionRunnable;
    private boolean isShowingTrackSelectionDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playeractivity);

        handler = new Handler(Looper.getMainLooper());
        Intent intent = getIntent();

        videoUrl = intent.getStringExtra("videourl");
        setupUIControls();
        setupExoPlayer();

        // Restore playback position if available
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getLong("currentPosition", 0);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("currentPosition", currentPosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void setupUIControls() {
        playerView = findViewById(R.id.exoPlayerView);
        ImageView forwardBtn = playerView.findViewById(R.id.fwd);
        ImageView rewindBtn = playerView.findViewById(R.id.rew);
        ImageView speedBtn = playerView.findViewById(R.id.exo_playback_speed);
        ImageView fullscreenBtn = playerView.findViewById(R.id.fullscreen);
        ImageView exoplayer_resize1 = playerView.findViewById(R.id.exoplayer_resize1);
        ImageView exoplayer_resize2 = playerView.findViewById(R.id.exoplayer_resize2);
        ImageView exoplayer_resize3 = playerView.findViewById(R.id.exoplayer_resize3);
        ImageView exoplayer_resize4 = playerView.findViewById(R.id.exoplayer_resize4);
        ImageView exoplayer_resize5 = playerView.findViewById(R.id.exoplayer_resize5);
        ImageView backBtn = playerView.findViewById(R.id.backExo);
        ImageView quality = playerView.findViewById(R.id.exo_track_selection_view);

        backBtn.setOnClickListener(view -> finish());

        speedBtn.setOnClickListener(v -> showSpeedDialog());

        forwardBtn.setOnClickListener(v -> {
            if (simpleExoPlayer != null) {
                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000);
            }
        });

        rewindBtn.setOnClickListener(v -> {
            if (simpleExoPlayer != null) {
                simpleExoPlayer.seekTo(Math.max(simpleExoPlayer.getCurrentPosition() - 10000, 0));
            }
        });

        quality.setOnClickListener(view -> {
            Log.d(TAG, "Quality button clicked");
            if (!isShowingTrackSelectionDialog && TrackSelectionDialog.willHaveContent(trackSelector)) {
                Log.d(TAG, "Showing TrackSelectionDialog");
                isShowingTrackSelectionDialog = true;
                TrackSelectionDialog trackSelectionDialog = TrackSelectionDialog.createForTrackSelector(trackSelector,
                        dismissDialog -> isShowingTrackSelectionDialog = false);
                trackSelectionDialog.show(getSupportFragmentManager(), null);
            } else {
                Log.d(TAG, "TrackSelectionDialog not shown");
            }
        });

        exoplayer_resize1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoplayer_resize1.setVisibility(View.GONE);
                exoplayer_resize2.setVisibility(View.VISIBLE);
                exoplayer_resize3.setVisibility(View.GONE);
                exoplayer_resize4.setVisibility(View.GONE);
                exoplayer_resize5.setVisibility(View.GONE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                Toast.makeText(playeractivity.this, "Fill Mode", Toast.LENGTH_SHORT).show();
            }
        });
        exoplayer_resize2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoplayer_resize1.setVisibility(View.GONE);
                exoplayer_resize2.setVisibility(View.GONE);
                exoplayer_resize3.setVisibility(View.VISIBLE);
                exoplayer_resize4.setVisibility(View.GONE);
                exoplayer_resize5.setVisibility(View.GONE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                Toast.makeText(playeractivity.this, "Fit Mode", Toast.LENGTH_SHORT).show();
            }
        });
        exoplayer_resize3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoplayer_resize1.setVisibility(View.GONE);
                exoplayer_resize2.setVisibility(View.GONE);
                exoplayer_resize3.setVisibility(View.GONE);
                exoplayer_resize4.setVisibility(View.VISIBLE);
                exoplayer_resize5.setVisibility(View.GONE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                Toast.makeText(playeractivity.this, "Zoom Mode", Toast.LENGTH_SHORT).show();
            }
        });
        exoplayer_resize4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoplayer_resize1.setVisibility(View.GONE);
                exoplayer_resize2.setVisibility(View.GONE);
                exoplayer_resize3.setVisibility(View.GONE);
                exoplayer_resize4.setVisibility(View.GONE);
                exoplayer_resize5.setVisibility(View.VISIBLE);
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                Toast.makeText(playeractivity.this, "Fixed Height", Toast.LENGTH_SHORT).show();
            }
        });
        exoplayer_resize5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoplayer_resize1.setVisibility(View.VISIBLE);
                exoplayer_resize2.setVisibility(View.GONE);
                exoplayer_resize3.setVisibility(View.GONE);
                exoplayer_resize4.setVisibility(View.GONE);
                exoplayer_resize5.setVisibility(View.GONE);

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                Toast.makeText(playeractivity.this, "Fixed Width", Toast.LENGTH_SHORT).show();
            }
        });

        fullscreenBtn.setOnClickListener(view -> toggleFullscreen());

        playerView.findViewById(R.id.exo_play).setOnClickListener(v -> {
            if (simpleExoPlayer != null) {
                simpleExoPlayer.play();
            }
        });

        playerView.findViewById(R.id.exo_pause).setOnClickListener(v -> {
            if (simpleExoPlayer != null && simpleExoPlayer.isPlaying()) {
                simpleExoPlayer.pause();
                currentPosition = simpleExoPlayer.getCurrentPosition(); // Save current position when paused
                cancelUpdatePositionRunnable(); // Cancel any ongoing position update
            }
        });
    }

    private void setupExoPlayer() {
        trackSelector = new DefaultTrackSelector(this);
        DefaultTrackSelector.Parameters parameters = trackSelector.buildUponParameters().setForceLowestBitrate(true).build();
        trackSelector.setParameters(parameters);

        // Initialize ExoPlayer
        simpleExoPlayer = new SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .setLoadControl(new DefaultLoadControl())
                .build();

        playerView.setPlayer(simpleExoPlayer);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

        MediaItem mediaItem = MediaItem.fromUri("https://hiskytechs.com/planemanger/get-course.php" + videoUrl);
        simpleExoPlayer.setMediaItem(mediaItem);

        simpleExoPlayer.prepare();
        simpleExoPlayer.seekTo(currentPosition); // Seek to saved position if any
        simpleExoPlayer.play();

        // Update playback position
        updatePositionRunnable = new Runnable() {
            @Override
            public void run() {
                if (simpleExoPlayer != null) {
                    currentPosition = simpleExoPlayer.getCurrentPosition();
                }
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(updatePositionRunnable);

        simpleExoPlayer.addListener(new com.google.android.exoplayer2.Player.Listener() {

            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(playeractivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
        cancelUpdatePositionRunnable(); // Cancel position updates when releasing player
    }

    private void cancelUpdatePositionRunnable() {
        if (handler != null && updatePositionRunnable != null) {
            handler.removeCallbacks(updatePositionRunnable);
        }
    }

    private void showSpeedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(playeractivity.this);
        builder.setTitle("Select Speed");

        builder.setItems(speedOptions, (dialog, which) -> {
            if (simpleExoPlayer != null) {
                float speed = 1f;
                switch (which) {
                    case 0:
                        speed = 0.25f;
                        break;
                    case 1:
                        speed = 0.5f;
                        break;
                    case 2:
                        speed = 1f;
                        break;
                    case 3:
                        speed = 1.5f;
                        break;
                    case 4:
                        speed = 2f;
                        break;
                }
                PlaybackParameters playbackParameters = new PlaybackParameters(speed);
                simpleExoPlayer.setPlaybackParameters(playbackParameters);
                Toast.makeText(playeractivity.this, "Playback Speed: " + speedOptions[which], Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void toggleFullscreen() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Toast.makeText(playeractivity.this, "Portrait mode", Toast.LENGTH_SHORT).show();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Toast.makeText(playeractivity.this, "Landscape mode", Toast.LENGTH_SHORT).show();
        }
    }

    private void pausePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.pause();
            currentPosition = simpleExoPlayer.getCurrentPosition(); // Save current position when paused
        }
    }

    private void resumePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.seekTo(currentPosition); // Seek to saved position when resuming
            simpleExoPlayer.play();
            handler.post(updatePositionRunnable); // Resume position updates
        }
    }
}
