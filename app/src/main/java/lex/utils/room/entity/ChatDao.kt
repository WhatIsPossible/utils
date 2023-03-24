package lex.utils.room.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * @Author dxl
 * @Date 2023/3/8 15:16
 * @Email lex911118@gmail.com
 * @Description This is ChatDao
 */
@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addChat(chat: Chat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addChats(chats: List<Chat>)

    @Query("DELETE FROM ${Chat.TABLE_NAME}")
    suspend fun deleteAllChat()

    @Query("SELECT * FROM ${Chat.TABLE_NAME} WHERE user = :user")
    fun getChatsByUser(user: String): Flow<List<Chat>>

    @Query("SELECT * FROM ${Chat.TABLE_NAME}")
    fun getAllChats(): Flow<List<Chat>>

    @Query("SELECT * FROM ${Chat.TABLE_NAME} WHERE type = :type")
    fun getAllChatsByType(type: Int): Flow<List<Chat>>
}