package edu.uark.ahnelson.roomwithaview2024.MainActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocation
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocationRepository
import kotlinx.coroutines.launch

class PhotoLocationViewModel(private val repository: PhotoLocationRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allPhotoLocations: LiveData<Map<Int,PhotoLocation>> = repository.allPhotoLocations.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(photoLocation: PhotoLocation) = viewModelScope.launch {
        repository.insert(photoLocation)
    }
}

class PhotoLocationViewModelFactory(private val repository: PhotoLocationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoLocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhotoLocationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
