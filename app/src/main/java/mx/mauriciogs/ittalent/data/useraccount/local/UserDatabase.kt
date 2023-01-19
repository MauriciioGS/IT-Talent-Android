package mx.mauriciogs.ittalent.data.useraccount.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mx.mauriciogs.ittalent.data.useraccount.local.entities.UserProfileEntity

private const val DATABASE_USER_VERSION = 1
private const val DATABASE_NAME = "user_local"

@Database(entities = [UserProfileEntity::class],
    version = DATABASE_USER_VERSION, exportSchema = false)

abstract class UserDatabase : RoomDatabase() {

    abstract val userDao: UserDao

    companion object {

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: build(context).also { INSTANCE = it }
        }

        fun build(context: Context) =
            Room.databaseBuilder(context, UserDatabase::class.java, DATABASE_NAME)
                // permite a Room recrear las tablas de la BD si las migraciones para migrar
                // al esquema más reciente no son encontradas
                .fallbackToDestructiveMigration()
                .build()
    }
}