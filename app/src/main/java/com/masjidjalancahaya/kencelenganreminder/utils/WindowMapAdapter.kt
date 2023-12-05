package com.masjidjalancahaya.kencelenganreminder.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.masjidjalancahaya.kencelenganreminder.R

class WindowMapAdapter(context: Context) : GoogleMap.InfoWindowAdapter {
    @SuppressLint("InflateParams")
    private val mWindow: View = LayoutInflater.from(context).inflate(R.layout.map_window, null)

    private fun renderWindowText(marker: Marker, view: View) {
        val title = view.findViewById<TextView>(R.id.tvInfoWindowTitle)
        val snippet = view.findViewById<TextView>(R.id.tvInfoWindowSnippet)

        title.text = marker.title
        snippet.text = marker.snippet
    }

    override fun getInfoWindow(marker: Marker): View {
        renderWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoContents(marker: Marker): View {
        renderWindowText(marker, mWindow)
        return mWindow
    }
}