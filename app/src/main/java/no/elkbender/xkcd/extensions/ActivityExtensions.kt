package no.elkbender.xkcd.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import no.elkbender.xkcd.R

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, tag: String? = null) {
    supportFragmentManager.inTransaction {
        replace(frameId, fragment)
        tag?.let { addToBackStack(fragment.javaClass.canonicalName) }
    }
}

fun AppCompatActivity.showSnack(text: String, length: Int) {
    Snackbar.make(
        findViewById(R.id.fragment_container),
        text,
        length
    ).show()
}