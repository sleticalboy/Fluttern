#!/usr/bin/env bash

# 杀掉 GradleDaemon 进程，释放系统内存
# 0 S  1000 16929 org.gradle.launcher.daemon.bootstrap.GradleDaemon 6.7
function kill_gradle_daemon_process() {
  for pid in $(pidof java); do
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

# 显示帮助
function show_help() {
  echo "-h: help of run.sh script"
  echo "-t: build type, should be release, profile or debug"
  exit 1
}

# $@ 表示所有参数
# $# 表示参数长度
# $0 表示当前脚本文件名
# $1..$# 表示具体的参数
echo "auto build start... args: $@, length: $#, \$0: $0, \$1: $1"

target=debug
type=debug

# 解析参数
function parse_args() {
  if [ $# -lt 2 ]; then
    return
  fi
  # 解析编译类型
  case $1 in
  '-t')
    shift
    type=$1
    ;;
  esac
  # 解析 target
  case $type in
  'release')
    target=$type
    ;;
  'profile')
    target=$type
    ;;
  'debug')
    target=$type
    ;;
  *)
    type=''
    target=''
    ;;
  esac
}

if [ "$type" == "" || "$target" == "" ]; then
  show_help
  exit 1
fi

# 清除编译缓存
find . -name build | xargs rm -rfv

# 将 flutter 模块编译成 aar
cd flutter_plugin && flutter build aar -v && cd -

# 查看编译好的 aar
echo ""
echo "out aars:"
echo "$(find flutter_plugin -name *.aar)"

# 将 aar 拷贝到 app/libs 下
if [ ! -d "app/libs" ]; then
  mkdir "app/libs"
fi
cp flutter_plugin/build/host/outputs/repo/com/binlee/flutter/plugin/flutter_${type}/1.0/flutter_${type}-1.0.aar app/libs

# 编译 apk
./gradlew assemble

# 查看编译好的apk
echo ""
echo "out apks:"
echo "$(find app -name *.apk)"

echo ""
echo 'auto build end...'

# 安装并启动 apk
echo ""
echo "launch app..."
echo ""

adb install -t -r -d app/build/outputs/apk/${type}/app-${target}.apk &&
  adb shell am start -n com.binlee.flutter/com.binlee.flutter.MainActivity

kill_gradle_daemon_process
