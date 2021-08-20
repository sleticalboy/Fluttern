package com.binlee.flutter

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import android.util.Log
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

/**
 * Created on 2021/8/19
 *
 * @author binli@faceunity.com
 */
class FlutterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initFlutter()
    }

    @SuppressLint("NewApi")
    private fun initFlutter() {
        Looper.getMainLooper().queue.addIdleHandler {
            // 在主线程消息队列空闲的时候，初始化 flutter sdk，加快首次打开 flutter 页面的速度
            // FlutterInjector.instance().flutterLoader().startInitialization(this)
            // 必须在主线程执行，FlutterJNI 中会检测当前线程 looper，若不在主线程则会抛出
            // RuntimeException("Methods marked with @UiThread must be executed on the main thread.
            // Current thread: thread-x")
            val engine = FlutterEngine(this)
            Log.d(TAG, "onCreate() called ${FlutterEngineCache.getInstance()}")
            FlutterEngineCache.getInstance().put("flutter_engine_id", engine)
            engine.navigationChannel.setInitialRoute("binlee/route/index")
            engine.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
            false
        }
    }

    companion object {
        private const val TAG = "FlutterApp"
    }
}