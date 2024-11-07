package edu.uark.ahnelson.roomwithaview2024.NewPhotoLocationActivity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Contacts.Photo
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocation
import edu.uark.ahnelson.roomwithaview2024.PhotoLocationApplication
import java.io.IOException
import java.util.Locale
import kotlin.math.roundToInt

class EditPhotoLocationActivity : AppCompatActivity() {

    // variables for xml elements
    private lateinit var locationImageView: ImageView
    private lateinit var textLocationView: TextView
    private lateinit var textDateView: TextView
    private lateinit var editDescriptionText: EditText
    private lateinit var photoLocation: PhotoLocation

    // refactoring newphotolocationActivity to have the photo path, time stamp, long, lat,
    // GET the description, and insert the photoLocation object into the database

    private lateinit var photoPath: String
    private lateinit var timeStamp: String
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    val newPhotoLocationViewModel: NewPhotoLocationViewModel by viewModels {
        NewPhotoLocationViewModelFactory((application as PhotoLocationApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_photo_location)

        // Get the photo details from the intent
        photoPath = intent.getStringExtra("PHOTO_PATH") ?: ""
        timeStamp = intent.getStringExtra("TIME_STAMP") ?: ""
        longitude = intent.getDoubleExtra("LONGITUDE", 0.0)
        latitude = intent.getDoubleExtra("LATITUDE", 0.0)


        locationImageView = findViewById(R.id.location_image)
        textLocationView = findViewById(R.id.text_location)
        textDateView = findViewById(R.id.text_date)
        editDescriptionText = findViewById(R.id.edit_description)

        // set text of location and date text views
        Log.d("EditPhotoLocationActivity", "photoPath $photoPath, timeStamp $timeStamp, longitude $longitude, latitude $latitude")

        setHumanReadableLocation(longitude, latitude)
        setFormattedDate(timeStamp)

        // set pic
         setPic()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Logic block to determine whether we are updating an exiting word
        //Or creating a new word
        //Get intent that created the activity id value, if exists
        val id = intent.getIntExtra("EXTRA_ID",-1)
        //If it doesn't exist, create a new Word object
        if(id == -1){
            photoLocation = PhotoLocation(null,"", 0.0, 0.0, "00-00-00", "")
        }else{
            //Otherwise, start the viewModel with the id
            //And begin observing the word to set the text in the
            //text view
            newPhotoLocationViewModel.start(id)
            newPhotoLocationViewModel.photoLocation.observe(this){
                if(it != null){
                    editDescriptionText.setText(it.id.toString())
                }
            }
        }

        //Get reference to the button
        val button = findViewById<Button>(R.id.button_save)
        //Set the click listener functionality
        //If text is empty, return with nothing
        button.setOnClickListener {
            Log.d("EditPhotoLocationActivity", "button clicked")
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editDescriptionText.text)) {
                Log.d("EditPhotoLocationActivity", "text is empty")
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                Log.d("EditPhotoLocationActivity", "text is not empty")
                //If text isn't empty, determine whether to update
                //or insert
                val photoDescription = editDescriptionText.text.toString()
                if(newPhotoLocationViewModel.photoLocation.value?.id == null){
                    Log.d("EditPhotoLocationActivity", "inserting new photoLocation")
                    newPhotoLocationViewModel.insert(PhotoLocation(null, photoPath, longitude, latitude, timeStamp, editDescriptionText.text.toString()))
                    Log.d("EditPhotoLocationActivity", "successfully inserted new photoLocation photoPath $photoPath, timeStamp $timeStamp, longitude $longitude, latitude $latitude description $photoDescription")
                }else{ // update the description ONLY
                    Log.d("EditPhotoLocationActivity", "updating photoLocation")
                    newPhotoLocationViewModel.photoLocation.value?.let { it1: PhotoLocation -> it1.photoDescription = photoDescription }
                    Log.d("EditPhotoLocationActivity", "successfully updated photoLocation")
                }
                Log.d("EditPhotoLocationActivity", "replyIntent")
                //replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK)
            }
            //End the activity
            finish()
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setHumanReadableLocation(latitudeX: Double, longitudeY: Double) {
        Log.d("EditPhotoLocationActivity", "setHumanReadableLocation latitude $latitudeX, longitude $longitudeY")
        val notFound = "Location not found"
        var addressText = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitudeX, longitudeY, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    val address = addresses[0]
                    addressText = address.getAddressLine(0)
                    textLocationView.text = addressText


                }
                override fun onError(errorMessage: String?) {
                    Log.e("EditPhotoLocationActivity", "Geocoder failed")

                }
            })

        } catch (e: IOException) {
            Log.e("EditPhotoLocationActivity", "Geocoder failed", e)
            textLocationView.text = notFound
        }
    }

    private fun setFormattedDate(timeStamp: String) {
        // timestamp is of the form "yyyyMMdd_HHmmss"
        // convert to "MM-DD-YYYY
        val month = timeStamp.substring(4, 6)
        val day = timeStamp.substring(6, 8)
        val year = timeStamp.substring(0, 4)
        val formattedDate = "$month-$day-$year"
        textDateView.text = formattedDate
    }

    private fun setPic() {
//        val targetW: Int = locationImageView.width
        val targetW = 125

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(photoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight
        val photoRatio:Double = (photoH.toDouble())/(photoW.toDouble())
        val targetH: Int = (targetW * photoRatio).roundToInt()
        // Determine how much to scale down the image
        val scaleFactor = 1.coerceAtLeast((photoW / targetW).coerceAtMost(photoH / targetH))


        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        val bitmap = BitmapFactory.decodeFile(photoPath, bmOptions)
        locationImageView.setImageBitmap(bitmap)
    }
}