package lex.utils.bitmap

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import lex.utils.exts.checkOrCreate
import java.io.File
import java.io.IOException

/**
 * Save to file
 *
 * @param file  图片文件. not directory
 * @return 保存成功后返回路径. 失败返回null.
 */
fun Bitmap.saveToFile(file: File): String? {
    if (!file.checkOrCreate()) return null
    try {
        file.outputStream().use { outputStream ->
            compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
        }
        return file.absolutePath
    } catch (e: SecurityException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun Bitmap.saveToFile(filePath: String): String? {
    return saveToFile(File(filePath))
}

/**
 * Get bitmap
 * 复制在Window上 该View所在区域内视图.
 * 和该View的绘制无关.
 * @param view
 * @param window
 * @param listenerThread
 * @param callback
 * @receiver
 */
@RequiresApi(Build.VERSION_CODES.O)
fun View.getBitmap(
    window: Window,
    listenerThread: Handler,
    callback: (bitmap: Bitmap?) -> Unit
) {
    val intArray = IntArray(2)
    getLocationInWindow(intArray)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    try {
        PixelCopy.request(
            window,
            Rect(intArray[0], intArray[1], intArray[0] + width, intArray[0] + height),
            bitmap,
            { copyResult ->
                callback.invoke(
                    if (copyResult == PixelCopy.SUCCESS) {
                        bitmap
                    } else {
                        bitmap.recycle()
                        null
                    }
                )
            },
            listenerThread
        )
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        callback.invoke(null)
    }
}

/**
 * Get bitmap
 *
 * @return  返回该view上绘制的bitmap.
 *  如果该view没有背景, bitmap就是透明的
 */
fun View.getBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    draw(Canvas(bitmap))
    return bitmap
}