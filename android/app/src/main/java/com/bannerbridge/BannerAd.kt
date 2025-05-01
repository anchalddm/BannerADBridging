package com.bannerbridge

import android.content.Context
import android.widget.FrameLayout
import android.view.ViewGroup.LayoutParams
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import android.util.Log
import android.view.Gravity
import android.graphics.Color
import android.util.DisplayMetrics

class BannerAd(context: Context) : FrameLayout(context) {
    private var adView: AdView? = null
    
    companion object {
        private const val TAG = "BannerAd"
        private const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    }
    
    init {
        Log.d(TAG, "Initializing BannerAd")
        setupBannerAd()
    }

    private fun setupBannerAd() {
        Log.d(TAG, "Setting up banner ad")
        
        MobileAds.initialize(context) { initializationStatus ->
            val statusMap = initializationStatus.adapterStatusMap
            statusMap.forEach { (adapter, status) ->
                Log.d(TAG, "Adapter: $adapter Status: ${status.initializationState}")
            }
            Log.d(TAG, "Mobile Ads SDK initialized, creating AdView")
            createAndLoadAd()
        }
    }

    private fun createAndLoadAd() {
        Log.d(TAG, "Creating new AdView")
        
        // Clean up existing AdView
        adView?.destroy()
        adView = null
        removeAllViews()

        // Create new AdView
        adView = AdView(context).apply {
            adUnitId = TEST_BANNER_ID
            setAdSize(AdSize.MEDIUM_RECTANGLE)
            
            // Use WRAP_CONTENT to let the ad determine its size
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }

            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d(TAG, "Ad loaded successfully!")
                    Log.d(TAG, "AdView size: ${width}x${height}")
                    Log.d(TAG, "AdSize: ${adSize?.width}x${adSize?.height}")
                    
                    // Update the container size after ad is loaded
                    post {
                        val params = layoutParams
                        params.width = width
                        params.height = height
                        layoutParams = params
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    Log.e(TAG, "Ad failed to load. Error code: ${error.code}")
                    Log.e(TAG, "Error message: ${error.message}")
                    Log.e(TAG, "Error domain: ${error.domain}")
                }
            }
        }

        // Set container to wrap content
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        
        // Add AdView to layout
        addView(adView)
        
        // Load the ad
        val adRequest = AdRequest.Builder().build()
        Log.d(TAG, "Requesting ad load")
        adView?.loadAd(adRequest)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d(TAG, "BannerAd detached from window")
        adView?.destroy()
        adView = null
    }
}

class BannerAdManager : SimpleViewManager<BannerAd>() {
    override fun getName() = "BannerAd"

    override fun createViewInstance(reactContext: ThemedReactContext): BannerAd {
        Log.d("BannerAdManager", "Creating new BannerAd instance")
        return BannerAd(reactContext)
    }
} 