import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'home.dart';

@pragma("vm:entry-point")
void show() {
  runApp(const FlutterViewApp());
}

class FlutterViewApp extends StatefulWidget {
  const FlutterViewApp({Key? key}) : super(key: key);

  @override
  State<FlutterViewApp> createState() => _FlutterViewAppState();
}

class _FlutterViewAppState extends State<FlutterViewApp> {
  static const String _channel = "increment";
  static const String _pong = "pong";
  static const String _emptyMessage = "";
  static const BasicMessageChannel<String?> platform =
  BasicMessageChannel(_channel, StringCodec());

  int _counter = 0;
  List<BottomNavigationBarItem>? _items;
  int _curItem = -1;

  @override
  void initState() {
    super.initState();
    platform.setMessageHandler(_handlePlatformIncrement);
    log("initState() called _curItem: $_curItem");
  }

  Future<String> _handlePlatformIncrement(String? message) async {
    setState(() {
      _counter++;
    });
    log('receive native message $message');
    return _emptyMessage;
  }

  void _sendFlutterIncrement() {
    log('send dart message to native $_pong');
    platform.send(_pong);
  }

  @override
  Widget build(BuildContext context) {
    log("build() called");
    return MaterialApp(
      title: "FlutterView",
      home: Scaffold(
        appBar: PreferredSize(
          child: AppBar(
            title: const Text("FlutterView"),
            backgroundColor: Colors.green,
          ),
          preferredSize: const Size.fromHeight(0),
        ),
        body: Center(
          child: Text(
            "counter from native $_counter time${_counter == 1 ? '' : 's'}",
            style: const TextStyle(color: Colors.black, fontSize: 17.0),
          ),
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: _sendFlutterIncrement,
          child: const Icon(Icons.add),
        ),
        bottomNavigationBar: BottomNavigationBar(
          items: _buildNavItems(),
          selectedItemColor: Colors.blue,
          unselectedItemColor: Colors.black,
          backgroundColor: Colors.blue,
          onTap: _onNavItemClicked,
        ),
      ),
      debugShowCheckedModeBanner: false,
    );
  }

  List<BottomNavigationBarItem> _buildNavItems() {
    return _items ??= const <BottomNavigationBarItem>[
      BottomNavigationBarItem(
        icon: Icon(Icons.chat),
        label: "聊天",
      ),
      BottomNavigationBarItem(icon: Icon(Icons.contacts), label: "联系人"),
      BottomNavigationBarItem(icon: Icon(Icons.circle), label: "朋友圈"),
      BottomNavigationBarItem(icon: Icon(Icons.settings), label: "设置"),
    ];
  }

  void _onNavItemClicked(int index) {
    if (index == _curItem) return;
    _curItem = index;
    log("onNavItemClicked() index: $index, ${_items?[index].label}");
    Route<Object?> route;
    if (index == 0) {
      // 主页
      route = MaterialPageRoute(
          builder: (context) =>
          const Center(
              child: Text(
                "这是主页",
                textDirection: TextDirection.ltr,
              )));
    } else if (index == 1) {
      // 联系人
      route = MaterialPageRoute(
          builder: (context) =>
          const Center(
            child: Text(
              "这是联系人",
              textDirection: TextDirection.ltr,
            ),
          ));
    } else if (index == 2) {
      // 朋友圈
      route = MaterialPageRoute(
          builder: (context) =>
          const Center(
            child: Text(
              "这是朋友圈",
              textDirection: TextDirection.ltr,
            ),
          ));
    } else {
      // 设置
      route = MaterialPageRoute(
          builder: (context) =>
          const Center(
            child: Text(
              "这是设置",
              textDirection: TextDirection.ltr,
            ),
          ));
    }
    Navigator.of(context).push(route);
  }
}
