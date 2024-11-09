package edu.uark.ahnelson.roomwithaview2024.MainActivity

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocation
import kotlin.math.roundToInt

/**
WordListAdapter class
Implements a ListAdapter holding Words in WordViewHolders
Compares words with the WordsComparator
@param onItemClicked the callback function when an itemView is clicked
 */
class PhotoLocationListAdapter(
    val onItemClicked:(id:Int)->Unit)
    : ListAdapter<PhotoLocation, PhotoLocationListAdapter.PhotoLocationViewHolder>(WordsComparator()) {

    /**
     * onCreateViewHolder creates the viewHolder object
     * Implements WordViewHolder
     * @param parent the object that holds the ViewGroup
     * @param viewType the type of the view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoLocationViewHolder {
        return PhotoLocationViewHolder.create(parent)
    }

    /**
     * onBindViewHolder is called when a view object is bound to a view holder
     * @param holder the ViewHolder being created
     * @param position integer value for the position in the recyclerView
     */
    override fun onBindViewHolder(holder: PhotoLocationViewHolder, position: Int) {
        //Get the item in a position
        val current = getItem(position)
        //Set its onClickListener to the class callback parameter
        holder.itemView.setOnClickListener {
            current.id?.let {
                it1 -> onItemClicked(it1)
            }
            }
        //Bind the item to the holder
        holder.bind(current)
    }

    /**
     * WordViewHolder class implements a RecyclerView.ViewHolder object
     * Responsible for creating the layouts and binding objects to views
     * @param itemView the View object to be bound
     */
    class PhotoLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Reference to the textView object
//        private val wordItemView: TextView = itemView.findViewById(R.id.textView)
        private var locationImageView: ImageView = itemView.findViewById(R.id.location_image)
        private var textDateView: TextView = itemView.findViewById(R.id.text_date)

        /**
         * bind binds a word object's data to views
         */
        fun bind(photoLocation: PhotoLocation?) {
            if (photoLocation != null) {
                // setFormattedDate(photoLocation.photoDate)
                textDateView.text = photoLocation.photoDate
                setPic(photoLocation.photoPath)
            }
        }

        private fun setFormattedDate(timeStamp: String) {
            // timestamp is of the form "yyyyMMdd_HHmmss"
            Log.d("PhotoLocationListAdapter", "PhotoLocationListAdapater: timeStamp: $timeStamp")
            // convert to "MM-DD-YYYY
            if (timeStamp.length >= 8) {
                // timestamp is of the form "yyyyMMdd_HHmmss"
                // convert to "MM-DD-YYYY"
                val month = timeStamp.substring(4, 6)
                val day = timeStamp.substring(6, 8)
                val year = timeStamp.substring(0, 4)
                val formattedDate = "$month-$day-$year"
                textDateView.text = formattedDate
            } else {
                // Handle the case where timeStamp is empty or invalid
                Log.e("EditPhotoLocationActivity", "Invalid timeStamp: $timeStamp")
            }
        }

        private fun setPic(photoPath: String) {
            // Get the dimensions of the View
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

        /**
         * create the view object
         */
        companion object {
            fun create(parent: ViewGroup): PhotoLocationViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return PhotoLocationViewHolder(view)
            }
        }
    }

    /**
     * Comparators to determine whether to actually inflate new views
     */
    class WordsComparator : DiffUtil.ItemCallback<PhotoLocation>() {
        override fun areItemsTheSame(oldItem: PhotoLocation, newItem: PhotoLocation): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoLocation, newItem: PhotoLocation): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
