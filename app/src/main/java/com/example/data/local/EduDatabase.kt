package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.StudyQuestionEntity
import com.example.data.model.UserProgressEntity

@Database(
    entities = [
        UserProgressEntity::class,
        StudyQuestionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class EduDatabase : RoomDatabase() {
    abstract fun eduDao(): EduDao

    companion object {
        @Volatile
        private var INSTANCE: EduDatabase? = null

        fun getDatabase(context: Context): EduDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EduDatabase::class.java,
                    "edu_ai_buddy.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

