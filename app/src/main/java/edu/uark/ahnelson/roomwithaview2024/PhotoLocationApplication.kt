package edu.uark.ahnelson.roomwithaview2024

import android.app.Application
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocationRepository
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocationRoomDatabase
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocationDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class PhotoLocationApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { PhotoLocationRoomDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { PhotoLocationRepository(database.PhotoLocationDao()) }
}
