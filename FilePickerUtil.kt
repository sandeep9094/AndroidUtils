package com.developidea.filepickerandroid

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import java.io.FileDescriptor

object FilePickerUtil {

    fun openFilePicker(activity: Activity, responseCode: Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        activity.startActivityForResult(intent, responseCode)
    }

    fun viewFile(activity: Activity, fileUri: Uri?) {
        val viewIntent = Intent(Intent.ACTION_VIEW)
        viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        viewIntent.data = fileUri
        val intent = Intent.createChooser(viewIntent, "Choose an application to open with:")
        activity.startActivity(intent)
    }

    fun getFileName(activity: Activity, uri: Uri): String {
        val fileName = "Unknown"
        val cursor: Cursor? = activity.contentResolver.query(
            uri, null, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex <= 0) {
                    return fileName
                }
                return it.getString(columnIndex)
            }
        }
        return fileName
    }


    fun getBitmapFromUri(activity: Activity, uri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor: ParcelFileDescriptor? = activity.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (exception: Exception) {
            return null
        }
    }


}
