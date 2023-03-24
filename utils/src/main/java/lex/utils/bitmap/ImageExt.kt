@file:JvmName("ImageExt")
@file:Suppress("unused")

package lex.utils.bitmap

import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.OutputStream


private const val TAG = "ImageExt"

private val ALBUM_DIR = Environment.DIRECTORY_PICTURES

private class OutputFileTaker(var file: File? = null)

/**
 * 复制图片文件到相册的Pictures文件夹
 *
 * @param context 上下文
 * @param fileName 文件名。 需要携带后缀
 * @param relativePath 相对于Pictures的路径
 */
fun File.copyToAlbum(context: Context, fileName: String, relativePath: String?): Uri? {
    if (!this.canRead() || !this.exists()) {
        Log.w(TAG, "check: read file error: $this")
        return null
    }
    return this.inputStream().saveToAlbum(context, fileName, relativePath)
}

/**
 * 保存Bitmap到相册的Pictures文件夹
 *
 * https://developer.android.google.cn/training/data-storage/shared/media
 *
 * @param context 上下文
 * @param fileName 文件名。 需要携带后缀
 * @param relativePath 相对于Pictures的路径
 * @param quality 质量
 */
fun Bitmap.saveToAlbum(
    context: Context,
    fileName: String,
    relativePath: String? = null,
    quality: Int = 75,
): Uri? {
    // 插入图片信息
    val resolver = context.contentResolver
    val outputFile = OutputFileTaker()
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        resolver.insertMediaImage(fileName, relativePath)
    } else {
        resolver.insertMediaImage28(fileName, relativePath, outputFile)
    }?.apply {
        // 保存图片
        (outputStream(resolver) ?: return null).use { outputStream ->
            val format = fileName.getBitmapFormat()
            this@saveToAlbum.compress(format, quality, outputStream)
        }
        // 通知媒体库更新
        finishPending(context, resolver, outputFile.file)
    }
}

/**
 * 保存图片到相册
 *
 * @param context   上下文
 * @param fileName  图片名称.需要携带后缀
 * @param relativePath  相对于Pictures的路径
 * @return
 */
fun InputStream.saveToAlbum(context: Context, fileName: String, relativePath: String?): Uri? {
    val resolver = context.contentResolver
    val outputFile = OutputFileTaker()
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        resolver.insertMediaImage(fileName, relativePath)
    } else {
        resolver.insertMediaImage28(fileName, relativePath, outputFile)
    }?.apply {
        // 保存图片
        (outputStream(resolver) ?: return null).use { outputStream ->
            use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        // 通知媒体库更新
        finishPending(context, resolver, outputFile.file)
    }
}

private fun Uri.finishPending(
    context: Context,
    resolver: ContentResolver,
    outputFile: File?,
) {
    val imageValues = ContentValues()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android Q添加了IS_PENDING状态，为0时其他应用才可见
        imageValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(this, imageValues, null, null)
    } else {
        if (outputFile != null) {
            imageValues.put(MediaStore.Images.Media.SIZE, outputFile.length())
        }
        resolver.update(this, imageValues, null, null)
        // 通知媒体库更新
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, this)
        context.sendBroadcast(intent)
    }
}

/**
 * 插入图片到媒体库
 * android10及以上
 * @param fileName  图片名称
 * @param relativePath  相对路径
 * @return
 */
@RequiresApi(Build.VERSION_CODES.Q)
private fun ContentResolver.insertMediaImage(
    fileName: String, relativePath: String?
): Uri? {
    val values = ContentValues().apply {
        val mimeType = fileName.getMimeType()
        if (mimeType != null) {
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        }
        val date = System.currentTimeMillis() / 1000
        put(MediaStore.Images.Media.DATE_ADDED, date)
        put(MediaStore.Images.Media.DATE_MODIFIED, date)

        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        val path = if (relativePath != null) "${ALBUM_DIR}/${relativePath}" else ALBUM_DIR
        put(MediaStore.Images.Media.RELATIVE_PATH, path)
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }
    return insert(MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values)
}

