package com.andreygolovin.imageconverter.model

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ConverterModel {

    lateinit var context: Context
    lateinit var savedPNGFile: File

    fun getBitmap(data: Intent?): Bitmap {
        val uri = data?.data
        return BitmapFactory.decodeStream(uri?.let { context.contentResolver.openInputStream(it) })
    }

    fun saveConvertedFileInStorage(bitmap: Bitmap) {
        val directory = createDirectory()
        savedPNGFile = createNewFile(directory)
        convertJPGToPNG(savedPNGFile, bitmap)
    }

    private fun convertJPGToPNG(savedPNGFile: File, bitmap: Bitmap) {
        val fileOutputStream = FileOutputStream(savedPNGFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.close()
    }

    private fun createDirectory(): String {
        val directory =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/PNG"
        val outputDir = File(directory)
        outputDir.mkdirs()
        return directory
    }

    private fun createNewFile(directory: String): File {
        return File(
            directory + File.separator + StringBuilder()
                .append(
                    SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
                        .format(Date())
                )
                .append(EXTENSION_FORMAT)
        )
    }

    companion object {
        private const val DATE_FORMAT = "dd-MM-yyyy_hh_mm_ss"
        private const val EXTENSION_FORMAT = ".png"
    }
}