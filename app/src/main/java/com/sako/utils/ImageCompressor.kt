package com.sako.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Image Compressor Utility
 * Compress gambar hingga ukuran maksimal 500KB dengan maintain aspect ratio
 */
object ImageCompressor {
    
    private const val MAX_FILE_SIZE_KB = 250 // 250KB untuk loading lebih cepat
    private const val MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_KB * 1024
    private const val MAX_DIMENSION = 1080 // Max width/height
    
    /**
     * Compress image file hingga maksimal 500KB
     */
    suspend fun compressImage(context: Context, sourceUri: Uri): File? = withContext(Dispatchers.IO) {
        try {
            // Decode dengan inSampleSize untuk efisiensi memory
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            
            context.contentResolver.openInputStream(sourceUri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }
            
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, MAX_DIMENSION, MAX_DIMENSION)
            options.inJustDecodeBounds = false
            
            // Decode bitmap dengan sampling
            val bitmap = context.contentResolver.openInputStream(sourceUri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            } ?: return@withContext null
            
            // Rotate jika perlu (fix orientation dari EXIF)
            val rotatedBitmap = rotateImageIfRequired(context, bitmap, sourceUri)
            
            // Compress dengan quality bertahap hingga < 500KB
            val outputFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            var quality = 90
            var fileSize: Long
            
            do {
                FileOutputStream(outputFile).use { out ->
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                }
                fileSize = outputFile.length()
                quality -= 10
            } while (fileSize > MAX_FILE_SIZE_BYTES && quality > 20)
            
            // Cleanup
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }
            rotatedBitmap.recycle()
            
            android.util.Log.d("ImageCompressor", "Original: ${getFileSize(context, sourceUri)}KB, Compressed: ${fileSize / 1024}KB, Quality: ${quality + 10}")
            
            outputFile
        } catch (e: Exception) {
            android.util.Log.e("ImageCompressor", "Error compressing image", e)
            null
        }
    }
    
    /**
     * Compress bitmap file yang sudah ada
     */
    suspend fun compressImageFile(file: File): File? = withContext(Dispatchers.IO) {
        try {
            // Decode dengan inSampleSize
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)
            
            options.inSampleSize = calculateInSampleSize(options, MAX_DIMENSION, MAX_DIMENSION)
            options.inJustDecodeBounds = false
            
            val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
                ?: return@withContext null
            
            // Rotate jika perlu
            val rotatedBitmap = rotateImageIfRequired(file, bitmap)
            
            // Compress
            val outputFile = File(file.parent, "compressed_${System.currentTimeMillis()}.jpg")
            var quality = 90
            var fileSize: Long
            
            do {
                FileOutputStream(outputFile).use { out ->
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                }
                fileSize = outputFile.length()
                quality -= 10
            } while (fileSize > MAX_FILE_SIZE_BYTES && quality > 20)
            
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }
            rotatedBitmap.recycle()
            
            android.util.Log.d("ImageCompressor", "Original: ${file.length() / 1024}KB, Compressed: ${fileSize / 1024}KB, Quality: ${quality + 10}")
            
            outputFile
        } catch (e: Exception) {
            android.util.Log.e("ImageCompressor", "Error compressing file", e)
            null
        }
    }
    
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    private fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap {
        try {
            context.contentResolver.openInputStream(selectedImage)?.use { input ->
                val ei = ExifInterface(input)
                val orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                
                return when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270f)
                    else -> img
                }
            }
        } catch (e: IOException) {
            android.util.Log.e("ImageCompressor", "Error reading EXIF", e)
        }
        return img
    }
    
    private fun rotateImageIfRequired(file: File, img: Bitmap): Bitmap {
        try {
            val ei = ExifInterface(file.absolutePath)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270f)
                else -> img
            }
        } catch (e: IOException) {
            android.util.Log.e("ImageCompressor", "Error reading EXIF", e)
        }
        return img
    }
    
    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degree) }
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }
    
    private fun getFileSize(context: Context, uri: Uri): Long {
        return try {
            context.contentResolver.openInputStream(uri)?.use { it.available().toLong() } ?: 0L
        } catch (e: Exception) {
            0L
        } / 1024
    }
}
