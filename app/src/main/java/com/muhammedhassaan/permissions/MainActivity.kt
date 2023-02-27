package com.muhammedhassaan.permissions

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.muhammedhassaan.permissions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClick()
    }

    //Method for handling the buttons and requesting permission for each click
    private fun onClick(){
        //Location
        binding.btnLocation.setOnClickListener {
            checkPermissions(
                LOCATION_PERMISSION,
                "location",
                LOCATION_REQUEST
            )
        }

        //Camera
        binding.btnCamera.setOnClickListener {
            checkPermissions(
                CAMERA_PERMISSION,
                "camera",
                CAMERA_REQUEST
            )
        }

        //Microphone
        binding.btnMic.setOnClickListener {
            checkPermissions(
                MIC_PERMISSION,
                "microphone",
                MIC_REQUEST
            )
        }

        //Storage
        binding.btnStorage.setOnClickListener {
            checkPermissions(
                STORAGE_PERMISSION,
                "storage",
                STORAGE_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String){
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                //Permission Denied
                permissionDeniedSnackBar(name)
            }else{
                //Permission Granted
                permissionGrantedSnackBar(name)
            }
        }

        when(requestCode){
            LOCATION_REQUEST -> innerCheck("location")
            CAMERA_REQUEST -> innerCheck("camera")
            MIC_REQUEST -> innerCheck("microphone")
            STORAGE_REQUEST -> innerCheck("storage")
        }
    }

    //Method to check the status of the permission and take the right actions corresponding to each case
    private fun checkPermissions(permission: String,name: String,requestCode: Int){
        if(RUNNING_M_OR_LOWER){
            when {
                permissionGranted(permission) -> {
                    permissionGrantedSnackBar(name)
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    showDialog(permission, name, requestCode)
                }
                else -> {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission),requestCode)
                }
            }
        }
    }

    //Checks if the permission is granted or not
    private fun permissionGranted(permission: String): Boolean{
        return ActivityCompat.checkSelfPermission(applicationContext,permission) == PackageManager.PERMISSION_GRANTED
    }

    //Dialog to explain the reason why the app requires such a permission
    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Permission Required")
            setMessage("Permission to access your $name is required to run the application")
            setPositiveButton("OK"){ dialog , which ->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission),requestCode)
            }
            setNegativeButton("Cancel"){ dialog , which ->

            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    //setting dialog to show to the user in case the user denied the permission multiple times
    private fun settingDialog(permission: String,name: String,requestCode: Int){
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Permission Required")
            setMessage("Permission to access your $name is required to run the application please allow the permission in the app settings")
            setPositiveButton("Settings") { dialog, which ->
                // Open app settings here
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + packageName)
                startActivity(intent)
            }
            setNegativeButton("Cancel") { dialog, which ->
                // Do nothing
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    //SnackBar to show permission granted message
    private fun permissionGrantedSnackBar(name: String){
        Snackbar.make(this,binding.root,"$name permission granted :)",Snackbar.ANIMATION_MODE_SLIDE).show()
        Log.i(TAG, "$name permission granted :)")
    }

    //SnackBar to show permission denied message
    private fun permissionDeniedSnackBar(name: String){
        Snackbar.make(this,binding.root,"$name permission denied :(",Snackbar.ANIMATION_MODE_SLIDE).show()
        Log.i(TAG, "$name permission denied :(")
    }


    companion object{
        //location permission and request code
        private const val LOCATION_REQUEST = 1
        private const val LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION

        //camera permission and request code
        private const val CAMERA_REQUEST = 2
        private const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA

        //microphone permission and request code
        private const val MIC_REQUEST = 3
        private const val MIC_PERMISSION = android.Manifest.permission.RECORD_AUDIO

        //storage permission and request code
        private const val STORAGE_REQUEST = 4
        private const val STORAGE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE


        private val TAG = MainActivity::class.java.simpleName

        //checking android SDK
        private val RUNNING_M_OR_LOWER = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
    }
}