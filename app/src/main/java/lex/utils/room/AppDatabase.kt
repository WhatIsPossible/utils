package lex.utils.room

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import lex.utils.room.entity.Chat
import lex.utils.room.entity.ChatDao
import kotlin.concurrent.thread

@Database(
    entities = [Chat::class],
    version = 1,
    exportSchema = true/*,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]*/
)
abstract class AppDatabase : RoomDatabase() {

    private val mIsDatabaseCreated = MutableLiveData<Boolean>()

    abstract fun userDao(): ChatDao

    /**
     * Check whether the database already exists and expose it via [.getDatabaseCreated]
     */
    private fun updateDatabaseCreated(context: Context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated()
        }
    }

    private fun setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true)
    }

    val databaseCreated: LiveData<Boolean>
        get() = mIsDatabaseCreated

    companion object {

        @VisibleForTesting
        private const val DATABASE_NAME = "basic-sample-db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                    it.updateDatabaseCreated(context.applicationContext)
                }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, DATABASE_NAME
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        thread {
                            val database = getInstance(context)
//                            insertData(database)
                            database.setDatabaseCreated()
                        }
                    }
                })
                .build()

    }
}