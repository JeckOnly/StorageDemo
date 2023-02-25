package com.jeckonly.storagedemo

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.Uri

import androidx.annotation.AnyRes


object UriUtil {

    /**
     * 返回resource文件夹的文件的uri
     */
    fun getUriToResource(
        context: Context,
        @AnyRes resId: Int
    ): Uri {
        val res: Resources = context.resources
        return getUriToResource(res, resId)
    }

    private fun getUriToResource(
        res: Resources,
        @AnyRes resId: Int
    ): Uri {
        val imageUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(res.getResourcePackageName(resId))
            .appendPath(res.getResourceTypeName(resId))
            .appendPath(res.getResourceEntryName(resId))
            .build()
        return imageUri
    }
}