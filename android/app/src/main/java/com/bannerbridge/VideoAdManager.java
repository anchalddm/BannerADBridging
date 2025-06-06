package com.bannerbridge;

import android.util.Log;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import java.util.Map;
import java.util.HashMap;

public class VideoAdManager extends SimpleViewManager<VideoAd> {
    public static final String REACT_CLASS = "VideoAd";
    private static final String TAG = "VideoAdManager";
    
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public VideoAd createViewInstance(ThemedReactContext reactContext) {
        Log.d(TAG, "Creating new VideoAd instance");
        return new VideoAd(reactContext);
    }

    @ReactProp(name = "width")
    public void setWidth(VideoAd view, int width) {
        Log.d(TAG, "Setting width prop: " + width);
    }

    @ReactProp(name = "height")
    public void setHeight(VideoAd view, int height) {
        Log.d(TAG, "Setting height prop: " + height);
    }

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        Map<String, Object> builders = new HashMap<>();
        
        Map<String, String> onLoadEvent = new HashMap<>();
        onLoadEvent.put("registrationName", "onVideoAdLoaded");
        builders.put("topVideoAdLoaded", onLoadEvent);
        
        Map<String, String> onFailEvent = new HashMap<>();
        onFailEvent.put("registrationName", "onVideoAdFailedToLoad");
        builders.put("topVideoAdFailedToLoad", onFailEvent);
        
        return builders;
    }
} 