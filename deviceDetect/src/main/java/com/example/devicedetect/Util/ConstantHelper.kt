package com.example.devicedetect.Util

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object ConstantHelper {
    internal const val spandanVendorId = 1155
    internal const val spandanVendorId1 = 4292
    internal const val spandanVendorId2 = 9025
    internal const val REQUEST_TO_UNLOCK = "RTU"
    internal const val REQUEST_TO_CONNECT = "RTC"
    internal const val START_KEY = "1"
    internal const val STOP_KEY = "0"
    internal const val DELIMITER = "Y"
    internal const val NOISE = "SPDN"

    //Device Config
    /*internal var BAUD_RATE = 115200
    internal var REQUEST_CODE = 0x22
    internal var STOP_BIT = 0
    internal var PARITY_BIT = 0x00
    internal var DATA_BIT = 0x08*/

    internal var BAUD_RATE = 115200
    internal var REQUEST_CODE = 0x22//or 0X20
    internal var STOP_BIT = 0
    internal var PARITY_BIT = 0x00
    internal var DATA_BIT = 0x08

    //ENUM Class
    internal enum class UsbPermission {
        Unknown, Requested, Granted, Denied
    }

    internal enum class ErrorCode {
        AUTHENTICATION, CONNECTION, COMMAND, ENDPOINT, USB_REQUEST, DATA
    }

    //Functions
    @Throws(NoSuchAlgorithmException::class)
    internal fun getSHA(input: String): ByteArray? {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(input.toByteArray(StandardCharsets.UTF_8))
    }

    internal fun toHexString(hash: ByteArray?): String {
        val number = BigInteger(1, hash)
        val hexString = StringBuilder(number.toString(16))
        while (hexString.length < 64) {
            hexString.insert(0, '0')
        }
        return hexString.toString()
    }
}