package com.binlee.flutter

import android.content.*
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

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

  override fun configureFlutterEngine(engine: FlutterEngine) {
    EventChannel(engine.dartExecutor, "_charging").setStreamHandler(object :
      EventChannel.StreamHandler {
      private var chargingStateChangeReceiver: BroadcastReceiver? = null
      override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        // register receiver
        chargingStateChangeReceiver = createChargingStateChangeReceiver(events)
        registerReceiver(
          chargingStateChangeReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
      }

      override fun onCancel(arguments: Any?) {
        // unregister receiver
        unregisterReceiver(chargingStateChangeReceiver);
        chargingStateChangeReceiver = null;
      }
    })
    MethodChannel(engine.dartExecutor, "_battery").setMethodCallHandler { call, result ->
      if ("getBatteryLevel" == call.method) {
        val level = getBatteryLevel()
        if (level != -1) {
          result.success(level)
        } else {
          result.error("unavailable", "Battery level is unavailable.", null)
        }
      } else {
        result.notImplemented()
      }
    }
  }

  private fun getBatteryLevel(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val manager = getSystemService(BATTERY_SERVICE) as BatteryManager
      manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    } else {
      val intent = ContextWrapper(applicationContext).registerReceiver(
        null,
        IntentFilter(Intent.ACTION_BATTERY_CHANGED)
      )
      (intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
        intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    }
  }

  private fun createChargingStateChangeReceiver(events: EventChannel.EventSink?):
    BroadcastReceiver {
    return object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        if (status == BatteryManager.BATTERY_STATUS_UNKNOWN) {
          events?.error("UNAVAILABLE", "Charging status unavailable", null)
        } else {
          val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
          events?.success(if (isCharging) "charging" else "discharging")
        }
      }
    }
  }

  companion object {
    private const val TAG = "FlutterActivity"
  }
}