package com.example.poc_usb_attach_demo

import android.app.Application
import com.example.devicedetect.MainUsbSerialHelper

class MainApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        //initialize module
        /*MainUsbSerialHelper.initialize(applicationContext, object : UsbHelperListener {
            override fun onDeviceConnect() {
            }

            override fun onDeviceVerified() {
            }

            override fun onReceivedData(data: String?) {
                Log.w("APPLICATION_CLASS", "onReceivedData: $data")
            }

            override fun onDeviceDisconnect() {
            }

            override fun onConnectionError(errorMessage: String?) {
            }

        })*/
        MainUsbSerialHelper.initialize(applicationContext)

    }

    override fun onTerminate() {
        super.onTerminate()
        //clear module
        MainUsbSerialHelper.clearInstance()
    }

}