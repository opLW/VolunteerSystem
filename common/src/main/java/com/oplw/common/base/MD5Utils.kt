package com.oplw.common.base

import java.security.MessageDigest

object MD5Utils {

    private val hexDigIts =
        arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f")

    /**
     * MD5加密
     * @param origin 字符
     * @param charsetName 编码
     * @return MD5加密之后的字符串
     */
    fun MD5Encode(origin: String, charsetName: String? = "utf8"): String? {
        var resultString: String? = null
        try {
            val md = MessageDigest.getInstance("MD5")
            resultString = if (null == charsetName || "" == charsetName) {
                byteArrayToHexString(md.digest(resultString!!.toByteArray()))
            } else {
                byteArrayToHexString(md.digest(resultString!!.toByteArray(charset(charsetName))))
            }
        } catch (e: Exception) {
            throw Exception("Error happen when MD5 encode!")
        }

        return resultString
    }


    private fun byteArrayToHexString(b: ByteArray): String {
        val resultSb = StringBuffer()
        for (i in b.indices) {
            resultSb.append(byteToHexString(b[i]))
        }
        return resultSb.toString()
    }

    private fun byteToHexString(b: Byte): String {
        var n = b.toInt()
        if (n < 0) {
            n += 256
        }
        val d1 = n / 16
        val d2 = n % 16
        return hexDigIts[d1] + hexDigIts[d2]
    }

}