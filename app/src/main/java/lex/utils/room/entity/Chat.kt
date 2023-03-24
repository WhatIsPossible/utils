package lex.utils.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import lex.utils.room.entity.Chat.Companion.TABLE_NAME

/**
 * @Author dxl
 * @Date 2023/3/8 14:58
 * @Email lex911118@gmail.com
 * @Description This is Chat
 */
@Entity(tableName = TABLE_NAME)
data class Chat(
    @PrimaryKey
    var id: String = "",
    @ColumnInfo("isSuccessful", defaultValue = "true")
    val isSuccessful: Boolean = true,
    val user: String,
    val createAt: Long,
    val type: Int,//调用api的方式类型->0:chat, 1:image
    @ColumnInfo("contentType", defaultValue = "0")
    val contentType: Int = 0,//消息类型->0:文字, 1:图片url
    var content: String = "",
    val url: String? = null,
) {

    fun setId() {
        id = "${type}_${user}_${createAt}"
    }

    companion object {
        const val TABLE_NAME = "chat"
    }
}
