package edu.uark.ahnelson.roomwithaview2024.PinActivity

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uark.ahnelson.roomwithaview2024.MainActivity.PhotoLocationListAdapter
import edu.uark.ahnelson.roomwithaview2024.MainActivity.PhotoLocationViewModel
import edu.uark.ahnelson.roomwithaview2024.MainActivity.PhotoLocationViewModelFactory
import edu.uark.ahnelson.roomwithaview2024.MapsActivity.MapsActivity
import edu.uark.ahnelson.roomwithaview2024.NewPhotoLocationActivity.EditPhotoLocationActivity
import edu.uark.ahnelson.roomwithaview2024.PhotoLocationApplication
import edu.uark.ahnelson.roomwithaview2024.R
import java.io.IOException
import java.util.Locale
import kotlin.properties.Delegates

class PinActivity: AppCompatActivity() {

    private var markerId by Delegates.notNull<Int>()
    private var longitude by Delegates.notNull<Double>()
    private var latitude by Delegates.notNull<Double>()
    //private lateinit var textLocationView: TextView

    private val newPhotoLocationViewModel : PhotoLocationViewModel by viewModels {
        PhotoLocationViewModelFactory((application as PhotoLocationApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)
        enableEdgeToEdge()

        markerId = intent.getIntExtra("MARKER_ID", 0)
        longitude = intent.getDoubleExtra("LONGITUDE", 0.0)
        latitude = intent.getDoubleExtra("LATITUDE", 0.0)
        Log.d("PinActivity", "PinActivity: Marker ID: $markerId, Longitude: $longitude, Latitude: $latitude")

        //textLocationView = findViewById(R.id.text_location)
        //setHumanReadableLocation(latitude, longitude)

        //Get reference to recyclerView object
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        //Create adapter class, passing the launchNewTaskActivity callback
        val adapter = PhotoLocationListAdapter(this::launchEditPhotoLocationActivity)
        //Set the adapter for the recyclerView to the adapter object
        recyclerView.adapter = adapter
        //Set the recyclerview layout to be a linearLayoutManager with activity context
        recyclerView.layoutManager = LinearLayoutManager(this)

        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        newPhotoLocationViewModel.allPhotoLocations.observe(this) { photolocations ->
            // Update the cached copy of the words in the adapter.
            // only pass in the photoLocations that have the same markerId as the one that was clicked
            val filteredPhotoLocations = photolocations.filter { it.value.markerId == markerId }
            adapter.submitList(filteredPhotoLocations.map { it.value })
        }

    }

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private fun setHumanReadableLocation(latitudeX: Double, longitudeY: Double) {
//        Log.d("EditPhotoLocationActivity", "setHumanReadableLocation latitude $latitudeX, longitude $longitudeY")
//        val notFound = "Location not found"
//        var addressText = ""
//        val geocoder = Geocoder(this, Locale.getDefault())
//        try {
//            val addresses = geocoder.getFromLocation(latitudeX, longitudeY, 1, object : Geocoder.GeocodeListener {
//                override fun onGeocode(addresses: MutableList<Address>) {
//                    val address = addresses[0]
//                    addressText = address.getAddressLine(0)
//                    textLocationView.text = addressText
//
//
//                }
//                override fun onError(errorMessage: String?) {
//                    Log.e("EditPhotoLocationActivity", "Geocoder failed")
//
//                }
//            })
//
//        } catch (e: IOException) {
//            Log.e("EditPhotoLocationActivity", "Geocoder failed", e)
//            textLocationView.text = notFound
//        }
//    }

    private fun launchEditPhotoLocationActivity(id: Int) {
        Log.d("PinActivity", "launchEditPhotoLocationActivity: id: $id")
        val secondActivityIntent = Intent(this, EditPhotoLocationActivity::class.java).apply {
            putExtra("PHOTO_PATH", "")
            putExtra("TIME_STAMP", "")
            putExtra("LONGITUDE", 0.00)
            putExtra("LATITUDE", 0.00)
            putExtra("MARKER_ID", 0)
            putExtra("ID", id)
        }
        startActivity(secondActivityIntent)
    }
}