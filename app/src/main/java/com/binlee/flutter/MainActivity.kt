package com.binlee.flutter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.binlee.flutter.databinding.ActivityMainBinding
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterActivityLaunchConfigs

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnStartFlutter.setOnClickListener {
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