package com.binlee.flutter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import io.flutter.embedding.engine.renderer.FlutterUiDisplayListener

/**
 * Created on 2021/8/19
 *
 * @author binli@faceunity.com
 */
class FlutterActivity : io.flutter.embedding.android.FlutterFragmentActivity(),
    FlutterUiDisplayListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startTime = intent.getLongExtra("_start_time", -1L)
        if (startTime < 0) return
        val totalTime = System.currentTimeMillis() - startTime
        Log.d(TAG, "onCreate() called after $totalTime ms")
    }

    override fun provideRootLayout(context: Context?): FrameLayout {
        // 返回的是一个 FrameLayout
        return super.provideRootLayout(context)
    }

    override fun onFlutterUiDisplayed() {
        val startTime = intent.getLongExtra("_start_time", -1L)
        if (startTime < 0) return
        val totalTime = System.currentTimeMillis() - startTime
        Log.d(TAG, "onFlutterUiDisplayed() called after $totalTime ms")
    }

    override fun onFlutterUiNoLongerDisplayed() {
    }

    companion object {
        private const val TAG = "FlutterActivity"
    }
}