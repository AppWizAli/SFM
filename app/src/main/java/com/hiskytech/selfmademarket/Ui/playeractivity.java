package com.hiskytech.selfmademarket.Ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class playeractivity extends AppCompatActivity {
    private static final String TAG = playeractivity.class.getSimpleName();
    private DefaultTrackSelector trackSelector;
    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView playerView;
    private String videoUrl1 = "";
    private String videoUrl2 = "";
    private String videoUrl3 = "";
    private long currentPosition = 0; // To store current playback position
    private Handler handler;
    private boolean isShowingTrackSelectionDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.getWindowInsetsController(getWindow().getDecorView()).hide(WindowInsetsCompat.Type.statusBars());

        setContentView(R.layout.activity_playeractivity);

        handler = new Handler(Looper.getMainLooper());
        Intent intent = getIntent();

        videoUrl1 = intent.getStringExtra("videourl1");
        videoUrl2 = intent.getStringExtra("videourl2");
        videoUrl3 = intent.getStringExtra("videourl3");
        setupUIControls();

        measureNetworkSpeed(speedMbps -> {
            String videoUrl;
            if (speedMbps >= 5) {
                videoUrl = "https://en.selfmademarket.net/planemanger/uploads/"+videoUrl1; // High quality
            } else if (speedMbps >= 3) {
                videoUrl = "https://en.selfmademarket.net/planemanger/uploads/"+videoUrl2;  // Medium quality
            } else {
                videoUrl = "https://en.selfmademarket.net/planemanger/uploads/"+videoUrl3;  // Low quality
            }
            initializePlayer(videoUrl);
        });

        // Restore playback position if available
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getLong("currentPosition", 0);
        }
    }

    private void initializePlayer(String videoUrl) {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
        }
        trackSelector = new DefaultTrackSelector(this);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .setLoadControl(new DefaultLoadControl())
                .build();
        playerView.setPlayer(simpleExoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        simpleExoPlayer.setMediaItem(mediaItem);
        simpleExoPlayer.prepare();
        simpleExoPlayer.setPlayWhenReady(true);

        // Resume playback from the saved position
        if (currentPosition != 0) {
            simpleExoPlayer.seekTo(currentPosition);
        }
    }

    // Simple network speed measurement function
    private void measureNetworkSpeed(final SpeedCallback callback) {
        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                URL url = new URL("https://example.com/testfile"); // Use a small test file
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                try (InputStream inputStream = connection.getInputStream()) {
                    byte[] buffer = new byte[1024];
                    while (inputStream.read(buffer) != -1) {
                        // Just reading the stream
                    }
                }

                long endTime = System.currentTimeMillis();
                int fileSizeBytes = connection.getContentLength();
                double timeTakenSecs = (endTime - startTime) / 1000.0;
                double speedMbps = (fileSizeBytes * 8) / (timeTakenSecs * 1000 * 1000); // Convert to Mbps

                runOnUiThread(() -> callback.onSpeedMeasured(speedMbps));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> callback.onSpeedMeasured(1.0)); // Default to low quality on error
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (simpleExoPlayer != null) {
            currentPosition = simpleExoPlayer.getCurrentPosition();
            simpleExoPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.seekTo(currentPosition);
            simpleExoPlayer.play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("currentPosition", currentPosition);
    }

    private void setupUIControls() {
        playerView = findViewById(R.id.exoPlayerView);
        ImageView forwardBtn = playerView.findViewById(R.id.fwd);
        ImageView rewindBtn = playerView.findViewById(R.id.rew);
        TextView speedBtn = playerView.findViewById(R.id.exo_playback_speed);
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

        exoplayer_resize1.setOnClickListener(v -> {
            exoplayer_resize1.setVisibility(View.GONE);
            exoplayer_resize2.setVisibility(View.VISIBLE);
            exoplayer_resize3.setVisibility(View.GONE);
            exoplayer_resize4.setVisibility(View.GONE);
            exoplayer_resize5.setVisibility(View.GONE);

            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            Toast.makeText(playeractivity.this, "Fill Mode", Toast.LENGTH_SHORT).show();
        });

        exoplayer_resize2.setOnClickListener(v -> {
            exoplayer_resize1.setVisibility(View.GONE);
            exoplayer_resize2.setVisibility(View.GONE);
            exoplayer_resize3.setVisibility(View.VISIBLE);
            exoplayer_resize4.setVisibility(View.GONE);
            exoplayer_resize5.setVisibility(View.GONE);

            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            Toast.makeText(playeractivity.this, "Fit Mode", Toast.LENGTH_SHORT).show();
        });

        exoplayer_resize3.setOnClickListener(v -> {
            exoplayer_resize1.setVisibility(View.GONE);
            exoplayer_resize2.setVisibility(View.GONE);
            exoplayer_resize3.setVisibility(View.GONE);
            exoplayer_resize4.setVisibility(View.VISIBLE);
            exoplayer_resize5.setVisibility(View.GONE);

            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            Toast.makeText(playeractivity.this, "Zoom Mode", Toast.LENGTH_SHORT).show();
        });

        exoplayer_resize4.setOnClickListener(v -> {
            exoplayer_resize1.setVisibility(View.GONE);
            exoplayer_resize2.setVisibility(View.GONE);
            exoplayer_resize3.setVisibility(View.GONE);
            exoplayer_resize4.setVisibility(View.GONE);
            exoplayer_resize5.setVisibility(View.VISIBLE);

            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
            Toast.makeText(playeractivity.this, "Fixed Height Mode", Toast.LENGTH_SHORT).show();
        });

        exoplayer_resize5.setOnClickListener(v -> {
            exoplayer_resize1.setVisibility(View.VISIBLE);
            exoplayer_resize2.setVisibility(View.GONE);
            exoplayer_resize3.setVisibility(View.GONE);
            exoplayer_resize4.setVisibility(View.GONE);
            exoplayer_resize5.setVisibility(View.GONE);

            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            Toast.makeText(playeractivity.this, "Fixed Width Mode", Toast.LENGTH_SHORT).show();
        });
    }
    private void showSpeedDialog() {
        // Implement a dialog for selecting playback speed
        String[] speedOptions = {"1080p", "720p", "420p"};
        new android.app.AlertDialog.Builder(this)
                .setTitle("Select Video Quality")
                .setItems(speedOptions, (dialog, which) -> {
                    // Save the current playback position before changing quality
                    if (simpleExoPlayer != null) {
                        currentPosition = simpleExoPlayer.getCurrentPosition();
                    }
                    String selectedUrl;
                    switch (which) {
                        case 0:
                            selectedUrl = "https://en.selfmademarket.net/planemanger/uploads/" + videoUrl1; // 1080p
                            break;
                        case 1:
                            selectedUrl = "https://en.selfmademarket.net/planemanger/uploads/" + videoUrl2; // 720p
                            break;
                        case 2:
                        default:
                            selectedUrl = "https://en.selfmademarket.net/planemanger/uploads/" + videoUrl3; // 420p
                            break;
                    }
                    initializePlayer(selectedUrl);
                })
                .show();
    }
    // Define a callback interface for network speed measurement
    private interface SpeedCallback {
        void onSpeedMeasured(double speedMbps);
    }

    // Check if the device has a fast network
    private boolean hasFastNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);

        if (capabilities != null) {
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) &&
                            (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ||
                                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)));
        }

        return false;
    }
}
