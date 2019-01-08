package no.elkbender.xkcd

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
