
Properties loadLocalProperties() {
    File localPropertiesFile = new File(rootProject.rootDir, "local.properties")
    Properties props = new Properties()
    localPropertiesFile.withReader("UTF-8") { reader -> props.load(reader) }
    return props
}

// 获取 flutter engine 版本号
String getEngineVersion() {
    // 1. 加载 local.properties 文件
    Properties props = loadLocalProperties()
    // 2. 获取 flutter sdk 路径
    String flutterRoot = props.getProperty('flutter.sdk')
    assert flutterRoot != null, "flutter.sdk not set in local.properties"
    assert new File(flutterRoot).exists(), "please install flutter sdk first"
    // 3. 读取版本号
    return new File(flutterRoot, "bin/internal/engine.version").text.trim()
}

final String baseVersion = '1.0.0'
String engineVersion = "${baseVersion}-${getEngineVersion()}"
println("engine version: ${engineVersion}")

// repositories {
//     // implementation("io.flutter:flutter_embedding_release:$engineVersion")
//     // implementation "io.flutter:arm64_v8a_release:$engineVersion"
//     // implementation "io.flutter:armeabi_v7a_release:$engineVersion"
//
//     // debugImplementation "io.flutter:flutter_embedding_debug:$engineVersion"
//     // debugImplementation "io.flutter:arm64_v8a_debug:$engineVersion"
//     // debugImplementation "io.flutter:armeabi_v7a_debug:$engineVersion"
//     // debugImplementation "io.flutter:x86_debug:$engineVersion"
//     // debugImplementation "io.flutter:x86_64_debug:$engineVersion"
// }