package com.example.albumsmanager.utilities

import android.text.format.DateUtils
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 *   @author opLW
 *   @date  2019/3/6
 */
object FormatDateUtil {

    /*
   将日期转换为统一的字符串格式
    */
    fun dateToFormatDate(date: Date): String {
        val sf = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
        return sf.format(date)
    }

    fun makeSuitableDateFormat(date: Long) : String {
        return if (DateUtils.isToday(date)) {
            "今天"
        } else if (isYesterday(date)){
            "昨天"
        } else {
            translateCommonDate(date)
        }
    }

    private fun isYesterday(date: Long) : Boolean{
        val time = Calendar.getInstance()
        val nowYear = time.get(Calendar.YEAR)
        val nowMonth = time.get(Calendar.MONTH)
        val nowDay = time.get(Calendar.DAY_OF_MONTH)

        time.time = Date(date)
        time.add(Calendar.DAY_OF_MONTH, 1) // 把日期加一天，如果等于现在则表示该日期是昨天
        /*Log.i("time", "" + nowYear + " " + nowMonth + " " + nowDay + " " +
                time.get(Calendar.YEAR) + " " + time.get(Calendar.MONTH) + " " + time.get(Calendar.DAY_OF_MONTH))*/
        return nowYear == time.get(Calendar.YEAR) &&
                nowMonth == time.get(Calendar.MONTH) &&
                nowDay == time.get(Calendar.DAY_OF_MONTH)
    }

    /*
    将代表日期的Long转化为一般格式，如2018年09月03日 星期日
     */
    private fun translateCommonDate(date: Long) : String{
        val now = Calendar.getInstance()
        val then = Calendar.getInstance().also { it.time = Date(date) }
        val sb = StringBuilder()
        val weekAry = arrayOf("星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")

        //判断年份是否和当前一样，一样则不添加
        if (then.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
            sb.append(then.get(Calendar.YEAR))
            sb.append("年")
        }
        sb.append(then.get(Calendar.MONTH) + 1)
        sb.append("月")
        sb.append(then.get(Calendar.DAY_OF_MONTH))
        sb.append("日")
        sb.append("  ")
        sb.append(weekAry[then.get(Calendar.DAY_OF_WEEK) - 1])

        return sb.toString()
    }

    /*
    将如2019:03:07 18:37:02的时间串，转换成2019年3月7日和28:37两个字符串
     */
     fun getDateAndTime(`when`: String) : Array<String>{
        //使用range的时候不用考虑endIndex减一的情况
        val year = `when`.substring(0..3).toInt()
        val month = `when`.substring(5..6).toInt()
        val day = `when`.substring(8..9).toInt()
        val date =  String.format("%d年%d月%d日", year, month, day)

        val start = `when`.indexOf(" ", 0) + 1
        val end = `when`.lastIndexOf(":", `when`.lastIndex)
        val time =  `when`.substring(start, end)

        return arrayOf(date, time)
    }
}