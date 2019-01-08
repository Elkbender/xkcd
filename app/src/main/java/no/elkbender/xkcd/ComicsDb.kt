package no.elkbender.xkcd

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Comic::class], version = 1)
abstract class ComicsDb : RoomDatabase() {
    abstract fun comicsDao(): ComicsDao

    companion object {
        private const val DATABASE_NAME = "comics-db"

        fun buildDatabase(context: Context): ComicsDb {
            return Room.databaseBuilder(
                context,
                ComicsDb::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }
}