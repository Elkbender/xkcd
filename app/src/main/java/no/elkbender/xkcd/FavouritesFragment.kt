package no.elkbender.xkcd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_favourites.*


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
    }
}