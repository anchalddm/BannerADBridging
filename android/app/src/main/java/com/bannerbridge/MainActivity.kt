package com.bannerbridge

import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

class MainActivity : ReactActivity() {

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "BannerBridge"

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate =
      DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)
}
 
// to run ad Android project
// package com.bannerbridge

// import com.facebook.react.ReactActivity
// import com.facebook.react.ReactActivityDelegate
// import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
// import com.facebook.react.defaults.DefaultReactActivityDelegate
// import android.os.Bundle
// import android.widget.FrameLayout
// import android.view.ViewGroup.LayoutParams
// import android.graphics.Color

// class MainActivity : ReactActivity() {

//   private var bannerAd: BannerAd? = null

//   override fun onCreate(savedInstanceState: Bundle?) {
//     super.onCreate(savedInstanceState)

//     // Create a FrameLayout as the main container
//     val rootLayout = FrameLayout(this).apply {
//         layoutParams = LayoutParams(
//             LayoutParams.MATCH_PARENT,
//             LayoutParams.MATCH_PARENT
//         )
//         setBackgroundColor(Color.WHITE)
//     }
//     setContentView(rootLayout)

//     // Create and add the BannerAd
//     bannerAd = BannerAd(this).apply {
//         layoutParams = FrameLayout.LayoutParams(
//             LayoutParams.WRAP_CONTENT,
//             LayoutParams.WRAP_CONTENT
//         ).apply {
//             // Center the banner
//             gravity = android.view.Gravity.CENTER
//         }
//     }
//     rootLayout.addView(bannerAd)
//   }

//   /**
//    * Returns the name of the main component registered from JavaScript. This is used to schedule
//    * rendering of the component.
//    */
//   override fun getMainComponentName(): String = "BannerBridge"

//   /**
//    * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
//    * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
//    */
//   override fun createReactActivityDelegate(): ReactActivityDelegate =
//       DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)
// }
