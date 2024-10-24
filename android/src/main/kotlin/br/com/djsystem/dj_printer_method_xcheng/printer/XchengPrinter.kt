package br.com.djsystem.dj_printer_method_xcheng.printer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.xcheng.printerservice.IPrinterCallback
import com.xcheng.printerservice.IPrinterService
import io.flutter.plugin.common.BinaryMwessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

internal class XchengPrinter(private val context: Context, binaryMessenger: BinaryMessenger) : MethodChannel.MethodCallHandler {
    var totalLen = 0L
    var currentLen = 0L
    var realTotalLen = 0.0
    var realCurrentLen = 0.0

    private val mListener: PrinterManagerListener? = null
    private var mCallback: IPrinterCallback? = null
    private var mPrinterService: IPrinterService? = null

    private var channel: MethodChannel = MethodChannel(binaryMessenger, CHANNEL)

    fun start() {
        channel.setMethodCallHandler(this)
        startPrinterService(null)
    }

    interface PrinterManagerListener {
        fun onServiceConnected()
        fun onServiceDisconnected()
    }

    private val mConnectionService: ServiceConnection =
        object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName) {
                this@XchengPrinter.mPrinterService = null
                this@XchengPrinter.mListener?.onServiceDisconnected()
                Log.i(TAG, "Service disconnected")
            }

            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                this@XchengPrinter.mPrinterService = IPrinterService.Stub.asInterface(service)
                this@XchengPrinter.mListener?.onServiceConnected()
                Log.i(TAG, "Service connected")
            }
        }

    fun startPrinterService(result: MethodChannel.Result?) {
        this.mCallback =
            object : IPrinterCallback.Stub() {
                @Throws(RemoteException::class)
                override fun onException(code: Int, msg: String) {
                    Log.w(TAG, "onException($code,$msg)")
                }

                @Throws(RemoteException::class)
                override fun onLength(current: Long, total: Long) {
                    Log.w(TAG, "onLength: start")
                    this@XchengPrinter.currentLen = current
                    this@XchengPrinter.totalLen = total
                    Log.w(TAG, "onLength: end")
                }

                override fun onComplete() {
                    Log.i(TAG, "onComplete: start")
                }

                @Throws(RemoteException::class)
                override fun onRealLength(realCurrent: Double, realTotal: Double) {
                    Log.i(TAG, "onReal Length: start")
                    this@XchengPrinter.realCurrentLen = realCurrent
                    this@XchengPrinter.realTotalLen = realTotal
                    Log.i(TAG, "realCurrent=$realCurrent, realTotal=$realTotal")
                    Log.i(TAG, "onReal Length: end")
                }
            }
        val intent = Intent()
        intent.setPackage("com.xcheng.printerservice")
        intent.action = "com.xcheng.printerservice.IPrinterService"
        try {
            this.context.startService(intent)
            val bound = this.context.bindService(intent, mConnectionService, Context.BIND_AUTO_CREATE)
            if (bound) {
                Log.i(TAG, "Service bound successfully")
            } else {
                Log.e(TAG, "Failed to bind service")
                result?.error("serviceBindingError", "Failed to bind service", null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start or bind service: ${e.message}")
            result?.error("serviceStartError", "Failed to start or bind service", e.message)
        }
    }

    fun printBitmap(bitmap: Bitmap, width: Int, height: Int, result: MethodChannel.Result?) {
        try {
            if (mPrinterService != null) {
                mPrinterService!!.printerInit(mCallback)
                mPrinterService!!.printBitmap(bitmap, mCallback)
                mPrinterService!!.printerPaper(mCallback)
                Log.i(TAG, "PrintBitmap Success")
                result?.success(true)
            } else {
                Log.e(TAG, "Printer service is not available")
                result?.error("serviceUnavailable", "Printer service is not available", null)
            }
        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException during printBitmap: ${e.message}")
            result?.error("sdkError", e.message, e.stackTrace.joinToString("\n"))
        } catch (e: Exception) {
            Log.e(TAG, "Exception during printBitmap: ${e.message}")
            result?.error("sdkError", e.message, e.stackTrace.joinToString("\n"))
        }
    }

    fun printWrapPaper(n: Int, result: MethodChannel.Result?) {
        try {
            if (mPrinterService != null) {
                mPrinterService!!.printWrapPaper(n, mCallback)
                Log.i(TAG, "PrintWrapPaper Success")
                result?.success(true)
            } else {
                Log.e(TAG, "Printer service is not available")
                result?.error("serviceUnavailable", "Printer service is not available", null)
            }
        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException during printWrapPaper: ${e.message}")
            result?.error("sdkError", e.message, e.stackTrace.joinToString("\n"))
        } catch (e: Exception) {
            Log.e(TAG, "Exception during printWrapPaper: ${e.message}")
            result?.error("sdkError", e.message, e.stackTrace.joinToString("\n"))
        }
    }

    fun initPrinter(result: MethodChannel.Result?) {
        try {
            if (mPrinterService != null) {
                mPrinterService!!.printerInit(mCallback)
                Log.i(TAG, "InitPrinter Success")
                result?.success(true)
            } else {
                Log.e(TAG, "Printer service is not available")
                result?.error("serviceUnavailable", "Printer service is not available", null)
            }
        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException during initPrinter: ${e.message}")
            result?.error("sdkError", e.message, e.stackTrace.joinToString("\n"))
        } catch (e: Exception) {
            Log.e(TAG, "Exception during initPrinter: ${e.message}")
            result?.error("sdkError", e.message, e.stackTrace.joinToString("\n"))
        }
    }

    fun resetPrinter(result: MethodChannel.Result?) {
        try {
            if (mPrinterService != null) {
                mPrinterService!!.printerReset(mCallback)
                Log.i(TAG, "ResetPrinter Success")
                result?.success(true)
            } else {
                Log.e(TAG, "Printer service is not available")
                result?.error("serviceUnavailable", "Printer service is not available", null)
            }
        } catch (e: RemoteException) {
            Log.e(TAG, "RemoteException during resetPrinter: ${e.message}")
            result?.error("sdkError", e.message, e.stackTrace.joinToString("\n"))
        } catch (e: Exception) {
            Log.e(TAG, "Exception during resetPrinter: ${e.message}")
            result?.error("sdkError", e.message, e.stackTrace.joinToString("\n"))
        }
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "startPrinterService" -> {
                Log.i(TAG, "Call startPrinterService")
                startPrinterService(result)
            }
            "printBitmap" -> {
                Log.i(TAG, "Call printBitmap")
                try {
                    val bytes = call.argument<ByteArray>("image")!!
                    val computedBitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    val width = call.argument<Int>("width")!!
                    val height = call.argument<Int>("height")!!
                    printBitmap(computedBitmap, width, height, result)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception during printBitmap call: ${e.message}")
                    result.error("sdkError", e.message, e.stackTrace.joinToString("\n"))
                }
            }
            "printWrapPaper" -> {
                Log.i(TAG, "Call printWrapPaper")
                try {
                    val lines = call.argument<Int>("lines")!!
                    printWrapPaper(lines, result)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception during printWrapPaper call: ${e.message}")
                    result.error("sdkError", e.message, e.stackTrace.joinToString("\n"))
                }
            }
            "initPrinter" -> {
                Log.i(TAG, "Call initPrinter")
                initPrinter(result)
            }
            "resetPrinter" -> {
                Log.i(TAG, "Call resetPrinter")
                resetPrinter(result)
            }
            "isAvailable" -> {
                Log.i(TAG, "Call isAvailable")
                try {
                    result.success(isAvailable())
                } catch (e: Exception) {
                    Log.e(TAG, "Exception during isAvailable call: ${e.message}")
                    result.error("sdkError", e.message, e.stackTrace.joinToString("\n"))
                }
            }
            else -> result.notImplemented()
        }
    }

    private fun isAvailable(): Boolean {
        return mPrinterService != null
    }

    companion object {
        private const val iPosServicePkg: String = "com.xcheng.printerservice"
        private const val TAG: String = "XchengPrinter"

        private const val CHANNEL = "br.com.djsystem/dj_printer_method_xcheng/printer/xcheng"
    }
}
