package com.example.data

import android.content.Context
import androidx.room.*

@Entity(tableName = "saved_games")
data class SavedGame(
    @PrimaryKey val id: String, // e.g., "slot_1", "slot_2", "autosave"
    val companyName: String,
    val industry: String,
    val currentTurn: Int,
    val cash: Double,
    val savedAt: Long,
    val stateJson: String // Complete State serialized with Moshi
)

@Dao
interface SavedGameDao {
    @Query("SELECT * FROM saved_games ORDER BY savedAt DESC")
    suspend fun getAllSavedGames(): List<SavedGame>

    @Query("SELECT * FROM saved_games WHERE id = :id LIMIT 1")
    suspend fun getSavedGameById(id: String): SavedGame?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGame(savedGame: SavedGame)

    @Query("DELETE FROM saved_games WHERE id = :id")
    suspend fun deleteGame(id: String)
}

@Database(entities = [SavedGame::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedGameDao(): SavedGameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "billionaire_empire_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
