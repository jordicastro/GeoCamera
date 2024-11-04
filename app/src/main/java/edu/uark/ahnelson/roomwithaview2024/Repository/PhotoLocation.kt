package edu.uark.ahnelson.roomwithaview2024.Repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_location_table")
data class PhotoLocation(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name="photoLocation") val photoLocation:String,
    @ColumnInfo(name="photoLongitude") val photoLongitude:Double,
    @ColumnInfo(name="photoLatitude") val photoLatitude:Double,
    @ColumnInfo(name="photoDate") val photoDate:String,
    @ColumnInfo(name="photoDescription") val photoDescription:String
)