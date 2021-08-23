// ignore_for_file: avoid_print

import 'dart:developer';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

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

  @override
  void initState() {
    super.initState();
    platform.setMessageHandler(_handlePlatformIncrement);
  }

  Future<String> _handlePlatformIncrement(String? message) async {
    setState(() {
      _counter++;
    });
    print('receive native message $message');
    return _emptyMessage;
  }

  void _sendFlutterIncrement() {
    print('send dart message to native $_pong');
    platform.send(_pong);
  }

  @override
  Widget build(BuildContext context) {
    log("build called");
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
          items: initNavItems(),
          selectedItemColor: Colors.blue,
          unselectedItemColor: Colors.black,
          backgroundColor: Colors.blue,
          onTap: _onNavItemClicked,
        ),
      ),
      debugShowCheckedModeBanner: false,
    );
  }

  List<BottomNavigationBarItem> initNavItems() {
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
    print("onNavItemClicked() index: $index, ${_items?[index].label}");
  }
}
