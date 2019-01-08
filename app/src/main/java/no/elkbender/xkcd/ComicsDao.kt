package no.elkbender.xkcd

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
abstract class ComicsDao {
    @Query("SELECT * FROM comic")
    abstract fun getAll(): List<Comic>

    @Insert
    abstract fun insertAll(vararg comics: Comic)

    @Delete
    abstract fun delete(user: Comic)
}