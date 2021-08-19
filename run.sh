#!/usr/bin/env bash

echo 'auto build start...'

# 将 flutter 模块编译成 aar
cd flutter_plugin && flutter build aar -v && cd -

# 查看编译好的 aar
echo -e "\nout aars: `find flutter_plugin -name *.aar`"

# 将 aar 拷贝到 app/libs 下
cp flutter_plugin/build/host/outputs/repo/com/faceunity/flutter_plugin/flutter_debug/1.0/flutter_debug-1.0.aar app/libs

# 编译 apk
./gradlew assemble -i

# 查看编译好的apk
echo -e "\nout apks: \n`find app -name *.apk`"

echo -e '\nauto build end...'

# 杀掉 GradleDaemon 进程，释放系统内存
# 0 S  1000 16929 org.gradle.launcher.daemon.bootstrap.GradleDaemon 6.7
gradle_pid=$(ps -l $(pidof java) | grep GradleDaemon | cut -d ' ' -f 5)
echo -e "\ngradle daemon pid: $gradle_pid \n$(ps -l $gradle_pid)"
kill -9 $gradle_pid

# 最好用截取字符串的方法过滤所有的 GradleDaemon 进程
#url="sfsdfadfadf"
#echo ${url: 8: 5}

# 安装并启动 apk
echo -e "\nlaunch app..."
adb install -t -r -d app/build/outputs/apk/debug/app-debug.apk && \
adb shell am start -n com.faceunity.flutter/com.faceunity.flutter.MainActivity