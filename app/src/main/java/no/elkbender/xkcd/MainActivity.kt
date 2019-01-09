package no.elkbender.xkcd

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    lateinit var db: ComicsDb

    private lateinit var menu: Menu
    private lateinit var comicFragment: ComicFragment

    private val favsFragment = FavouritesFragment()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_comic -> {
                menu.findItem(R.id.share).isVisible = true
                replaceFragment(
                    comicFragment,
                    R.id.fragment_container,
                    ComicFragment::class.java.canonicalName
                )
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favourites -> {
                fab.hide()
                menu.findItem(R.id.share).isVisible = false
                replaceFragment(
                    favsFragment,
                    R.id.fragment_container,
                    FavouritesFragment::class.java.canonicalName
                )
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = ComicsDb.buildDatabase(this)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_options, menu)

        replaceComicFragment(
            ComicFragment.newInstance(
                intent.getParcelableExtra(COMIC_ARG),
                intent.getByteArrayExtra(IMAGE_ARG)
            )
        )

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.search -> {
                showSnack("Not implemented", Snackbar.LENGTH_LONG)
                return true
            }
            R.id.share -> {
                comicFragment.shareComic()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun replaceComicFragment(fragment: ComicFragment) {
        comicFragment = fragment
        runOnUiThread {
            navigation.selectedItemId = R.id.navigation_comic
        }
    }

    companion object {
        private const val COMIC_ARG = "COMIC_ARG"
        private const val IMAGE_ARG = "IMAGE_ARG"

        const val CURRENT_COMIC = "https://xkcd.com/info.0.json"
        const val FIRST_COMIC = "https://xkcd.com/1/info.0.json"

        fun previous(c: Comic) = "https://xkcd.com/${c.num.minus(1)}/info.0.json"
        fun next(c: Comic) = "https://xkcd.com/${c.num.plus(1)}/info.0.json"
        fun random() = "https://xkcd.com/221/info.0.json" // TODO: Make actual random rather than joke

        fun fetchComic(client: OkHttpClient, url: String): Call {
            val request = Request.Builder()
                .url(url)
                .build()

            return client.newCall(request)
        }

        fun newIntent(context: Context, comic: Comic, img: Bitmap): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(COMIC_ARG, comic)
                putExtra(
                    IMAGE_ARG,
                    convertToByteArray(img)
                )
            }
        }

        fun convertToByteArray(img: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            img.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }
    }
}
