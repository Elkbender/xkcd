package no.elkbender.xkcd.fragments.comic

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.beust.klaxon.Klaxon
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_comic.*
import kotlinx.android.synthetic.main.nav_button_bar.*
import no.elkbender.xkcd.db.Comic
import no.elkbender.xkcd.activities.MainActivity
import no.elkbender.xkcd.activities.MainActivity.Companion.fetchComic
import no.elkbender.xkcd.R
import no.elkbender.xkcd.activities.MainActivity.Companion.MOST_RECENT
import no.elkbender.xkcd.activities.MainActivity.Companion.SHARED_PREFERENCES
import no.elkbender.xkcd.db.ComicsDb
import no.elkbender.xkcd.extensions.showSnack
import no.elkbender.xkcd.utility.GestureListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.net.URL

class ComicFragment : Fragment(), View.OnTouchListener {
    private val client = OkHttpClient()
    private lateinit var comic: Comic
    private lateinit var image: Bitmap
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gestureDetector = GestureDetector(requireContext(), object : GestureListener() {
            override fun onSwipe(direction: Direction): Boolean {
                when(direction) {
                    Direction.Left -> next.callOnClick()
                    Direction.Right -> prev.callOnClick()
                    else -> print("Ignore these")
                }
                return true
            }

            override fun onLongPress(e: MotionEvent?) {
                super.onLongPress(e)
                requireActivity().runOnUiThread { showAltText() }
            }
        })
        initComic()
    }

    private fun initComic() {
        arguments?.let { bundle ->
            bundle.getParcelable<Comic>(COMIC_ARG)?.let {
                comic = it
            }
            bundle.getByteArray(IMG_ARG)?.let {
                image = convertToBitmap(it)
            }
        }
    }

    private fun convertToBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_comic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initListeners()
        showComic()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        v?.performClick()
        return true
    }

    private fun initListeners() {
        requireActivity().runOnUiThread { addFabListener() }

        first.setOnClickListener { getComic(MainActivity.FIRST_COMIC) }
        prev.setOnClickListener { getComic(MainActivity.previous(comic)) }
        random.setOnClickListener {
            getComic(
                MainActivity.random(
                    requireActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE).getInt(
                        MOST_RECENT, 1
                    )
                )
            )
        }
        next.setOnClickListener { getComic(MainActivity.next(comic)) }
        last.setOnClickListener { getComic(MainActivity.CURRENT_COMIC) }

        comic_img.setOnTouchListener(this)
    }

    fun shareComic() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, comic.safe_title)
        share.putExtra(Intent.EXTRA_TEXT, comic.img)

        startActivity(Intent.createChooser(share, "Share comic"))
    }

    private fun addFabListener() {
        val activity = (requireActivity() as MainActivity)
        val dao = ComicsDb.getInstance(requireContext()).comicsDao()

        activity.fab.show()
        activity.fab.setOnClickListener {
            if (isFavourite()) dao.delete(comic)
            else dao.insertAll(comic)
            setFabIcon()
        }
    }

    private fun showAltText() {
        AlertDialog.Builder(requireContext())
            .setTitle(comic.year + "-" + comic.month + " - " + comic.day) // According to https://xkcd.com/1179
            .setMessage(comic.alt)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
            .getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
    }

    private fun getComic(url: String) {
        toggleProgressBar(View.VISIBLE)
        fetchComic(client, url).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                when (response.code()) {
                    200 -> response.body()?.let { responseBody ->
                        val result = Klaxon().parse<Comic>(responseBody.string())
                        result?.let {
                            comic = it
                            image = BitmapFactory.decodeStream(URL(it.img).openStream())
                        }
                    }

                    404 -> (activity as MainActivity).showSnack("Comic not found", Snackbar.LENGTH_SHORT)
                }
                showComic()
            }
        })
    }

    private fun showComic() {
        toggleProgressBar(View.GONE)
        setFabIcon()

        requireActivity().runOnUiThread {
            requireActivity().title = comic.safe_title
            comic_img.setImageBitmap(image)
            padding.visibility = if(isScrollable()) View.VISIBLE else View.GONE
        }
    }

    private fun isScrollable() = comic_view.height < comic_img.height + comic_view.paddingTop + comic_view.paddingBottom

    private fun setFabIcon() {
        val activity = (activity as MainActivity)
        activity.fab.setImageResource((if (isFavourite()) R.drawable.fab_unfavourite else R.drawable.fab_favourite))
    }

    private fun isFavourite() = comic in ComicsDb.getInstance(requireContext()).comicsDao().getAll()

    private fun toggleProgressBar(state: Int) = requireActivity().runOnUiThread {
        progressbar.visibility = state
    }

    companion object {
        private const val COMIC_ARG = "COMIC_ARG"
        private const val IMG_ARG = "IMG_ARG"

        fun newInstance(comic: Comic, img: ByteArray): ComicFragment {
            val fragment = ComicFragment()
            val args = Bundle()
            args.putParcelable(COMIC_ARG, comic)
            args.putByteArray(IMG_ARG, img)
            fragment.arguments = args
            return fragment
        }
    }
}