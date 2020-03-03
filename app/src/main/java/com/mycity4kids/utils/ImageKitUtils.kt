package com.mycity4kids.utils

class ImageKitUtils(val url: String, val height: Int, val width: Int) {

    fun getOptimizedImage(): String {
        return url.plus("/tr:h-" + height)
    }
}
