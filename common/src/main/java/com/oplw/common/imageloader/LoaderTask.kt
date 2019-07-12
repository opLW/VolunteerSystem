package com.oplw.common.imageloader

import android.view.View

/**
 *
 *   @author opLW
 *   @date  2019/7/11
 */
class LoaderTask(val target: View,
                 val resourceId: Int,
                 val size: Size) {

    inner class Size(val width: Int, val height: Int)


}

