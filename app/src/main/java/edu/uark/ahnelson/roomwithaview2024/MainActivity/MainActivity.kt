package edu.uark.ahnelson.roomwithaview2024.MainActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.uark.ahnelson.roomwithaview2024.NewPhotoLocationActivity.EditPhotoLocationActivity
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.PhotoLocationApplication
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocation

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var currentPhotoPath = ""
    private var currentTimeStamp = ""
    private val newPhotoLocationViewModel : PhotoLocationViewModel by viewModels {
        PhotoLocationViewModelFactory((application as PhotoLocationApplication).repository)
    }

    val takePictureResultLauncher = registerForActivityResult(
        ActivityResultContracts
            .StartActivityForResult()){
            result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_CANCELED){
            Log.d("MainActivity","Picture Intent Cancelled")
        }else{
            // setPic()
            Log.d("MainActivity","Picture Successfully taken at $currentPhotoPath")
            // TODO: insert into database
            val newPhotoLocation = PhotoLocation(null, currentPhotoPath, 0.00, 0.00, currentTimeStamp, "description", -1)
            newPhotoLocationViewModel.insert(newPhotoLocation)

            Log.d("MainActivity","Inserted into database: $newPhotoLocation")

        }

    }

    //ViewModel object to communicate between Activity and repository
    private val photoLocationViewModel: PhotoLocationViewModel by viewModels {
        PhotoLocationViewModelFactory((application as PhotoLocationApplication).repository)
    }
    /**
    Callback function passed through to RecyclerViewItems to launch
    A new activity based on id
    @param id id of the item that is clicked
     */
    fun launchNewPhotoLocationActivity(id:Int){
        val secondActivityIntent = Intent(this, EditPhotoLocationActivity::class.java)
        secondActivityIntent.putExtra("EXTRA_ID",id)
        this.startActivity(secondActivityIntent)
    }


    /**
    onCreate callback, handle setting up the application
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Get reference to recyclerView object
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        //Create adapter class, passing the launchNewWordActivity callback
        val adapter = PhotoLocationListAdapter(this::launchNewPhotoLocationActivity)
        //Set the adapter for the recyclerView to the adapter object
        recyclerView.adapter = adapter
        //Set the recyclerview layout to be a linearLayoutManager with activity context
        recyclerView.layoutManager = LinearLayoutManager(this)
        //Start observing the words list (now map), and pass updates through
        //to the adapter
        photoLocationViewModel.allPhotoLocations.observe(this, Observer { photoLocations ->
            // Update the cached copy of the words in the adapter.
            photoLocations?.let { adapter.submitList(it.values.toList()) }
        })

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
}