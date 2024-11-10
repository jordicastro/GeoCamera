package edu.uark.ahnelson.roomwithaview2024.NewPhotoLocationActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocation
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.math.abs

class NewPhotoLocationViewModel(private val repository: PhotoLocationRepository) : ViewModel() {
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    val allPhotoLocations: LiveData<Map<Int,PhotoLocation>> = repository.allPhotoLocations.asLiveData()
    val _photoLocation = MutableLiveData<PhotoLocation>().apply{value=null}
    val photoLocation: LiveData<PhotoLocation>
        get() = _photoLocation

    fun start(wordId:Int){
        viewModelScope.launch {
            repository.allPhotoLocations.collect{
                _photoLocation.value = it[wordId]
            }
        }
    }

    fun insert(photoLocation: PhotoLocation) = viewModelScope.launch {
        repository.insert(photoLocation)
    }

    fun update(photoLocation: PhotoLocation) = viewModelScope.launch {
        repository.update(photoLocation)
    }

    fun updateDescription(description: String) {
        val photoLocation = _photoLocation.value ?: return
        photoLocation.photoDescription = description
        update(photoLocation)
    }

    // isAtSameLocation: checks if the photoLocation is at the same location as another photoLocation, groups them by markerId
//    fun isAtSameLocation(latitude: Double, longitude: Double, callback: (Boolean, Int) -> Unit) {
//        viewModelScope.launch {
//            repository.allPhotoLocations.collect {
//                checkIfAtSameLocation(it, latitude, longitude, callback)
//            }
//
//            callback(false, -1)
//        }
//    }
//
//    private fun checkIfAtSameLocation(it: Map<Int, PhotoLocation>, latitude: Double, longitude: Double, callback: (Boolean, Int) -> Unit): Map<Int, PhotoLocation> {
//        for (photoLocation in it) {
//            val thisLatitude = photoLocation.value.photoLatitude
//            val thisLongitude = photoLocation.value.photoLongitude
//            if (abs(thisLatitude - latitude) < 0.0005 && abs(thisLongitude - longitude) < 0.0005) {
//                callback(true, photoLocation.value.markerId)
//                return it
//            }
//        }
//        callback(false, -1)
//        return it
//    }
}

class NewPhotoLocationViewModelFactory(private val repository: PhotoLocationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewPhotoLocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewPhotoLocationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}