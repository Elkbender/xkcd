package no.elkbender.xkcd.fragments.favourites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import no.elkbender.xkcd.db.Comic
import no.elkbender.xkcd.R

class FavouritesAdapter(private val favourites: List<Comic>, private val clickListener: (Comic) -> Unit) : RecyclerView.Adapter<FavouritesAdapter.FavViewHolder>() {
    inner class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val num = itemView.findViewById<TextView>(R.id.number)
        private val title = itemView.findViewById<TextView>(R.id.title)
        private val date = itemView.findViewById<TextView>(R.id.date)

        fun bind(comic: Comic, clickListener: (Comic) -> Unit) {
            val comicNum = "#" + comic.num.toString()
            val comicDate = comic.month + "/" + comic.day + " - " + comic.year

            num.text = comicNum
            title.text = comic.safe_title
            date.text = comicDate
            itemView.setOnClickListener { clickListener(comic) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favourite, parent, false) as View

        return FavViewHolder(view)
    }

    override fun getItemCount() = favourites.size

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        holder.bind(favourites[position], clickListener)
    }
}