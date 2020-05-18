package com.mycity4kids.utils

class ImageKitUtils(val url: String, val height: Int, val width: Int) {

    fun getOptimizedImage(): String {
        return url.plus("/tr:h-" + height)
    }

    fun getVlogsCardImage(): String {
        return url.plus("/tr:w-500:w-500,h-500,cm-extract,fo-top")
    }
}
