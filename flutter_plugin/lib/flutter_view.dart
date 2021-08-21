import 'dart:developer';

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
    return const MaterialApp(
      title: "FlutterView",
      home: Scaffold(
        body: Center(
          child: Text(
            "FlutterView",
            style: TextStyle(color: Colors.black),
          ),
        ),
      ),
    );
  }
}
