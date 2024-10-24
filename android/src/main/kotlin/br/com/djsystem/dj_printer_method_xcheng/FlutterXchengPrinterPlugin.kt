package br.com.djsystem.dj_printer_method_xcheng

import android.app.Activity
import android.content.Context
import android.util.Log

import br.com.djsystem.dj_printer_method_xcheng.printer.XchengPrinter

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/**
 * Flutter Pos Printer SDK Plugin for Flutter
 *
 * Based on code from https://github.com/TFSThiagoBR98/stone_sdk_flutter Commit:
 * 103ac7fb8576dd3ea885aa61f66191f84d97a7d7
 */
class FlutterXchengPrinterPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private var binaryMessenger: BinaryMessenger? = null
    private var activity: Activity? = null

    private var printerXcheng: XchengPrinter? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext

        binaryMessenger = flutterPluginBinding.binaryMessenger
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL)
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        printerXcheng?.onMethodCall(call, result)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        binaryMessenger = null
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity

        printerXcheng = XchengPrinter(context, binaryMessenger!!)
        printerXcheng?.start()
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        activity = null
        printerXcheng = null
    }

    companion object {
        private const val TAG: String = "FlutterXchengPrinterPlugin"
        private const val CHANNEL = "br.com.djsystem/dj_printer_method_xcheng"
    }
}
