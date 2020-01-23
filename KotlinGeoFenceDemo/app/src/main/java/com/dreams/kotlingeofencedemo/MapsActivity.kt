package com.dreams.kotlingeofencedemo

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dreams.kotlingeofencedemo.services.GeofenceTransitionService
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, OnSuccessListener<Void>,
    OnFailureListener {

    private val TAG = "MapsActivity"
    private val REQUEST_PERMISSIONS_ID_CODE = 123
    private val GEOFENCE_REQ_CODE = 10
    private var mapFragment: SupportMapFragment? = null

    private lateinit var map: GoogleMap
    private var geofencingClient: GeofencingClient? = null
    private val geofencePendingIntent: PendingIntent? = null
    private var geoFenceMarker: Marker? = null

    private var locationManager: LocationManager? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lastLocation: Location? = null
    private var locationMarker: Marker? = null
    private var initFindUser: Boolean = true


    // Draw Geofence circle on GoogleMap
    private var geoFenceLimits: Circle? = null

    private val KEY_GEOFENCE_LAT = "GEOFENCE LATITUDE"
    private val KEY_GEOFENCE_LON = "GEOFENCE LONGITUDE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        createGoogleApi()

        // Get a Ref to Location Manager.
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check for and request required permissions.
        if(setPermissions()){
            // Start Requesting Updates.
            getLastKnownLocation()
        }

        // Obtain the SupportMapFragment
        initGmaps()
    }

    // PERMISSION MANAGEMENT
    private fun setPermissions(): Boolean {
        val permissionsNeededList = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getPermissionsNeededListPost0(permissionsNeededList)
        } else {
            getPermissionsNeededListPre0(permissionsNeededList)
        }

        if (permissionsNeededList.isNotEmpty()) {
            makeRequest(permissionsNeededList)
            return false
        }
        return true
    }

    // Workaround for Permission Bug: https://github.com/permissions-dispatcher/PermissionsDispatcher/issues/646
    private fun getPermissionsNeededListPre0(permissionsNeededList: MutableList<String>) {
        val permissionFineLocation =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Fine Location Permission Denied")
            permissionsNeededList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }

    // Workaround for Permission Bug: https://github.com/permissions-dispatcher/PermissionsDispatcher/issues/646
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun getPermissionsNeededListPost0(permissionsNeededList: MutableList<String>) {
        val permissionFineLocation =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Fine Location Permission Denied")
            permissionsNeededList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val permissionBackgroundLocation =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

        if (permissionBackgroundLocation != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Access Background Permission Denied")
            permissionsNeededList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun makeRequest(permissionsNeededList: MutableList<String>) {

        ActivityCompat.requestPermissions(
            this,
            permissionsNeededList.toTypedArray(),
            REQUEST_PERMISSIONS_ID_CODE
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_ID_CODE -> {
                Log.d(TAG, "onRequestPermissionsResult")

                val perms: HashMap<String, Int> = HashMap<String, Int>()

                perms.put(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    PackageManager.PERMISSION_GRANTED
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    perms.put(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        PackageManager.PERMISSION_GRANTED
                    )
                }


                if (grantResults.isNotEmpty()) {

                    for (i in permissions.indices) {
                        perms[permissions[i]] = grantResults[i]
                    }

                    // Workaround for Permission Bug: https://github.com/permissions-dispatcher/PermissionsDispatcher/issues/646
                    // I hate it >_<
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                        // Check for both permissions
                        if (perms.get(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.d(TAG, "Background and Fine location services permission granted")

                            // Start Requesting Updates.
                            getLastKnownLocation()
                        } else {
                            Log.d(TAG, "Some permissions are not granted ask again ")
                            //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                            //                        // shouldShowRequestPermissionRationale will return true
                            //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            ) {
                                val builder = AlertDialog.Builder(this)
                                builder.setMessage("Location Permissions are needed for this application.")
                                builder.setTitle("Permissions Required")
                                builder.setPositiveButton("Okay")
                                { dialog, which ->
                                    Log.d(TAG, "Permissions Accepted")
                                    makeRequest(
                                        mutableListOf(
                                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                    )
                                }
                                val dialog = builder.create()
                                dialog.show()

                                // Proceed with Logic by disabling the related features or close the app.
                            } else {
                                Toast.makeText(
                                    this,
                                    "Please Enable Permissions for this app in phone settings.",
                                    Toast.LENGTH_LONG
                                ).show()

                                this.finish()

                            }
                            //permission is denied (and never ask again is  checked)
                        }
                    } else {
                        // Check for both permissions
                        if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "Fine location services permission granted")

                            // Start Requesting Updates.
                            getLastKnownLocation()
                        } else {
                            Log.d(TAG, "Some permissions are not granted ask again ")
                            //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                            // shouldShowRequestPermissionRationale will return true
                            //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            ) {
                                val builder = AlertDialog.Builder(this)
                                builder.setMessage("Location Permissions are needed for this application.")
                                builder.setTitle("Permissions Required")
                                builder.setPositiveButton("Okay")
                                { dialog, which ->
                                    Log.d(TAG, "Clicked")
                                    makeRequest(
                                        mutableListOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                    )
                                }
                                val dialog = builder.create()
                                dialog.show()

                                // Proceed with Logic by disabling the related features or close the app.
                            } else {
                                Toast.makeText(
                                    this,
                                    "Please Enable Permissions for this app in phone settings.",
                                    Toast.LENGTH_LONG
                                ).show()

                                this.finish()

                            }
                            //permission is denied (and never ask again is  checked)
                        }
                    }


                }
            }
        }
    }


    private fun initGmaps() {
        Log.d(TAG, "initGmaps()")
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        initFindUser = true
    }

    private fun createGoogleApi() {
        Log.d(TAG, "createGoogleApi()")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(){

        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0.0f, this)

    }

    // Get Last Known Location
    private fun getLastKnownLocation() {

        Log.d(TAG, "getLastKnownLocation()")

        fusedLocationClient.lastLocation
            .addOnSuccessListener(this) { location ->
                // Got last known location.
                if (location != null) {
                    // Logic to handle location object
                    Log.i(
                        TAG,
                        "LastKnown location. "
                                + "Long: "
                                + location.longitude
                                + " | Lat: "
                                + location.latitude
                    )
                    lastLocation = location

                    writeLastLocation()
                    startLocationUpdates()
                } else {
                    Log.w(TAG, "No Location Found Yet")
                    startLocationUpdates()
                }
            }
    }

    private fun writeLastLocation() {
        Log.i(TAG, "writeLastLocation()")
        writeActualLocation(lastLocation)

    }

    private fun writeActualLocation(lastLocation: Location?) {
        Log.i(TAG, "writeActualLocation()")
        markerLocation(LatLng(lastLocation!!.latitude, lastLocation.longitude))
        // TODO("Add Textview to record lat and long")
    }

    private fun markerLocation(latLng: LatLng) {

        Log.i(TAG, "markerLocation [ $latLng]")

        // Define Marker Options
        val title = latLng.latitude.toString() + ", " + latLng.longitude
        val markerOptions = MarkerOptions().position(latLng).title(title)
        if (locationMarker != null) {
            locationMarker!!.remove()
        }
        locationMarker = map.addMarker(markerOptions)
        Log.wtf(TAG, "markerLocation [ $latLng]")
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
        Log.d(TAG, "onMapReady()")
        map = googleMap
        map.setOnMapClickListener(this)
        map.setOnMarkerClickListener(this)
        recoverGeofenceMarker()

    }

    override fun onMapClick(latLng: LatLng) {

        Log.d(TAG, "onMapClick($latLng)")
        markerForGeofence(latLng)
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        Log.d(TAG, "onMarkerClickListener: " + marker.position)
        return false
    }

    private fun markerForGeofence(latLng: LatLng) {

        Log.i(TAG, "markerForGeoFence$latLng)")

        // Define Marker Options
        val title = latLng.latitude.toString() + ", " + latLng.longitude
        val markerOptions = MarkerOptions().position(latLng).icon(
            BitmapDescriptorFactory
                .defaultMarker(
                    BitmapDescriptorFactory.HUE_AZURE
                )
        )
            .title(title)

        // Remove last geoFenceMarker
        if (geoFenceMarker != null) {
            geoFenceMarker!!.remove()
        }

        geoFenceMarker = map.addMarker(markerOptions)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //Define the listener
    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "onLocationChanged() [$location ]")


        // If lastLocation is initialized check to see if it is still up-to-date.
        // If not, update it and redraw marker.
        if (lastLocation != null) {
            if (lastLocation != location) {
                if (locationMarker != null)
                {
                    locationMarker!!.remove()
                }

                val latitude = location.latitude
                val longitude = location.longitude
                val latLng = LatLng(latitude, longitude)

                // Use Google's Geocoder api to find out information about the coordinates.
                val geocoder = Geocoder(applicationContext)

                val addressList: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)

                var addressInfo = addressList[0].locality + " " + addressList[0].countryName

                // Create marker with location of user and address information.
                locationMarker = map.addMarker(MarkerOptions().position(latLng).title(addressInfo))

                // On first run, animate to user's location marker.
                if (initFindUser)
                {
                    initFindUser = false
                    centerCameraOnLocation(locationMarker!!.position);
                }
            }
        }
        else
        {
            lastLocation = location
            val latitude = location.latitude
            val longitude = location.longitude
            val latLng = LatLng(latitude, longitude)

            val geocoder = Geocoder(applicationContext)

            val addressList: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)

            var addressInfo = addressList[0].locality + " " + addressList[0].countryName

            locationMarker = map.addMarker(MarkerOptions().position(latLng).title(addressInfo))

            if (initFindUser)
            {
                initFindUser = false
                centerCameraOnLocation(locationMarker!!.position);
            }
        }
    }
    private fun centerCameraOnLocation(position: LatLng) {
        Log.i(TAG, "centerCameraOnLocation()")

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0f))
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.geofence -> {
                startGeofence()
                return true
            }
            R.id.clear -> {
                clearGeofence()
                return true
            }
            R.id.centerlocation -> {

                if(lastLocation == null)
                {
                    Toast.makeText(this,"Please Wait Until Your Location is Found.", Toast.LENGTH_LONG).show()
                }
                else {
                    centerCameraOnLocation(
                        LatLng(
                            lastLocation!!.latitude,
                            lastLocation!!.longitude
                        )
                    )
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    companion object {

        private val TAG = MapsActivity::class.java.simpleName
        private val GEOFENCE_RADIUS = 300.0f // Meters

        private val NOTIFICATION_MESSAGE = "NOTIFICATION MESSAGE"

        fun makeNotificationIntent(context: Context, message: String): Intent {

            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra(NOTIFICATION_MESSAGE, message)
            return intent

        }

        private val GEOFENCE_REQ_ID = "Tc2r Geofence"
        private val GEO_DURATION = (60 * 60 * 1000).toLong()
    }
    // Add the created GeofenceRequest to the device's monitoring list.
    private fun addGeofence(geofencingRequest: GeofencingRequest) {
        Log.d(TAG, "addGeofence")
        if (setPermissions()) {
            geofencingClient = LocationServices.getGeofencingClient(this)
            geofencingClient!!.addGeofences(geofencingRequest, createGeofencePendingIntent())
                .addOnSuccessListener(this).addOnFailureListener(this)
        }

    }

    // Add Geofence On Success Listener
    override fun onSuccess(p0: Void?) {
        Log.d(TAG, "Add Geofence onSuccessListener")
        saveGeofence()
        drawGeofence()
    }

    // Add Geofence On Failure Listener
    override fun onFailure(e: Exception) {
        Log.d(TAG, "Add Geofence onFailureListener")
        // Log Failure.
    }

    // Start Geofence creation process
    private fun startGeofence() {
        Log.d(TAG, "startGeofence()")
        if (geoFenceMarker != null) {
            val geofence = createGeoFence(geoFenceMarker!!.position, GEOFENCE_RADIUS)
            val geofencingRequest = createGeofenceRequest(geofence)
            addGeofence(geofencingRequest)
        } else {
            Log.e(TAG, "Geofence Marker is Null")
        }
    }

    // Create a Geofence.
    private fun createGeoFence(latLng: LatLng, radius: Float): Geofence {
        Log.d(TAG, "createGeofence()")
        return Geofence.Builder().setRequestId(GEOFENCE_REQ_ID)
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setExpirationDuration(GEO_DURATION).setTransitionTypes(
                GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT
            )
            .build()
    }

    // Saving GeoFence Marker with prefs mng
    private fun saveGeofence() {
        Log.d(TAG, "saveGeofence()")

        // Saving GeoFence marker with prefs mng
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putLong(
            KEY_GEOFENCE_LAT,
            java.lang.Double.doubleToRawLongBits(geoFenceMarker!!.position.latitude)
        )
        editor.putLong(
            KEY_GEOFENCE_LON,
            java.lang.Double.doubleToRawLongBits(geoFenceMarker!!.position.longitude)
        )
        editor.apply()
    }

    // Recovering last Geofence Marker
    private fun recoverGeofenceMarker() {
        Log.d(TAG, "recoverGeofenceMarker()")

        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        if (sharedPreferences.contains(KEY_GEOFENCE_LAT) && sharedPreferences
                .contains(KEY_GEOFENCE_LON)
        ) {
            val lat =
                java.lang.Double.longBitsToDouble(sharedPreferences.getLong(KEY_GEOFENCE_LAT, -1))
            val lon =
                java.lang.Double.longBitsToDouble(sharedPreferences.getLong(KEY_GEOFENCE_LON, -1))
            val latLng = LatLng(lat, lon)
            markerForGeofence(latLng)
            drawGeofence()
        }
    }

    // Use CircleOptions to draw a circle to represent the limts of the Geofence.
    private fun drawGeofence() {
        Log.d(TAG, "drawGeofence()")

        if (geoFenceLimits != null) {
            geoFenceLimits!!.remove()
        }

        val circleOptions = CircleOptions().center(geoFenceMarker!!.position)
            .strokeColor(Color.argb(50, 70, 70, 70))
            .fillColor(Color.argb(100, 150, 150, 150))
            .radius(GEOFENCE_RADIUS.toDouble())
        geoFenceLimits = map.addCircle(circleOptions)
    }

    // Clear Geofence
    private fun clearGeofence()
    {
        Log.d(TAG, "clearGeofence()")

        geofencingClient = LocationServices.getGeofencingClient(this)
        geofencingClient!!.removeGeofences(createGeofencePendingIntent())
            .addOnSuccessListener { removeGeofenceDraw() }
    }

    // Remove on screen draw of Geofence.
    private fun removeGeofenceDraw()
    {
        Log.d(TAG, "removeGeofenceDraw()")
        if (geoFenceMarker != null) {
            geoFenceMarker!!.remove()
        }
        if (geoFenceLimits != null) {
            geoFenceLimits!!.remove()
        }
    }

    // Create a Geofence Request
    private fun createGeofenceRequest(geofence: Geofence): GeofencingRequest {

        Log.d(TAG, "createGeofenceRequest()")
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).addGeofence(geofence)
            .build()
    }

    // Creates a Geofence Pending Intent if one doesn't exist.
    private fun createGeofencePendingIntent(): PendingIntent {

        Log.d(TAG, "createGeofencePendingIntent()")
        if (geofencePendingIntent != null) {
            return geofencePendingIntent
        }

        val intent = Intent(this.applicationContext, GeofenceTransitionService::class.java)
        return PendingIntent
            .getService(this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    }


}
