package lex.utils.exts

import android.content.ContentProvider
import java.io.File
import java.io.IOException

/**
 * 获取文件大小.
 * 单位:B
 */
val File?.size: Long
    get() {
        if (this == null) return 0
        var size: Long = 0
        try {
            if (this.isDirectory) {
                val fileList = this.listFiles() ?: return 0
                for (file in fileList) {
                    size += file.size
                }
            } else {
                size += this.length()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

val File?.formatSize: String
    get() {
        val size = this.size
        return if (size < 1024) {
            "${size}B"
        } else if (size < 1048576) {
            "${size / 1024}KB"
        } else if (size < 1073741824) {
            "${size / 1048576}MB"
        } else {
            "${size / 1073741824}GB"
        }
    }

fun File.deleteFile() {
    if (exists()) {
        if (isDirectory) {
            val childFile = listFiles()
            if (childFile != null && childFile.isNotEmpty()) {
                for (f in childFile) {
                    f.deleteFile()
                }
            }
        }
        if (!delete()) {
            deleteOnExit()
        }
    }
}

/**
 * 检查文件是否存在, 如果不存在就创建
 *
 * @return true:文件已经存在 或文件被创建; false:出现异常
 */
fun File.checkOrCreate(): Boolean {
    try {
        if (!exists()) {
            parentFile?.run {
                if (!exists()) {
                    mkdirs()//创建父级目录
                }
            }
            if (!exists()) {
                createNewFile()//创建文件
            }
        }
        return true
    } catch (ioException: IOException) {
        ioException.printStackTrace()
    } catch (securityException: SecurityException) {
        securityException.printStackTrace()
    }
    return false
}

fun ContentProvider.insertMediaImage(){

}