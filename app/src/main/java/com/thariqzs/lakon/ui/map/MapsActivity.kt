package com.thariqzs.lakon.ui.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.thariqzs.lakon.ui.main.MainActivity
import com.thariqzs.lakon.ui.main.MainViewModel

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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d(TAG, "onMapReady: test")

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