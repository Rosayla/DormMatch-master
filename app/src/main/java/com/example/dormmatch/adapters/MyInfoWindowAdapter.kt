package com.example.dormmatch.adapters

import android.view.View
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.example.dormmatch.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class MyInfoWindowAdapter(private val context: Context): InfoWindowAdapter {

    override fun getInfoContents(p0: Marker): View? {
        TODO("Not yet implemented")
    }

    override fun getInfoWindow(p0: Marker): View {
        val infoView: View = LayoutInflater.from(context).inflate(R.layout.custom_info, null)

        val title: TextView = infoView.findViewById(R.id.title_info)
        val desc: TextView = infoView.findViewById(R.id.description_info)
        val imageView: ImageView = infoView.findViewById(R.id.imagemA_info)

        val delim = "&_:_&"
        val arraySnippet = p0.snippet.toString().split(delim).toTypedArray()
        title.setText(arraySnippet[0])
        desc.setText(arraySnippet[1])
        Picasso.get().load(p0.title).into(imageView)

        return infoView

    }
}