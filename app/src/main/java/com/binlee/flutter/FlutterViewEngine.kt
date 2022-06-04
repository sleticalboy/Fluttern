package com.binlee.flutter

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.flutter.embedding.android.ExclusiveAppComponent
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.platform.PlatformPlugin

/**
 * Created on 2021-08-21.
 * @author binlee
 */
class FlutterViewEngine(private val engine: FlutterEngine) : LifecycleEventObserver {

  private var activity: ExclusiveAppComponent<ComponentActivity>? = null
  private var flutterView: FlutterView? = null
  private var platformPlugin: PlatformPlugin? = null

  fun attachActivity(activity: ExclusiveAppComponent<ComponentActivity>) {
    if (activity == this.activity) return
    this.activity = activity
    doAttach()
  }

  fun attachView(flutterView: FlutterView) {
    if (flutterView == this.flutterView) return
    this.flutterView = flutterView
    doAttach()
  }

  fun detachView() {
    doDetach()
    flutterView = null
  }

  fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    if (isAttached()) {
      engine.activityControlSurface.onRequestPermissionsResult(
        requestCode,
        permissions,
        grantResults
      )
    }
  }

  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (isAttached()) {
      engine.activityControlSurface.onActivityResult(requestCode, resultCode, data)
    }
  }

  fun onUserLeaveHint() {
    if (isAttached()) {
      engine.activityControlSurface.onUserLeaveHint()
    }
  }

  private fun isAttached() = activity != null && flutterView != null

  private fun doDetach() {
    flutterView?.let {
      activity?.appComponent?.lifecycle?.removeObserver(this)
      engine.activityControlSurface.detachFromActivity()
      platformPlugin?.destroy()
      platformPlugin = null
      engine.lifecycleChannel.appIsDetached()
      it.detachFromFlutterEngine()
    }
  }

  private fun doAttach() {
    activity?.appComponent?.let {
      flutterView?.let { view ->
        platformPlugin = PlatformPlugin(it, engine.platformChannel)
        engine.activityControlSurface.attachToActivity(it as ExclusiveAppComponent<Activity>, it.lifecycle)
        view.attachToFlutterEngine(engine)
        it.lifecycle.addObserver(this)
      }
    }
  }

  override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    when (event) {
      Lifecycle.Event.ON_RESUME -> {
        activity?.let { engine.lifecycleChannel.appIsResumed() }
        platformPlugin?.updateSystemUiOverlays()
      }
      Lifecycle.Event.ON_PAUSE -> {
        activity?.let { engine.lifecycleChannel.appIsInactive() }
      }
      Lifecycle.Event.ON_STOP -> {
        activity?.let { engine.lifecycleChannel.appIsPaused() }
      }
      Lifecycle.Event.ON_DESTROY -> {
        doDetach()
        activity = null
      }
      else -> {
      }
    }
  }
}