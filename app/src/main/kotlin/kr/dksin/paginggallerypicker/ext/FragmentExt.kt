package kr.dksin.paginggallerypicker.ext

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

fun Fragment.setSupportActionBar(toolbar: Toolbar?) {
    activity?.let {
        (it as? AppCompatActivity)?.setSupportActionBar(toolbar)
    }
}

fun Fragment.getSupportActionBar(): ActionBar? {
    activity?.let {
        return (it as? AppCompatActivity)?.supportActionBar
    }
    return null
}