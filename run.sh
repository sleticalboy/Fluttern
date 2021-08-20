#!/usr/bin/env bash

# 杀掉 GradleDaemon 进程，释放系统内存
# 0 S  1000 16929 org.gradle.launcher.daemon.bootstrap.GradleDaemon 6.7
function kill_gradle_daemon_process() {
  for pid in $(pidof java)
  do
    line=$(ps -l $pid | grep GradleDaemon)
    if [ "$line" == "" ]; then
        continue
    fi
    echo "we're gonna kill $pid"
    kill -9 $pid
  done
}

# 使用 Android studio 自带的 jdk 进行编译
function export_java_home() {
  javac_cmd=$(locate javac | grep android-studio/jre)
  # /xxx/android-studio/jre/bin/javac
  echo "javac is: $javac_cmd"
  if [ -z $javac_cmd ]; then
      echo "android studio is not installed..."
      exit 1
  fi
  # 字符串替换替换
  #${string/substring/replacement}
  export JAVA_HOME=${javac_cmd/"/bin/javac"/""}
}

echo 'auto build start...'

# 清除编译缓存
find . -name build | xargs rm -rfv

# 将 flutter 模块编译成 aar
cd flutter_plugin && flutter build aar -v && cd -

# 查看编译好的 aar
echo -e "\nout aars:\n`find flutter_plugin -name *.aar`"

# 将 aar 拷贝到 app/libs 下
cp flutter_plugin/build/host/outputs/repo/com/binlee/flutter/plugin/flutter_debug/1.0/flutter_debug-1.0.aar app/libs

# 编译 apk
./gradlew assemble --debug -s

# 查看编译好的apk
echo -e "\nout apks: \n`find app -name *.apk`"

echo -e '\nauto build end...'

# 安装并启动 apk
echo -e "\nlaunch app..."
adb install -t -r -d app/build/outputs/apk/debug/app-debug.apk && \
adb shell am start -n com.binlee.flutter/com.binlee.flutter.MainActivity

kill_gradle_daemon_process