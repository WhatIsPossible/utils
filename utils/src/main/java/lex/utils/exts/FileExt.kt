package lex.utils.exts

import java.io.File

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

