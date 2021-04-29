package com.example.myapplication.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    companion object {
        private const val LOCATION_PERMISSION = 42
    }

    private lateinit var mGoogleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val first = LatLng(-34.00, 151.00)
    private val second = LatLng(-31.083332, 150.916672)
    private val third = LatLng(-32.916668, 151.750000)
    private val forth = LatLng(-27.470125, 153.021072)
    private var locationList: ArrayList<LatLng> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment.getMapAsync(this)
        locationList.add(first)
        locationList.add(second)
        locationList.add(third)
        locationList.add(forth)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mGoogleMap.isMyLocationEnabled = true
            initMap()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION
            )
        }
        mGoogleMap.setOnMapClickListener {
            mGoogleMap.clear()
        }
    }

    /**
     * Method to initialize map, map listeners and add arrays of markers
     */
    private fun initMap() {
        mGoogleMap.setOnMyLocationButtonClickListener(this)
        mGoogleMap.setOnMyLocationClickListener(this)
        initLocationTracking()
        for (i in 0 until locationList.size) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(locationList[i]))
            mGoogleMap.addMarker(
                MarkerOptions().position(locationList[i])
                    .title("Marker")
                    .icon(bitmapFromVector())
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun initLocationTracking() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    updateMapLocation(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            LocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onResume() {
        super.onResume()
        if (::mGoogleMap.isInitialized) {
            initLocationTracking()
        }
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * Method to update current location and add marker
     *
     * @param location location
     */
    private fun updateMapLocation(location: Location) {
        val currentLocation = LatLng(location.latitude, location.longitude)
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
        mGoogleMap.addMarker(
            currentLocation.let {
                MarkerOptions()
                    .position(it)
                    .title("Current Location Marker")
            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION) {
            if (permissions.size == 1 &&
                permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                initMap()
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Log.d("debug", "Current Location ::" + p0.latitude + "  " + p0.longitude)
    }

    /**
     * Method to convert bitmap image from vector drawable
     */
    private fun bitmapFromVector(): BitmapDescriptor {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(this, R.drawable.ic_map_marker)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}