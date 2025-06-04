package com.bannerbridge;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import android.util.Log;
import android.view.Gravity;
import android.graphics.Color;
import android.app.Activity;
import android.view.View;
import java.util.Map;
import java.util.HashMap;

public class BannerAd extends FrameLayout {
    private static final String TAG = "BannerAd";
    private static final String TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111";
    
    private AdView adView;
    private TextView helloWorldText;

    public BannerAd(Context context) {
        super(context);
        Log.d(TAG, "Initializing BannerAd");
        setupHelloWorldText();
        setupBannerAd();
    }

    private void setupHelloWorldText() {
        Log.d(TAG, "Setting up Hello World text");
        helloWorldText = new TextView(getContext());
        helloWorldText.setText("Hello Anchal");
        helloWorldText.setTextSize(24);
        helloWorldText.setTextColor(Color.BLACK);
        helloWorldText.setGravity(Gravity.CENTER);
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        helloWorldText.setLayoutParams(params);
        
        addView(helloWorldText);
    }

    private void setupBannerAd() {
        Log.d(TAG, "Setting up banner ad");
        
        MobileAds.initialize(getContext(), initializationStatus -> {
            initializationStatus.getAdapterStatusMap().forEach((adapter, status) -> 
                Log.d(TAG, "Adapter: " + adapter + " Status: " + status.getInitializationState())
            );
            createAndLoadAd();
        });
    }

    private void createAndLoadAd() {
        Log.d(TAG, "Creating new AdView");
        
        // Clean up existing AdView
        if (adView != null) {
            adView.destroy();
            adView.removeAllViews();
            removeView(adView);
            adView = null;
        }
        
        // Get current activity for better mediation performance
        Activity currentActivity = null;
        if (getContext() instanceof ReactContext) {
            currentActivity = ((ReactContext) getContext()).getCurrentActivity();
        }
        
        if (currentActivity == null) {
            Log.e(TAG, "No activity found");
            return;
        }
        
        // Create new AdView with activity
        adView = new AdView(currentActivity);
        adView.setAdUnitId(TEST_BANNER_ID);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        adView.setLayoutParams(params);
        
        adView.setVisibility(View.VISIBLE);
        
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(TAG, "Ad loaded successfully!");
                
                helloWorldText.setText("Ad Loaded Successfully!");
                
                AdSize adSize = adView.getAdSize();
                if (adSize != null) {
                    int width = adSize.getWidthInPixels(getContext());
                    int height = adSize.getHeightInPixels(getContext());
                    
                    measure(
                        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                    );
                    
                    requestLayout();
                    invalidate();
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError error) {
                Log.e(TAG, "Ad failed to load. Error: " + error.getMessage());
                helloWorldText.setText("Ad Failed to Load: " + error.getMessage());
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "Ad opened");
            }
            
            @Override
            public void onAdClosed() {
                Log.d(TAG, "Ad closed");
            }
            
            @Override
            public void onAdImpression() {
                Log.d(TAG, "Ad impression");
            }
            
            @Override
            public void onAdClicked() {
                Log.d(TAG, "Ad clicked");
            }
        });

        // Add AdView to layout before loading
        addView(adView);
        
        // Load the ad
        AdRequest adRequest = new AdRequest.Builder().build();
        Log.d(TAG, "Requesting ad load");
        adView.loadAd(adRequest);
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
        Log.d(TAG, "BannerAd detached from window");
        if (adView != null) {
            adView.destroy();
            adView = null;
        }
    }
}

class BannerAdManager extends SimpleViewManager<BannerAd> {
    public static final String REACT_CLASS = "BannerAd";
    
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public BannerAd createViewInstance(ThemedReactContext reactContext) {
        Log.d("BannerAdManager", "Creating new BannerAd instance");
        return new BannerAd(reactContext);
    }

    @ReactProp(name = "width")
    public void setWidth(BannerAd view, int width) {
        Log.d("BannerAdManager", "Setting width prop: " + width);
    }

    @ReactProp(name = "height")
    public void setHeight(BannerAd view, int height) {
        Log.d("BannerAdManager", "Setting height prop: " + height);
    }

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        Map<String, Object> builders = new HashMap<>();
        
        Map<String, String> onLoadEvent = new HashMap<>();
        onLoadEvent.put("registrationName", "onBannerAdLoaded");
        builders.put("topBannerAdLoaded", onLoadEvent);
        
        Map<String, String> onFailEvent = new HashMap<>();
        onFailEvent.put("registrationName", "onBannerAdFailedToLoad");
        builders.put("topBannerAdFailedToLoad", onFailEvent);
        
        return builders;
    }
} 