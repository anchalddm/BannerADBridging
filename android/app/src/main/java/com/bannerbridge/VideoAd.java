package com.bannerbridge;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.view.ViewGroup.LayoutParams;
import com.facebook.react.bridge.ReactContext;
import com.google.android.gms.ads.*;
import com.google.android.gms.ads.nativead.*;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import android.app.Activity;
import android.view.Gravity;
import android.graphics.Color;

public class VideoAd extends FrameLayout {
    private static final String TAG = "VideoAd";
    private static final String TEST_VIDEO_AD_UNIT = "/3865/ddm.people.app/feed/app-flex-1";
    private static final String NATIVE_CUSTOM_FORMAT_ID = "12420333";
    
    private static final String ASSET_HEADING = "headline";
    private static final String ASSET_SUBTEXT = "heading";
    private static final String ASSET_CALL_TO_ACTION = "callToAction";
    
    private NativeCustomFormatAd nativeCustomFormatAd;
    private MediaView mediaView;
    private TextView headingTextView, subtextTextView;
    private Button ctaButton;
    private ImageView muteButton;
    private boolean isMuted = false;
    private TextView statusText;
    private FrameLayout adContainer;
    private VideoController currentVideoController;

    public VideoAd(Context context) {
        super(context);
        Log.d(TAG, "Initializing VideoAd");
        setupViews();
        setupVideoAd();
    }

    private void setupViews() {
        // Create status text
        statusText = new TextView(getContext());
        statusText.setText("Loading Video Ad...");
        statusText.setTextSize(18);
        statusText.setTextColor(Color.BLACK);
        statusText.setGravity(Gravity.CENTER);
        addView(statusText, new FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.TOP | Gravity.CENTER_HORIZONTAL
        ));

        // Create ad container
        adContainer = new FrameLayout(getContext());
        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        );
        containerParams.gravity = Gravity.CENTER;
        adContainer.setLayoutParams(containerParams);
        addView(adContainer);

        // Create MediaView
        mediaView = new MediaView(getContext());
        mediaView.setLayoutParams(new FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        ));
        adContainer.addView(mediaView);

        // Create UI elements
        setupUIElements();
    }

    private void setupUIElements() {
        // Heading TextView
        headingTextView = new TextView(getContext());
        headingTextView.setTextSize(18);
        headingTextView.setTextColor(Color.WHITE);
        FrameLayout.LayoutParams headingParams = new FrameLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.TOP | Gravity.CENTER_HORIZONTAL
        );
        headingParams.topMargin = 16;
        adContainer.addView(headingTextView, headingParams);
        
        // Subtext TextView
        subtextTextView = new TextView(getContext());
        subtextTextView.setTextSize(14);
        subtextTextView.setTextColor(Color.WHITE);
        FrameLayout.LayoutParams subtextParams = new FrameLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.TOP | Gravity.CENTER_HORIZONTAL
        );
        subtextParams.topMargin = 48;
        adContainer.addView(subtextTextView, subtextParams);
        
        // CTA Button
        ctaButton = new Button(getContext());
        ctaButton.setBackgroundColor(Color.BLUE);
        ctaButton.setTextColor(Color.WHITE);
        FrameLayout.LayoutParams ctaParams = new FrameLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
        );
        ctaParams.bottomMargin = 16;
        adContainer.addView(ctaButton, ctaParams);
        
        // Mute Button
        muteButton = new ImageView(getContext());
        muteButton.setBackgroundColor(Color.parseColor("#80000000"));
        muteButton.setOnClickListener(v -> toggleMute());
        FrameLayout.LayoutParams muteParams = new FrameLayout.LayoutParams(80, 80);
        muteParams.gravity = Gravity.TOP | Gravity.END;
        muteParams.topMargin = 16;
        muteParams.rightMargin = 16;
        adContainer.addView(muteButton, muteParams);
    }

    private void setupVideoAd() {
        final Activity currentActivity;
        if (getContext() instanceof ReactContext) {
            currentActivity = ((ReactContext) getContext()).getCurrentActivity();
        } else {
            currentActivity = null;
        }
        
        if (currentActivity == null) {
            Log.e(TAG, "No activity found");
            statusText.setText("Error: No activity found");
            return;
        }

        MobileAds.initialize(currentActivity, initializationStatus -> {
            Log.d(TAG, "MobileAds initialized");
            createAndLoadAd(currentActivity);
        });
    }

    private void createAndLoadAd(Activity activity) {
        VideoOptions videoOptions = new VideoOptions.Builder()
            .setStartMuted(false)
            .setCustomControlsRequested(true)
            .build();
            
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build();
        
        AdManagerAdRequest.Builder adRequestBuilder = new AdManagerAdRequest.Builder();
        adRequestBuilder.addCustomTargeting("advertest", "moonshotnative");
        
        AdLoader videoAdLoader = new AdLoader.Builder(activity, TEST_VIDEO_AD_UNIT)
            .forCustomFormatAd(
                NATIVE_CUSTOM_FORMAT_ID,
                ad -> {
                    Log.d(TAG, "Custom format ad loaded successfully");
                    displayAd(ad);
                    statusText.setVisibility(View.GONE);
                    adContainer.setVisibility(View.VISIBLE);
                    requestLayout();
                },
                this::handleCustomClick
            )
            .withNativeAdOptions(adOptions)
            .withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(LoadAdError error) {
                    Log.e(TAG, "Video ad failed to load: " + error.getMessage());
                    statusText.setText("Ad Failed to Load: " + error.getMessage());
                    adContainer.setVisibility(View.GONE);
                }
            })
            .build();

        Log.d(TAG, "Loading video ad...");
        videoAdLoader.loadAd(adRequestBuilder.build());
    }

    private void handleCustomClick(NativeCustomFormatAd ad, String assetName) {
        Log.d(TAG, "Custom click on asset: " + assetName);
    }

    private void displayAd(NativeCustomFormatAd ad) {
        if (nativeCustomFormatAd != null) {
            nativeCustomFormatAd.destroy();
        }
        nativeCustomFormatAd = ad;
        
        headingTextView.setText(ad.getText(ASSET_HEADING));
        subtextTextView.setText(ad.getText(ASSET_SUBTEXT));
        ctaButton.setText(ad.getText(ASSET_CALL_TO_ACTION));
        
        MediaContent mediaContent = ad.getMediaContent();
        if (mediaContent != null && mediaContent.hasVideoContent()) {
            mediaView.setMediaContent(mediaContent);
            
            VideoController videoController = mediaContent.getVideoController();
            videoController.mute(isMuted);
            
            videoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Reset and replay without view modifications
                    mediaView.setMediaContent(mediaContent);
                    videoController.mute(isMuted);
                    videoController.play();
                }
                
                @Override
                public void onVideoMute(boolean muted) {
                    isMuted = muted;
                }
            });
            
            // Store controller reference for reuse
            currentVideoController = videoController;
            
            // Start initial playback
            videoController.play();
        }
        
        ad.recordImpression();
    }

    private void toggleMute() {
        if (nativeCustomFormatAd != null) {
            MediaContent mediaContent = nativeCustomFormatAd.getMediaContent();
            if (mediaContent != null && mediaContent.hasVideoContent()) {
                VideoController videoController = mediaContent.getVideoController();
                isMuted = !isMuted;
                videoController.mute(isMuted);
            }
        }
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        post(() -> {
            measure(
                MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY)
            );
            layout(getLeft(), getTop(), getRight(), getBottom());
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (nativeCustomFormatAd != null) {
            nativeCustomFormatAd.destroy();
            nativeCustomFormatAd = null;
        }
    }
} 