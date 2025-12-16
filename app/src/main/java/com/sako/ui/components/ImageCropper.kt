package com.sako.ui.components

import android.content.Context
import java.io.File

/**
 * Helper function untuk create temporary file untuk hasil crop
 * (Simplified - cropping dilakukan di backend via Cloudinary transformation)
 */
fun createCropOutputFile(context: Context): File {
    val fileName = "temp_${System.currentTimeMillis()}.jpg"
    return File(context.cacheDir, fileName)
}
