package com.binlee.flutter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.binlee.flutter.databinding.ActivityMainBinding
import io.flutter.embedding.android.FlutterActivityLaunchConfigs
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.android.RenderMode

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnStartFlutterActivity.setOnClickListener { startFlutterActivity() }
        binding.btnStartFlutterFragment.setOnClickListener { startFlutterFragment() }
        binding.btnAddFlutterView.setOnClickListener { addFlutterView() }
    }

    override fun onBackPressed() {
        if (!interceptOnBackPressed()) super.onBackPressed()
    }

    // 拦截返回事件，移除 flutter fragment
    private fun interceptOnBackPressed(): Boolean {
        val tag = FlutterFragment::class.java.name
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment is io.flutter.embedding.android.FlutterFragment) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
            return true
        }
        return false
    }

    // 启动一个 flutter activity
    private fun startFlutterActivity() {
        val intent = FlutterFragmentActivity.CachedEngineIntentBuilder(
            FlutterActivity::class.java, "flutter_engine_id"/*使用缓存的 FlutterEngine*/
        )
            .backgroundMode(FlutterActivityLaunchConfigs.BackgroundMode.transparent)
            .build(this)
        intent.putExtra("_start_time", System.currentTimeMillis())
        startActivity(intent)
    }

    // 启动 flutter fragment
    private fun startFlutterFragment() {
        val tag = FlutterFragment::class.java.name
        var frag = supportFragmentManager.findFragmentByTag(tag)
        if (frag == null) {
            frag = io.flutter.embedding.android.FlutterFragment.CachedEngineFragmentBuilder(
                FlutterFragment::class.java, "flutter_engine_id"
            )
                .renderMode(RenderMode.surface)
                .build()
            supportFragmentManager.beginTransaction().add(R.id.fl_container, frag, tag).commit()
        } else {
            supportFragmentManager.beginTransaction().show(frag).commit()
        }
    }

    // 添加一个 flutter view
    private fun addFlutterView() {}
}