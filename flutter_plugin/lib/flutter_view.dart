import 'dart:developer';
import 'dart:ffi';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

void main() {}

@pragma("vm:entry-point")
void show() {
  runApp(const FlutterViewApp());
}

class FlutterViewApp extends StatelessWidget {
  const FlutterViewApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    log("build called");
    return MaterialApp(
      title: "FlutterView",
      home: Scaffold(
        appBar: AppBar(
          title: const Text("FlutterView"),
          backgroundColor: Colors.green,
        ),
        body: const Center(
          child: Text(
            "FlutterView",
            style: TextStyle(color: Colors.black),
          ),
        ),
        bottomNavigationBar: BottomNavigationBar(
          items: [
            BottomNavigationBarItem(
              icon: Icon(Icons.chat),
              label: "聊天",
            ),
            BottomNavigationBarItem(icon: Icon(Icons.contacts), label: "联系人"),
            BottomNavigationBarItem(icon: Icon(Icons.circle), label: "朋友圈"),
            BottomNavigationBarItem(icon: Icon(Icons.settings), label: "设置"),
          ],
          selectedItemColor: Colors.blue,
          unselectedItemColor: Colors.black,
          backgroundColor: Colors.blue,
        ),
      ),
    );
  }
}
