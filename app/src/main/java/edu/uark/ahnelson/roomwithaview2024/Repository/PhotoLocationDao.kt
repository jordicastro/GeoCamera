package edu.uark.ahnelson.roomwithaview2024.Repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoLocationDao {

    @MapInfo(keyColumn = "id")
    @Query("SELECT * FROM photo_location_table GROUP BY markerId")
    fun getSortedPhotoLocationsByMarkerId(): Flow<Map<Int,PhotoLocation>>

    @Update
    suspend fun update(photoLocation: PhotoLocation)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(photoLocation: PhotoLocation)

    @Query("DELETE FROM photo_location_table")
    suspend fun deleteAll()

}
