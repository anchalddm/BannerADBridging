package com.bannerbridge

import android.content.Context
import android.widget.FrameLayout
import android.widget.TextView
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
import android.widget.LinearLayout

class BannerAd(context: Context) : LinearLayout(context) {
    private var adView: AdView? = null
    private var helloWorldText: TextView? = null
    
    companion object {
        private const val TAG = "BannerAd"
        private const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    }
    
    init {
        orientation = VERTICAL
        Log.d(TAG, "Initializing BannerAd")
        setBackgroundColor(Color.WHITE)  // Change background back to white
        setupHelloWorldText()
        setupBannerAd()
    }

    private fun setupHelloWorldText() {
        Log.d(TAG, "Setting up Hello World text")
        helloWorldText = TextView(context).apply {
            text = "Hello Kotlin"
            textSize = 24f
            setTextColor(Color.BLACK)  // Change text color back to black
            setBackgroundColor(Color.TRANSPARENT)  // Make text background transparent
            gravity = Gravity.CENTER
            setPadding(20, 20, 20, 20)
            
            layoutParams = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        }
        addView(helloWorldText)
        Log.d(TAG, "Hello World text added to view")
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
        if (adView != null) {
            removeView(adView)
        }
        adView = null
        
        // Create new AdView
        adView = AdView(context).apply {
            adUnitId = TEST_BANNER_ID
            setAdSize(AdSize.MEDIUM_RECTANGLE)
            
            // Set layout params for LinearLayout to wrap content initially
            layoutParams = LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,  // Let AdView determine its width based on AdSize
                LayoutParams.WRAP_CONTENT   // Let AdView determine its height based on AdSize
            ).apply {
                 gravity = Gravity.CENTER_HORIZONTAL // Center AdView horizontally within LinearLayout
            }
            // Don't set visibility here, will be added and made visible in onAdLoaded

            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d(TAG, "Ad loaded successfully!")
                    Log.d(TAG, "AdView size reported in onAdLoaded: ${width}x${height}")
                    Log.d(TAG, "AdSize reported in onAdLoaded: ${adSize?.width}x${adSize?.height}")
                    Log.d(TAG, "Parent size in onAdLoaded: ${this@BannerAd.width}x${this@BannerAd.height}")

                    // Update text when ad loads successfully
                    helloWorldText?.text = "Ad Loaded Successfully!"
                    
                    // Add AdView to layout after ad is loaded
                    this@BannerAd.addView(adView)

                    // Make AdView visible and request layout update
                    adView?.visibility = VISIBLE
                    this@BannerAd.requestLayout()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    Log.e(TAG, "Ad failed to load. Error code: ${error.code}")
                    Log.e(TAG, "Error message: ${error.message}")
                    Log.e(TAG, "Error domain: ${error.domain}")
                    // Optionally, update text or hide the container on failure
                    helloWorldText?.text = "Ad Failed to Load: ${error.getMessage()}"
                    // Hide AdView on failure
                    adView?.visibility = GONE
                }
                 // Add other potential AdListener methods for debugging
                 override fun onAdOpened() {
                     Log.d(TAG, "Ad opened")
                 }
                 override fun onAdClosed() {
                     Log.d(TAG, "Ad closed")
                 }
                 override fun onAdImpression() {
                     Log.d(TAG, "Ad impression")
                 }
                 override fun onAdClicked() {
                     Log.d(TAG, "Ad clicked")
                 }
            }
        }

        // Do NOT add AdView here, it will be added in onAdLoaded
        // addView(adView)
        
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
        val bannerAdView = BannerAd(reactContext)
        // Set initial layout params for the native view based on RN props
        val widthPx = reactContext.resources.displayMetrics.density * 300 // Convert dp to pixels
        val heightPx = reactContext.resources.displayMetrics.density * 250 // Convert dp to pixels
        bannerAdView.layoutParams = LinearLayout.LayoutParams(widthPx.toInt(), heightPx.toInt())
        return bannerAdView
    }

    @ReactProp(name = "width")
    fun setWidth(view: BannerAd, width: Int) {
        Log.d("BannerAdManager", "Setting width prop: $width")
        // React Native's layout system will handle the width based on the prop
    }

    @ReactProp(name = "height")
    fun setHeight(view: BannerAd, height: Int) {
        Log.d("BannerAdManager", "Setting height prop: $height")
        // React Native's layout system will handle the height based on the prop
    }
} 