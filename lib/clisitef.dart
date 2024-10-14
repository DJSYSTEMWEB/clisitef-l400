library clisitef;

import 'dart:io';

import 'package:clisitef_l400/android/clisitef_android.dart';
import 'package:clisitef_l400/clisitef_sdk.dart';
import 'package:flutter/services.dart';

class CliSitef {
  CliSitef._();

  static CliSiTefSDK get instance => Platform.isAndroid
      ? CliSiTefAndroid()
      : throw PlatformException(
          code: 'NotSupported',
          message: 'This library only supports Android applications');
}
