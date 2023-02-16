package com.example.devicedetect

import android.text.SpannableStringBuilder
import android.util.Log
import com.example.devicedetect.Util.ConstantHelper
import kotlinx.coroutines.*
import java.nio.ByteBuffer

class UsbSerialIOManager(
    usbSerialIOOperation: UsbSerialIOOperation, usbListener: UsbHelperListener
) {

    private var TAG = "USB_COMMUNICATION_HELPER"

    //UsbListener
    private var mUsbListener: UsbHelperListener

    //UsbOperation
    private var usbSerialIOOperation: UsbSerialIOOperation

    //ReadBuffer
    private var mReadBuffer: ByteBuffer? = null

    //Coroutine
    private var coroutineJob: CompletableJob? = null

    //Required Variables
    private var counter: Int = 0
    private var stringBuilder = StringBuilder()

    //init block
    init {
        this.mUsbListener = usbListener
        this.usbSerialIOOperation = usbSerialIOOperation
        this.mReadBuffer = usbSerialIOOperation.getReadBuffer()
    }

    /**
     * Start
     */
    fun start() {
        coroutineJob.let {
            if (it != null) {
                if (it.isActive) {
                    Log.e(TAG, "start: Already Started...")
                }
            } else {
                launchCoroutine()
            }
        }
    }

    /**
     * Stop
     */
    fun stop() {
        //close coroutine
        coroutineJob.let { cJob ->
            if (cJob != null && cJob.isActive) {
                cJob.cancel()
            } else {
                Log.e(TAG, "disconnect: Coroutine Job is null")
            }
        }
    }

    /**
     * Launch Coroutine
     */
    private fun launchCoroutine() {
        counter = 0
        coroutineJob = Job()
        coroutineJob.let { cJob ->
            if (cJob != null) {
                CoroutineScope(Dispatchers.IO + cJob).launch {
                    try {
                        while (true) {
                            mReadBuffer.let { readBuffer ->
                                if (readBuffer != null) {
                                    val buffer: ByteArray? = readBuffer.array()
                                    val len = usbSerialIOOperation.read(buffer)
                                    if (len > 0) {
                                        val data = ByteArray(len)
                                        System.arraycopy(buffer, 0, data, 0, len)
                                        Log.v(TAG, "Read Data : " + String(data))

                                        //receiveRawDataAndApplyFilter
                                        receiveRawDataAndApplyFilter(data)

                                        //send data for filtering
                                        //UsbDataFiltering.getRawDataAnApplyFilter(data)

                                    } else {
                                        Log.e(TAG, "launchCoroutine: Len is less than 0")
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Run ending due to exception: " + e.message)
                        //mUsbListener.onConnectionError("Run ending due to exception: " + e.message)
                        //MainUsbSerialHelper.disconnect()
                    }
                }
            } else {
                Log.e(TAG, "launchCoroutine: Coroutine Job is null")
                mUsbListener.onConnectionError("${ConstantHelper.ErrorCode.DATA} : Coroutine job is null")
            }
        }
    }

    /**
     * Receive Raw Data And Filter it
     * @param data = bytearray data
     */
    private fun receiveRawDataAndApplyFilter(data: ByteArray) {

        val spn = SpannableStringBuilder()
        if (data.isNotEmpty()) spn.append(String(data))

        //val cmd = "command"
        val cmd = MainUsbSerialHelper.currentCommand
        Log.w("CMD_TAG", "receiveRawDataAndApplyFilter: $cmd")

        MainUsbSerialHelper.receivedData(spn.toString())

        /*//APPLY FILTER WITH DELIMITER
        stringBuilder.append(spn)
        if (stringBuilder.isNotEmpty() || (stringBuilder.toString()
                .contains(ConstantHelper.DELIMITER))
        ) {
            val result = stringBuilder.toString().split(ConstantHelper.DELIMITER).toTypedArray()
            val lastElementOfResult = result[result.size - 1]
            val lastElementOfBuilder = stringBuilder.substring(
                stringBuilder.lastIndexOf(ConstantHelper.DELIMITER) + 1, stringBuilder.length
            )
            if (lastElementOfResult == lastElementOfBuilder) {
                returnFilteredData(result, 0, result.size - 1, cmd)
                stringBuilder.delete(
                    0, stringBuilder.lastIndexOf(ConstantHelper.DELIMITER) + 1
                )
            } else {
                returnFilteredData(result, 0, result.size, cmd)
                stringBuilder.delete(0, stringBuilder.length)
            }
        }*/
    }

    /**
     * Return Filter Data
     * @param result : data in array format
     * @param start : start index of array
     * @param end : end index of array
     * @param command : current executing command
     */
    private fun returnFilteredData(result: Array<String>, start: Int, end: Int, command: String) {
        for (i in start until end) {
            val time = ++counter
            Log.w(TAG, "returnDataToUser: Data ${result[i]} \tCounter : $time")
            MainUsbSerialHelper.receivedData(result[i])
            //UsbDataFiltering.getRawDataAnApplyFilter(result[i])
        }
    }
}