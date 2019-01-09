package no.elkbender.xkcd.fragments.favourites

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_favourites.*
import no.elkbender.xkcd.db.Comic
import no.elkbender.xkcd.db.ComicsDb
import no.elkbender.xkcd.activities.MainActivity
import no.elkbender.xkcd.R
import no.elkbender.xkcd.fragments.comic.ComicFragment
import java.net.URL


class FavouritesFragment : Fragment() {
    private lateinit var db: ComicsDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = (activity as MainActivity).db
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = resources.getString(R.string.title_favourites)
        no_favs.visibility = if (db.comicsDao().getAll().isEmpty()) View.VISIBLE else View.GONE

        val manager = LinearLayoutManager(requireContext())
        val viewAdapter =
            FavouritesAdapter(db.comicsDao().getAll()) { item: Comic ->
                favouriteItemClicked(item)
            }

        requireActivity().findViewById<RecyclerView>(R.id.fav_list).apply {
            setHasFixedSize(true)
            layoutManager = manager
            adapter = viewAdapter
        }
    }

    private fun favouriteItemClicked(item: Comic) {
        val activity = (activity as MainActivity)

        Thread {
            activity.replaceComicFragment(
                ComicFragment.newInstance(
                    item, MainActivity.convertToByteArray(
                        BitmapFactory.decodeStream(
                            URL(item.img).openStream()
                        )
                    )
                )
            )
        }.start()
    }
}