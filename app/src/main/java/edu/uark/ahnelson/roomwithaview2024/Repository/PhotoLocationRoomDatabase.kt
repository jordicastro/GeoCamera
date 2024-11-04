package edu.uark.ahnelson.roomwithaview2024.Repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(PhotoLocation::class), version = 1, exportSchema = false)
public abstract class PhotoLocationRoomDatabase : RoomDatabase() {

    abstract fun PhotoLocationDao(): PhotoLocationDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: PhotoLocationRoomDatabase? = null

        fun getDatabase(context: Context, scope:CoroutineScope): PhotoLocationRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhotoLocationRoomDatabase::class.java,
                    "photo_location_database"
                ).addCallback(PhotoLocationDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
    private class PhotoLocationDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.PhotoLocationDao())
                }
            }
        }

        suspend fun populateDatabase(photoLocationDao: PhotoLocationDao) {
            // Delete all content here.
            photoLocationDao.deleteAll()

            // Add sample words.
            var photoLocation = PhotoLocation(null,"", 0.00, 0.00, "00-00-00", "")
            photoLocationDao.insert(photoLocation)
            photoLocation = PhotoLocation(null,"", 0.00, 0.00, "00-00-00", "")
            photoLocationDao.insert(photoLocation)

            // TODO: Add your own words!
        }
    }
}



