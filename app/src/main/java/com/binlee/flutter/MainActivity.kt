package com.binlee.flutter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.binlee.flutter.databinding.ActivityMainBinding
import io.flutter.FlutterInjector
import io.flutter.embedding.android.FlutterActivityLaunchConfigs
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.android.RenderMode
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var viewEngine: FlutterViewEngine? = null
    private var flutterView: FlutterView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.btnStartFlutterActivity.setOnClickListener { startFlutterActivity() }
        binding!!.btnStartFlutterFragment.setOnClickListener { startFlutterFragment() }
        binding!!.btnAddFlutterView.setOnClickListener { addFlutterView() }
    }

    override fun onBackPressed() {
        if (!interceptOnBackPressed()) super.onBackPressed()
    }

    // 拦截返回事件，移除 flutter fragment
    private fun interceptOnBackPressed(): Boolean {
        val tag = FlutterFragment::class.java.name
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment is io.flutter.embedding.android.FlutterFragment) {
            supportFragmentManager.beginTransaction().remove(fragment).commitNowAllowingStateLoss()
            return true
        }
        if (flutterView?.parent == binding!!.flContainer) {
            binding!!.flContainer.removeView(flutterView)
            viewEngine?.detachView()
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
        val transaction = supportFragmentManager.beginTransaction()
        if (frag == null) {
            frag = io.flutter.embedding.android.FlutterFragment.CachedEngineFragmentBuilder(
                FlutterFragment::class.java, "flutter_engine_id"
            )
                .renderMode(RenderMode.surface)
                .build()
            transaction.add(R.id.fl_container, frag, tag)
        } else {
            transaction.show(frag)
        }
        transaction.commit()
    }

    // 添加一个 flutter view
    private fun addFlutterView() {
        if (viewEngine == null) {
            // var engine = FlutterEngineCache.getInstance().get("flutter_engine_id")
            // 只要 FlutterJNI 不被销毁，就不能再次执行新的 dart entry point
            // 因此这里新创建一个 FlutterEngine，但是首次速度会比较慢
            val engine = FlutterEngine(this)
            engine.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint(
                FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                "show"
            ))
            viewEngine = FlutterViewEngine(engine)
        }
        viewEngine?.attachActivity(this)
        // 实例化一个 flutter view 并添加到容器中
        if (flutterView == null) flutterView = FlutterView(this)
        if (binding!!.flContainer.indexOfChild(flutterView) < 0) {
            binding!!.flContainer.addView(flutterView)
        }
        viewEngine?.attachView(flutterView!!)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        viewEngine?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewEngine?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onUserLeaveHint() {
        viewEngine?.onUserLeaveHint()
        super.onUserLeaveHint()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}