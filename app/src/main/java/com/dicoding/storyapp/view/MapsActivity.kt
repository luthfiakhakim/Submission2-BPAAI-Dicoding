package com.dicoding.storyapp.view

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.dicoding.storyapp.viewmodel.StoryViewModel
import com.dicoding.storyapp.viewmodel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: ActivityMapsBinding
    private lateinit var storyViewModel: StoryViewModel

    private val callback = OnMapReadyCallback { googleMap ->
        lifecycleScope.launch {
            storyViewModel.getUser().observe(this@MapsActivity) {
                storyViewModel.showLocationStory(it.token)
            }

            storyViewModel.itemStory.observe(this@MapsActivity) { list ->
                list.forEach {
                    val location = LatLng(it.lat, it.lon)
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    try {
                        val success =
                            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(applicationContext, R.raw.map_style))
                        if (!success) {
                            Log.e(TAG, "Failed parsing MapStyle.")
                        }
                    } catch (exception: Resources.NotFoundException) {
                        Log.e(TAG, "MapStyle not found: ", exception)
                    }
                    googleMap.addMarker(MarkerOptions().position(location).title(it.name).snippet(it.description))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(location))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(dataStore)

        storyViewModel = ViewModelProvider(this, ViewModelFactory(pref, this))[StoryViewModel::class.java]

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(callback)
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}