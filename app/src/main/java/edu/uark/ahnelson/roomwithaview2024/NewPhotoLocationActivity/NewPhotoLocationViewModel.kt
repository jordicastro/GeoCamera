package edu.uark.ahnelson.roomwithaview2024.NewPhotoLocationActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocation
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NewPhotoLocationViewModel(private val repository: PhotoLocationRepository) : ViewModel() {
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
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

    fun getUniquePhotoLocations(): List<PhotoLocation> { // returns photo locations with unique markerIds (grouped by markerId)
        return repository.getUniquePhotoLocations()
    }
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
