package com.binlee.flutter

import android.os.Bundle
import android.util.Log

/**
 * Created on 2021/8/19
 *
 * @author binli@faceunity.com
 */
class FlutterActivity : io.flutter.embedding.android.FlutterActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startTime = intent.getLongExtra("_start_time", -1L)
        if (startTime < 0) return
        val totalTime = System.currentTimeMillis() - startTime
        Log.d(TAG, "onCreate() called after $totalTime ms")
    }

    override fun onFlutterUiDisplayed() {
        super.onFlutterUiDisplayed()
        val startTime = intent.getLongExtra("_start_time", -1L)
        if (startTime < 0) return
        val totalTime = System.currentTimeMillis() - startTime
        Log.d(TAG, "onFlutterUiDisplayed() called after $totalTime ms")
    }

    companion object {
        private const val TAG = "FlutterActivity"
    }
}