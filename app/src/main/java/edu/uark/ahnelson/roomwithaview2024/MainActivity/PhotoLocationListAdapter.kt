package edu.uark.ahnelson.roomwithaview2024.MainActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.Repository.PhotoLocation

/**
WordListAdapter class
Implements a ListAdapter holding Words in WordViewHolders
Compares words with the WordsComparator
@param onItemClicked the callback function when an itemView is clicked
 */
class PhotoLocationListAdapter(
    val onItemClicked:(id:Int)->Unit)
    : ListAdapter<PhotoLocation, PhotoLocationListAdapter.WordViewHolder>(WordsComparator()) {

    /**
     * onCreateViewHolder creates the viewHolder object
     * Implements WordViewHolder
     * @param parent the object that holds the ViewGroup
     * @param viewType the type of the view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder.create(parent)
    }

    /**
     * onBindViewHolder is called when a view object is bound to a view holder
     * @param holder the ViewHolder being created
     * @param position integer value for the position in the recyclerView
     */
    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        //Get the item in a position
        val current = getItem(position)
        //Set its onClickListener to the class callback parameter
        holder.itemView.setOnClickListener {
            current.id?.let { it1 -> onItemClicked(it1) }
            }
        //Bind the item to the holder
        holder.bind(current)
    }

    /**
     * WordViewHolder class implements a RecyclerView.ViewHolder object
     * Responsible for creating the layouts and binding objects to views
     * @param itemView the View object to be bound
     */
    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Reference to the textView object
        private val wordItemView: TextView = itemView.findViewById(R.id.textView)

        /**
         * bind binds a word object's data to views
         */
        fun bind(photoLocation: PhotoLocation?) {
            if (photoLocation != null) {
                wordItemView.text = photoLocation.id.toString()
            }
        }

        /**
         * create the view object
         */
        companion object {
            fun create(parent: ViewGroup): WordViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return WordViewHolder(view)
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
