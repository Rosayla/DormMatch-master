package com.example.dormmatch.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dormmatch.R
import com.example.dormmatch.adapters.MyInfoWindowAdapter
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Maps.newInstance] factory method to
 * create an instance of this fragment.
 */
class Maps : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var nMap: GoogleMap
    private lateinit var localizacao: ArrayList<String>
    private lateinit var descricao: ArrayList<String>
    private lateinit var imagem: ArrayList<String>
    private lateinit var titulo: ArrayList<String>
    private lateinit var idPropriedade: ArrayList<String>

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private var lastLocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        loadStreets()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localizacao = arrayListOf()
        descricao = arrayListOf()
        imagem = arrayListOf()
        titulo = arrayListOf()
        idPropriedade = arrayListOf()

        // Initialize the FusedLocationProviderClient and LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Map.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Maps().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        private const val PERMISSIONS_REQUEST_LOCATION = 100
        private const val DEFAULT_ZOOM = 17f
    }

    override fun onMapReady(googleMap: GoogleMap) {
        nMap = googleMap

        nMap.setInfoWindowAdapter(MyInfoWindowAdapter(requireContext()))

        for (i in 0 until idPropriedade.size) {
            nMap.addMarker(
                MarkerOptions().position(getCoord(localizacao[i])).title(imagem[i])
                    .snippet(titulo[i] + "&_:_&" + descricao[i])
            )
        }

        // Check if GoogleMap is null or not
        nMap.let {
            nMap = it

            // Check if the app has location permission
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
                // Get the current location and add a marker
                getCurrentLocation()
            } else {
                // Request location permission if it is not granted
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    private fun getCoord(address: String): LatLng {
        val geocoder = Geocoder(requireContext())
        val list = geocoder.getFromLocationName(address, 1)
        val lat = list?.get(0)?.latitude
        val lng = list?.get(0)?.longitude

        return LatLng(lat!!, lng!!)
    }

    private fun loadStreets() {
        val ref = FirebaseDatabase.getInstance().getReference("propriedade")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val loc = "${snap.child("localizacao").value}"
                        val idP = "${snap.child("idPropriedade").value}"
                        val desc = "${snap.child("descricao").value}"
                        val title = "${snap.child("titulo").value}"
                        val Image = "${snap.child("imagemCapa").value}"

                        localizacao.add(loc)
                        descricao.add(desc)
                        titulo.add(title)
                        idPropriedade.add(idP)
                        imagem.add(Image)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                nMap.isMyLocationEnabled = true
                // Get the current location
                getCurrentLocation()
            } else {
                // Permission is not granted, display a message and redirect the user to app settings
                locationPermissionDenied()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getCurrentLocation()
    }

    override fun onPause() {
        super.onPause()
        nMap.clear()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        // Check if location services are enabled on the device
        if (isLocationEnabled()) {
            // Get the current location of the user
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        lastLocation = it

                        nMap.setInfoWindowAdapter(MyInfoWindowAdapter(requireContext()))


                        for (i in 0 until idPropriedade.size) {
                            nMap.addMarker(
                                MarkerOptions().position(getCoord(localizacao[i])).title(imagem[i])
                                    .snippet(titulo[i] + "&_:_&" + descricao[i])
                            )
                        }

                        // Add a marker to the current location with a different icon
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))
                    }
                }
        } else {
            // Location services are not enabled, display a message and redirect the user to location settings
            locationServicesDisabled()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun isLocationEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            val mode: Int = try {
                Settings.Secure.getInt(
                    requireActivity().contentResolver,
                    Settings.Secure.LOCATION_MODE
                )
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }

    private fun locationServicesDisabled() {
        // Display a message and redirect the user to location settings
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun locationPermissionDenied() {
        // Display a message and redirect the user to app settings
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

}
