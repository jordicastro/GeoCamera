package edu.uark.ahnelson.roomwithaview2024.NewPhotoLocationActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocation
import edu.uark.ahnelson.roomwithaview2024.PhotoLocationApplication

class NewPhotoLocationActivity : AppCompatActivity() {

    private lateinit var editWordView: EditText
    private lateinit var photoLocation: PhotoLocation
    val newWordViewModel: NewWordViewModel by viewModels {
        NewWordViewModelFactory((application as PhotoLocationApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_word)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        editWordView = findViewById(R.id.edit_word)

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
            newWordViewModel.start(id)
            newWordViewModel.photoLocation.observe(this){
                if(it != null){
                    editWordView.setText(it.id.toString())
                }
            }
        }

        //Get reference to the button
        val button = findViewById<Button>(R.id.button_save)
        //Set the click listener functionality
        //If text is empty, return with nothing
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editWordView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                //If text isn't empty, determine whether to update
                //or insert
                val photoLocation = editWordView.text.toString()
                if(newWordViewModel.photoLocation.value?.id == null){
                    newWordViewModel.insert(PhotoLocation(null, photoLocation, 0.0, 0.0, "00-00-00", ""))
                }else{
                    newWordViewModel.photoLocation.value?.let { it1 -> newWordViewModel.update(it1) }
                }
                //replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK)
            }
            //End the activity
            finish()
        }

    }
}