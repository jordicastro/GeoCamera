package edu.uark.ahnelson.roomwithaview2024.NewPhotoLocationActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocation
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocationRepository
import kotlinx.coroutines.launch

class NewWordViewModel(private val repository: PhotoLocationRepository) : ViewModel() {
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
}

class NewWordViewModelFactory(private val repository: PhotoLocationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewWordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewWordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
