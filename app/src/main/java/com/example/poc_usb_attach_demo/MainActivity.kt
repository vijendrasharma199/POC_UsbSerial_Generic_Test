package com.example.poc_usb_attach_demo

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.devicedetect.MainUsbSerialHelper
import com.example.devicedetect.UsbHelperListener
import com.example.poc_usb_attach_demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), UsbHelperListener {

    private var TAG = "USB_COMMUNICATION_HELPER"

    lateinit var binding: ActivityMainBinding

    //Module Variables
    var arraylist = ArrayList<String>()

    //Required Variable
    var divideBy: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.w(TAG, "onCreate:")

        initialization()

        binding.sendBtn.setOnClickListener { sendCommand() }

        binding.clearBtn.setOnClickListener {
            binding.receivedTextTv.text = ""
            binding.cmdEt.setText("")
            binding.timerEt.setText("")
            binding.receivedTextTv.text = "Size of list : ${arraylist.size}\n"
            arraylist.clear()
        }

        //set default partition
        binding.partitionEt.setText("100")

        MainUsbSerialHelper.setDeviceConfiguration(115200, 0x20, 0x08, 0x00, 0)
        //MainUsbSerialHelper.setDeviceConfiguration(115200, 0x22, 0x08, 0x00, 0)

        MainUsbSerialHelper.setDeviceCallback(this, this)
    }

    private fun sendCommand() {
        arraylist.clear()
        val cmd = binding.cmdEt.text.toString().trim()
        val time = binding.timerEt.text.toString().trim()

        val partitionBy = binding.partitionEt.text.toString().trim()
        divideBy = if (partitionBy.isNotEmpty()) partitionBy.toInt() else 100

        if (!TextUtils.isEmpty(cmd)) {
            binding.receivedTextTv.append("----------------------\nSend : $cmd\n")
            if (time.isNotEmpty()) {
                val thread = Thread {
                    MainUsbSerialHelper.sendCommand("1")
                    Thread.sleep(time.toInt() * 1000L)
                    MainUsbSerialHelper.sendCommand("0")
                }
                thread.start()
            } else {
                MainUsbSerialHelper.sendCommand(cmd)
            }
        } else {
            showToast("Please enter command...")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun initialization() {
        binding.receivedTextTv.movementMethod = ScrollingMovementMethod()
    }

    override fun onDeviceConnect() {
        Log.d(TAG, "Activity : Device Connected...")
        runOnUiThread {
            binding.receivedTextTv.append("Device Connected....\n")
            binding.deviceStatusTv.text = "Device Connected..."
            binding.deviceStatusTv.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.verify_color))
        }
    }

    override fun onDeviceVerified() {
        Log.d(TAG, "Activity : Device Verified...")
        binding.receivedTextTv.append("Received : Device ready for communication\n")
        binding.deviceStatusTv.text = "Device Verified..."
        binding.deviceStatusTv.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.start_color))
    }

    override fun onReceivedData(data: String) {
        Log.w(TAG, "Activity : $data")
        //runOnUiThread { binding.receivedTextTv.append("Received : $data\n") }

        arraylist.add(data)
        if (arraylist.size % divideBy == 0) {
            var size = arraylist.size
            runOnUiThread { binding.receivedTextTv.append("Received : $data : Size of List : $size\n") }
            //runOnUiThread { binding.receivedTextTv.append("Received : $data\n") }
        }
    }

    override fun onDeviceDisconnect() {
        Log.d(TAG, "Activity : Device Disconnected")
        runOnUiThread {
            binding.receivedTextTv.append("Device disconnected or transmission stopped.....\n")
            binding.deviceStatusTv.text = "Device Disconnected..."
            binding.deviceStatusTv.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.stop_color))
            binding.receivedTextTv.append("Size of List : ${arraylist.size}\n")
        }
    }

    override fun onConnectionError(errorMessage: String?) {
        Log.e(TAG, "onConnectionError: $errorMessage")
        runOnUiThread {
            binding.deviceStatusTv.text = "Device Error..."
            binding.deviceStatusTv.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.stop_color))
            binding.receivedTextTv.append("$errorMessage\n")
        }
    }

    fun goToActivityMain2(view: View) {
        startActivity(Intent(this, MainActivity2::class.java))
        //finish()
    }
}