package lex.utils.bitmap

import lex.utils.exts.checkOrCreate
import java.io.File
import java.net.URL

/**
 * @Author dxl
 * @Date 2023/3/23 11:14
 * @Email lex911118@gmail.com
 * @Description This is ImageDownloadUtil
 */
object ImageDownloadUtil {

    /**
     * 下载图片原图, 保存到文件中
     *
     * @param imageUrl  要下载图片的地址
     * @param outFile   保存图片的文件
     * @return  true:保存成功; false:保存失败;
     */
    fun download(imageUrl: String, outFile: File): Boolean {
        if (imageUrl.isEmpty()) return false
        if (!outFile.checkOrCreate()) return false
        try {
            val url = URL(imageUrl)
            url.openConnection().getInputStream().use { inputStream ->
                outFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}