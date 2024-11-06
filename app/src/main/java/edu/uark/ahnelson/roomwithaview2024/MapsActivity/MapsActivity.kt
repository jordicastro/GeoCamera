package edu.uark.ahnelson.roomwithaview2024.MapsActivity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.uark.ahnelson.roomwithaview2024.MainActivity.PhotoLocationListAdapter
import edu.uark.ahnelson.roomwithaview2024.MainActivity.PhotoLocationViewModel
import edu.uark.ahnelson.roomwithaview2024.MainActivity.PhotoLocationViewModelFactory
import edu.uark.ahnelson.roomwithaview2024.PhotoLocationApplication
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocation
import edu.uark.ahnelson.roomwithaview2024.Util.LocationUtilCallback
import edu.uark.ahnelson.roomwithaview2024.Util.createLocationCallback
import edu.uark.ahnelson.roomwithaview2024.Util.createLocationRequest
import edu.uark.ahnelson.roomwithaview2024.Util.replaceFragmentInActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapsActivity : AppCompatActivity() {
    //mapsFragment object to hold the fragment
    private lateinit var mapsFragment: OpenStreetMapFragment
    //Boolean to keep track of whether permissions have been granted
    private var locationPermissionEnabled: Boolean = false
    //Boolean to keep track of whether activity is currently requesting location Updates
    private var locationRequestsEnabled: Boolean = false
    //Member object for the FusedLocationProvider
    private lateinit var locationProviderClient: FusedLocationProviderClient
    //Member object for the last known location
    private lateinit var mCurrentLocation: Location
    //Member object to hold onto locationCallback object
    //Needed to remove requests for location updates
    private lateinit var mLocationCallback: LocationCallback

    // photo path and time stamp
    private var currentPhotoPath = ""
    private var currentTimeStamp = ""
    private var currentLongitude = 0.00
    private var currentLatitude = 0.00
    private val newPhotoLocationViewModel : PhotoLocationViewModel by viewModels {
        PhotoLocationViewModelFactory((application as PhotoLocationApplication).repository)
    }

    private val takePictureResultLauncher = registerForActivityResult(
        ActivityResultContracts
            .StartActivityForResult()){
            result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_CANCELED){
            Log.d("MainActivity","Picture Intent Cancelled")
        }else{
            // get the description
            Log.d("MainActivity","Picture Successfully taken at $currentPhotoPath")
            // TODO: insert into database
            val newPhotoLocation = PhotoLocation(null, currentPhotoPath, currentLongitude, currentLatitude, currentTimeStamp, "description")
            newPhotoLocationViewModel.insert(newPhotoLocation)

            Log.d("MapsActivity","Inserted into database: $newPhotoLocation")

        }

    }

    //ViewModel object to communicate between Activity and repository
    private val photoLocationViewModel: PhotoLocationViewModel by viewModels {
        PhotoLocationViewModelFactory((application as PhotoLocationApplication).repository)
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            //If successful, startLocationRequests
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationPermissionEnabled = true
                startLocationRequests()
            }
            //If successful at coarse detail, we still want those
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                locationPermissionEnabled = true
                startLocationRequests()
            }

            else -> {
                //Otherwise, send toast saying location is not enabled
                locationPermissionEnabled = false
                Toast.makeText(this, "Location Not Enabled", Toast.LENGTH_LONG)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.map_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Configuration.getInstance().load(this, getSharedPreferences(
            "${packageName}_preferences", Context.MODE_PRIVATE))

        mapsFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                as OpenStreetMapFragment? ?:OpenStreetMapFragment.newInstance().also{
            replaceFragmentInActivity(it,R.id.fragmentContainerView)
        }
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkForLocationPermission()

        // camera stuff
        //Get reference to recyclerView object
        //        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        //        //Create adapter class, passing the launchNewWordActivity callback
        //        val adapter = PhotoLocationListAdapter(this::launchNewPhotoLocationActivity)
        //        //Set the adapter for the recyclerView to the adapter object
        //        recyclerView.adapter = adapter
        //        //Set the recyclerview layout to be a linearLayoutManager with activity context
        //        recyclerView.layoutManager = LinearLayoutManager(this)
        //        //Start observing the words list (now map), and pass updates through
        //        //to the adapter
        //        photoLocationViewModel.allPhotoLocations.observe(this, Observer { photoLocations ->
        //            // Update the cached copy of the words in the adapter.
        //            photoLocations?.let { adapter.submitList(it.values.toList()) }
        //        })

        // fabTakePicture listener
        findViewById<FloatingActionButton>(R.id.fabTakePicture).setOnClickListener {
            takeAPicture()
        }

    }

    private fun createFilePath(): Pair<String, String> {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        Log.d("MainActivity", "timeStamp: $timeStamp")
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intent
        return Pair(image.absolutePath, timeStamp)
    }

    private fun takeAPicture() {
        Log.d("MainActivity","Taking a picture")

        val pictureIntent: Intent = Intent().setAction(MediaStore.ACTION_IMAGE_CAPTURE)
        if(pictureIntent.resolveActivity(packageManager)!=null){
            val (filepath, timestamp) = createFilePath()
            val myFile: File = File(filepath)
            currentPhotoPath = filepath
            currentTimeStamp = timestamp
            val photoUri = FileProvider.getUriForFile(this,"edu.uark.ahnelson.roomwithaview2024.fileprovider",myFile)
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
            takePictureResultLauncher.launch(pictureIntent)
        }
    }

    private fun checkForLocationPermission(){
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startLocationRequests()
            }
            else -> {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
        }
    }

    //LocationUtilCallback object
    //Dynamically defining two results from locationUtils
    //Namely requestPermissions and locationUpdated
    private val locationUtilCallback = object : LocationUtilCallback {
        //If locationUtil request fails because of permission issues
        //Ask for permissions
        override fun requestPermissionCallback() {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        //If locationUtil returns a Location object
        //Populate the current location and log
        override fun locationUpdatedCallback(location: Location) {
            mCurrentLocation = location
            mapsFragment.changeCenterLocation(GeoPoint(location.latitude,location.longitude))
            mapsFragment.addMarker(GeoPoint(location.latitude,location.longitude), 7)
//            Log.d(
//                "MainActivity",
//                "Location is [Lat: ${location.latitude}, Long: ${location.longitude}]"
//            )
            currentLatitude = location.latitude
            currentLongitude = location.longitude
        }
    }

    private fun startLocationRequests() {
        //If we aren't currently getting location updates
        if (!locationRequestsEnabled) {
            //create a location callback
            mLocationCallback = createLocationCallback(locationUtilCallback)
            //and request location updates, setting the boolean equal to whether this was successful
            locationRequestsEnabled =
                createLocationRequest(this, locationProviderClient, mLocationCallback)
        }
    }
}