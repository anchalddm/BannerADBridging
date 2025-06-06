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
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.Toast;

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
    private ImageView placeholderView;

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
        // This container will hold both the text card and the controls row
        LinearLayout bottomControlsContainer = new LinearLayout(getContext());
        bottomControlsContainer.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams bottomContainerParams = new FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM
        );
        bottomControlsContainer.setLayoutParams(bottomContainerParams);

        // --- Card for Heading and Subheading ---
        LinearLayout textOverlayCard = new LinearLayout(getContext());
        textOverlayCard.setOrientation(LinearLayout.VERTICAL);
        int overlayPadding = (int) (24 * getResources().getDisplayMetrics().density);
        textOverlayCard.setPadding(overlayPadding, overlayPadding, overlayPadding, overlayPadding);
        textOverlayCard.setBackground(createRoundedBackground());
        LinearLayout.LayoutParams textCardParams = new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        );
        textCardParams.leftMargin = (int) (16 * getResources().getDisplayMetrics().density);
        textCardParams.rightMargin = (int) (16 * getResources().getDisplayMetrics().density);
        textOverlayCard.setLayoutParams(textCardParams);

        headingTextView = new TextView(getContext());
        headingTextView.setTextSize(22);
        headingTextView.setTypeface(Typeface.DEFAULT_BOLD);
        headingTextView.setTextColor(Color.WHITE);
        textOverlayCard.addView(headingTextView);

        subtextTextView = new TextView(getContext());
        subtextTextView.setTextSize(15);
        subtextTextView.setTextColor(Color.WHITE);
        subtextTextView.setAlpha(0.9f);
        LinearLayout.LayoutParams subtextLpParams = new LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        subtextLpParams.topMargin = (int) (8 * getResources().getDisplayMetrics().density);
        textOverlayCard.addView(subtextTextView, subtextLpParams);
        
        bottomControlsContainer.addView(textOverlayCard);

        // --- Row for CTA and Mute Button ---
        RelativeLayout controlsRowLayout = new RelativeLayout(getContext());
        LinearLayout.LayoutParams controlsRowParams = new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        );
        controlsRowParams.topMargin = (int) (12 * getResources().getDisplayMetrics().density); // Space between text card and controls row
        controlsRowParams.leftMargin = (int) (16 * getResources().getDisplayMetrics().density);
        controlsRowParams.rightMargin = (int) (16 * getResources().getDisplayMetrics().density);
        controlsRowParams.bottomMargin = (int) (24 * getResources().getDisplayMetrics().density); // Bottom margin for the whole controls block
        controlsRowLayout.setLayoutParams(controlsRowParams);

        // CTA Button
        ctaButton = new Button(getContext());
        ctaButton.setTextColor(Color.WHITE);
        ctaButton.setTextSize(15);
        ctaButton.setTypeface(Typeface.DEFAULT_BOLD);
        ctaButton.setBackground(createCtaBackground());
        int ctaHorizontalPadding = (int) (20 * getResources().getDisplayMetrics().density);
        int ctaVerticalPadding = (int) (10 * getResources().getDisplayMetrics().density);
        ctaButton.setPadding(ctaHorizontalPadding, ctaVerticalPadding, ctaHorizontalPadding, ctaVerticalPadding);
        ctaButton.setAllCaps(false);
        ctaButton.setGravity(Gravity.CENTER); // Ensure text and arrow are centered

        RelativeLayout.LayoutParams ctaRlParams = new RelativeLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ctaRlParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        ctaRlParams.addRule(RelativeLayout.CENTER_VERTICAL);
        ctaButton.setLayoutParams(ctaRlParams);
        controlsRowLayout.addView(ctaButton);

        // CTA Button Click Listener
        ctaButton.setOnClickListener(v -> {
            // Show an auto-dismissible Toast message
            Toast.makeText(getContext(), "CTA Clicked! Opening link...", Toast.LENGTH_SHORT).show();
            
            if (nativeCustomFormatAd != null) {
                nativeCustomFormatAd.performClick(ASSET_CALL_TO_ACTION);
            } else {
                Log.w(TAG, "CTA button clicked, but nativeCustomFormatAd is null.");
            }
        });

        // Mute Button
        muteButton = new ImageView(getContext());
        muteButton.setImageResource(isMuted ? R.drawable.ic_volume_off : R.drawable.ic_volume_on);
        GradientDrawable muteBgShape = new GradientDrawable();
        muteBgShape.setShape(GradientDrawable.OVAL);
        muteBgShape.setColor(Color.argb(150, 0, 0, 0));
        muteButton.setBackground(muteBgShape);
        int buttonSize = (int) (40 * getResources().getDisplayMetrics().density);
        int padding = (int) (8 * getResources().getDisplayMetrics().density);
        muteButton.setPadding(padding, padding, padding, padding);
        RelativeLayout.LayoutParams muteRlParams = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
        muteRlParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        muteRlParams.addRule(RelativeLayout.CENTER_VERTICAL);
        muteButton.setLayoutParams(muteRlParams);
        controlsRowLayout.addView(muteButton);
        muteButton.setOnClickListener(v -> toggleMute());

        bottomControlsContainer.addView(controlsRowLayout);
        adContainer.addView(bottomControlsContainer);
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

                    // Log available assets
                    if (ad.getAvailableAssetNames() != null) {
                        Log.d(TAG, "Available ad assets:");
                        for (String assetName : ad.getAvailableAssetNames()) {
                            CharSequence textAsset = ad.getText(assetName);
                            NativeCustomFormatAd.DisplayOpenMeasurement displayOpenMeasurement = ad.getDisplayOpenMeasurement();
                            MediaContent mediaContent = ad.getMediaContent();

                            if (textAsset != null) {
                                Log.d(TAG, "  Asset Name: " + assetName + ", Type: Text, Value: " + textAsset.toString());
                            } else if (ad.getImage(assetName) != null) {
                                Log.d(TAG, "  Asset Name: " + assetName + ", Type: Image");
                            } else if (assetName.equals("media") && mediaContent != null) { // Common name for media content
                                Log.d(TAG, "  Asset Name: " + assetName + ", Type: MediaContent, Has Video: " + mediaContent.hasVideoContent());
                            } else if (displayOpenMeasurement != null && assetName.contains("display_open_measurement")) { // Example check
                                Log.d(TAG, "  Asset Name: " + assetName + ", Type: DisplayOpenMeasurement");
                            }else {
                                Log.d(TAG, "  Asset Name: " + assetName + ", Type: Unknown or not directly loggable as text/image");
                            }
                        }
                    } else {
                        Log.d(TAG, "No available asset names found.");
                    }

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
        
        // Use "headline" asset for the heading
        CharSequence headlineCharSequence = ad.getText("headline"); // Use the key "headline"
        String headlineText = "";
        if (headlineCharSequence != null) {
            headlineText = headlineCharSequence.toString().trim();
        }

        // Hide if headline is empty or literally "Test Ad : null"
        if (!headlineText.isEmpty() && !"Test Ad : null".equalsIgnoreCase(headlineText)) {
            headingTextView.setText(headlineText);
            headingTextView.setVisibility(View.VISIBLE);
        } else {
            headingTextView.setVisibility(View.GONE); 
        }

        // Subtext and CTA remain the same, using their defined asset keys
        subtextTextView.setText(ad.getText(ASSET_SUBTEXT));
        ctaButton.setText(ad.getText(ASSET_CALL_TO_ACTION) + " →");

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
                muteButton.setImageResource(isMuted ? 
                    R.drawable.ic_volume_off : R.drawable.ic_volume_on);
            }
        }
    }

    private Drawable createRoundedBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#CC222222")); // semi-transparent black
        drawable.setCornerRadius(40f); // Adjust corner radius as needed
        return drawable;
    }

    private Drawable createCtaBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.BLACK);
        drawable.setCornerRadius(100f); // Pill shape
        return drawable;
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