/**
 * 插入图片到媒体库
 * android10以下
 * @param fileName
 * @param relativePath
 * @return
 */
private fun ContentResolver.insertMediaImage28(
    fileName: String, relativePath: String?, outputFile: OutputFileTaker
): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return null

    // 老版本. 需要获取WRITE_EXTERNAL_STORAGE权限
    val pictures = Environment.getExternalStoragePublicDirectory(ALBUM_DIR)
    val saveDir = if (relativePath != null) File(pictures, relativePath) else pictures

    if (!saveDir.exists() && !saveDir.mkdirs()) {
        Log.e(TAG, "save: error: can't create Pictures directory")
        return null
    }

    // 文件路径查重，重复的话在文件名后拼接数字
    var imageFile = File(saveDir, fileName)
    val fileNameWithoutExtension = imageFile.nameWithoutExtension
    val fileExtension = imageFile.extension

    var queryUri = queryMediaImage28(imageFile.absolutePath)
    var suffix = 1
    while (queryUri != null) {
        val newName = fileNameWithoutExtension + "(${suffix++})." + fileExtension
        imageFile = File(saveDir, newName)
        queryUri = queryMediaImage28(imageFile.absolutePath)
    }

    outputFile.file = imageFile
    val values = ContentValues().apply {
        val mimeType = fileName.getMimeType()
        if (mimeType != null) {
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        }
        val date = System.currentTimeMillis() / 1000
        put(MediaStore.Images.Media.DATE_ADDED, date)
        put(MediaStore.Images.Media.DATE_MODIFIED, date)

        put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
        // 保存路径
        put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
    }

    return insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
}

private fun Uri.outputStream(resolver: ContentResolver): OutputStream? {
    return try {
        resolver.openOutputStream(this)
    } catch (e: FileNotFoundException) {
        Log.e(TAG, "save: open stream error: $e")
        null
    }
}

private fun String.getBitmapFormat(): Bitmap.CompressFormat {
    val fileName = this.lowercase()
    return when {
        fileName.endsWith(".png") -> Bitmap.CompressFormat.PNG
        fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") -> Bitmap.CompressFormat.JPEG
        fileName.endsWith(".webp") -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            Bitmap.CompressFormat.WEBP_LOSSLESS else Bitmap.CompressFormat.WEBP
        else -> Bitmap.CompressFormat.PNG
    }
}

private fun String.getMimeType(): String? {
    val fileName = this.lowercase()
    return when {
        fileName.endsWith(".png") -> "image/png"
        fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") -> "image/jpeg"
        fileName.endsWith(".webp") -> "image/webp"
        fileName.endsWith(".gif") -> "image/gif"
        else -> null
    }
}

/**
 * Android Q以下版本，查询媒体库中当前路径是否存在
 * @return Uri 返回null时说明不存在，可以进行图片插入逻辑
 */
private fun ContentResolver.queryMediaImage28(imagePath: String): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return null

    val imageFile = File(imagePath)
    if (imageFile.canRead() && imageFile.exists()) {
        Log.v(TAG, "query: path: $imagePath exists")
        // 文件已存在，返回一个file://xxx的uri
        return Uri.fromFile(imageFile)
    }
    // 保存的位置
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    // 查询是否已经存在相同图片
    val query = this.query(
        collection,
        arrayOf(MediaStore.Images.Media._ID, @Suppress("DEPRECATION") MediaStore.Images.Media.DATA),
        "${@Suppress("DEPRECATION") MediaStore.Images.Media.DATA} == ?",
        arrayOf(imagePath), null
    )
    query?.use {
        while (it.moveToNext()) {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val id = it.getLong(idColumn)
            val existsUri = ContentUris.withAppendedId(collection, id)
            Log.v(TAG, "query: path: $imagePath exists uri: $existsUri")
            return existsUri
        }
    }
    return null
}
