package edu.uark.ahnelson.roomwithaview2024.Repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class PhotoLocationRepository(private val photoLocationDao: PhotoLocationDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allPhotoLocations: Flow<Map<Int,PhotoLocation>> = photoLocationDao.getAllPhotoLocations()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(photoLocation: PhotoLocation) {
        photoLocationDao.insert(photoLocation)
    }

    @WorkerThread
    suspend fun update(photoLocation: PhotoLocation){
        photoLocationDao.update(photoLocation)
    }


}
