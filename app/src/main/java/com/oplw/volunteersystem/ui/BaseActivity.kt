package com.oplw.volunteersystem.ui

import androidx.appcompat.app.AppCompatActivity
import com.oplw.volunteersystem.MyManager

/**
 *
 *   @author opLW
 *   @date  2019/7/11
 */
open class BaseActivity: AppCompatActivity() {
    override fun onStop() {
        MyManager.getInstance().shutdownAllConnector()
        super.onStop()
    }
}