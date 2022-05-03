package com.oceanbrasil.ocean_android_gps_03_05_2022

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.oceanbrasil.ocean_android_gps_03_05_2022.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Add a marker at Ocean SP and move the camera
        val oceanSp = LatLng(-23.556730534318714, -46.733184596313315)
        mMap.addMarker(MarkerOptions().position(oceanSp).title("Ocean SP - USP"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(oceanSp, 18.5f))

        mMap.addCircle(
            CircleOptions()
                .center(oceanSp)
                .radius(40.0)
                .strokeWidth(0f)
                .fillColor(Color.parseColor("#537CDBE7"))
        )

        iniciarLocalizacao()
    }

    private fun iniciarLocalizacao() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationProvider = LocationManager.NETWORK_PROVIDER

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )

            // Encerra a função iniciarLocalizacao()
            return
        }

        // Última localização
        val ultimaLocalizacao = locationManager.getLastKnownLocation(locationProvider)

        Toast.makeText(this, ultimaLocalizacao.toString(), Toast.LENGTH_LONG).show()

        ultimaLocalizacao?.let {
            val latLng = LatLng(it.latitude, it.longitude)

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.5f))
        }

        // Pegar informações do GPS em tempo real
        locationManager.requestLocationUpdates(
            locationProvider,
            1000,
            1F
        ) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()

            val latLng = LatLng(it.latitude, it.longitude)

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.5f))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1
            && (grantResults[0] == PackageManager.PERMISSION_GRANTED
            || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            iniciarLocalizacao()
        }
    }
}