package no.elkbender.xkcd


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.Klaxon
import no.elkbender.xkcd.Comic
import no.elkbender.xkcd.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.net.URL

class SplashActivity : AppCompatActivity()  {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initComic()
    }

    private fun initComic() {
        MainActivity.fetchComic(
            client,
            MainActivity.CURRENT_COMIC
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                if (response.code() == 200) {
                    response.body()?.let { responseBody ->
                        val result = Klaxon().parse<Comic>(responseBody.string())
                        result?.let {
                            startMain(it, BitmapFactory.decodeStream(URL(it.img).openStream()))
                        }
                    }
                }
            }
        })
    }

    private fun startMain(comic: Comic, img: Bitmap) {
        startActivity(MainActivity.newIntent(this, comic, img))
        finish()
    }
}