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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.databinding.ActivityMapsBinding
import com.example.myapplication.model.response.Vehicle
import com.example.myapplication.preferences.PreferenceManager
import com.example.myapplication.retrofit.RetrofitClient
import com.example.myapplication.ui.base.ViewModelFactory
import com.example.myapplication.util.Status
import com.example.myapplication.viewmodel.VehicleViewModel
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

    private lateinit var binding: ActivityMapsBinding

    companion object {
        private const val LOCATION_PERMISSION = 42
    }

    private lateinit var mGoogleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mVehicleViewModel: VehicleViewModel
    private var mLocationList: ArrayList<LatLng> = arrayListOf()
    private var mVehiclesList: ArrayList<Vehicle.VehicleItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initView()
    }

    /**
     * Method to initialize UI views and registers listeners
     */
    private fun initView() {
        mVehicleViewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(RetrofitClient.apiService)
            )[VehicleViewModel::class.java]
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment.getMapAsync(this)
        getVehicles()
    }


    /**
     * Method to observe vehicles details
     */
    private fun getVehicles() {
        val authToken = PreferenceManager.getInstance(this).getAuthToken()
        if (authToken != null) {
            mVehicleViewModel.getVehiclesDetails(authToken).observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            if (it.data!!.isSuccessful) {
                                mVehiclesList = it.data.body()!!
                                for (items in it.data.body()!!) {
                                    mLocationList.add(LatLng(items.lat, items.lng))
                                }
                                for (i in 0 until mLocationList.size) {
                                    mGoogleMap.moveCamera(
                                        CameraUpdateFactory.newLatLng(mLocationList[i])
                                    )
                                    mGoogleMap.addMarker(
                                        MarkerOptions().position(mLocationList[i])
                                            .title(mVehiclesList[i].vehicleMake)
                                            .icon(bitmapFromVector())
                                    )
                                }
                            }
                        }

                        Status.ERROR -> {
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }

                        Status.LOADING -> {
                            Log.d("TAG", "Loading ::::")
                        }
                    }
                }
            }
        }
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
    }

    @SuppressLint("MissingPermission")
    private fun initLocationTracking() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                //locationResult
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