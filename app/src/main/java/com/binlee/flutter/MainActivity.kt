package com.binlee.flutter

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterActivityLaunchConfigs

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_start_flutter).setOnClickListener {
            val intent = FlutterActivity.CachedEngineIntentBuilder(
                com.binlee.flutter.FlutterActivity::class.java,
                "flutter_engine_id"/*使用缓存的 FlutterEngine*/
            )
                .backgroundMode(FlutterActivityLaunchConfigs.BackgroundMode.transparent)
                .build(this)
            intent.putExtra("_start_time", System.currentTimeMillis())
            startActivity(intent)
        }
    }
}