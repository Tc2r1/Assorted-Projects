package com.dreams.kotlingeofencedemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "MapsActivity"
    private val REQUEST_PERMISSIONS_ID_CODE = 123
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Check for and request required permissions.
        setPermissions()
        
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    // PERMISSION MANAGEMENT
    private fun setPermissions(): Boolean {
        val permissionsNeededList = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getPermissionsNeededListPost0(permissionsNeededList)
        } else {
            getPermissionsNeededListPre0(permissionsNeededList)
        }

        if (!permissionsNeededList.isEmpty()) {
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
                            // process the normal flow
                            //else any one or both the permissions are not granted
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
                                    Log.d(TAG, "Clicked")
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


                                //proceed with logic by disabling the related features or quit the app.
                            }
                            //permission is denied (and never ask again is  checked)
                        }
                    } else {
                        // Check for both permissions
                        if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "Fine location services permission granted")
                            // process the normal flow
                            //else any one or both the permissions are not granted
                        } else {
                            Log.d(TAG, "Some permissions are not granted ask again ")
                            //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                            //                        // shouldShowRequestPermissionRationale will return true
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


                                //proceed with logic by disabling the related features or quit the app.
                            }
                            //permission is denied (and never ask again is  checked)
                        }
                    }


                }
            }
        }
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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
