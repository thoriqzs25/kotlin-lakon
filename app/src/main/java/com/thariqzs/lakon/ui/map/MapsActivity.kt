package com.thariqzs.lakon.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.thariqzs.lakon.R
import com.thariqzs.lakon.data.model.Story
import com.thariqzs.lakon.databinding.ActivityMapsBinding
import com.thariqzs.lakon.factory.ViewModelFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapsViewModel = obtainViewModel(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val indonesia = LatLng(-6.200000, 106.816666)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(indonesia))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 5f))

        mapsViewModel.stories.observe(this) {
            assignMarkers(mMap,it)
        }
    }

    private fun obtainViewModel(activity: MapsActivity): MapsViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, null,this@MapsActivity)
        return ViewModelProvider(activity, factory)[MapsViewModel::class.java]
    }

    private fun assignMarkers(mMap: GoogleMap, stories: List<Story>) {
        stories.forEach { story ->
            val latLng = LatLng(story.lat.toDouble(), story.lon.toDouble())
            mMap.addMarker(MarkerOptions().position(latLng).title(story.name))
        }
    }

    companion object {
        val TAG = "mapthoriq"
    }
}