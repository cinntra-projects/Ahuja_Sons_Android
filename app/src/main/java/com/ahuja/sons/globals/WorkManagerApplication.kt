package com.ahuja.sons.globals

import android.Manifest
import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.gson.JsonObject
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.model.MapData
import com.ahuja.sons.newapimodel.ResponseAddTicket
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.NetworkInterface
import java.util.*

@SuppressLint("SpecifyJobSchedulerIdRange")
class WorkManagerApplication: JobService() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var sessionManagement : SessionManagement
    lateinit var networkInterface : NetworkInterface

    override fun onStartJob(params: JobParameters?): Boolean {
        val context = applicationContext
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        sessionManagement  = SessionManagement(context)

        try {
            Log.d("JobSchedular===>", "Schedular Called")
            getMyCurrentLocation()

        }catch (e:Exception){
            e.printStackTrace()
        }

        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.e("JobService===>", "Stop Service----")
        return false
    }

    //todo getting current location lat and long...
    open fun getMyCurrentLocation() {
        val locationManager = this.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(this@WorkManagerApplication, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@WorkManagerApplication, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationProviderClient.lastLocation.addOnCompleteListener(
                OnCompleteListener<Location?> { task ->
                    val location = task.result
                    if (location != null) {
                        val geocoder: Geocoder
                        var addresses: List<Address>? = null
                        geocoder = Geocoder(this, Locale.getDefault())
                        try {
                            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            val address = addresses!![0].getAddressLine(0)
                            Log.e("manager_current_lat", location.latitude.toString() )
                            Log.e("manager_current_long", location.longitude.toString() )

                            bindLocationApi(location.latitude, location.longitude,address)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }


                    } else {
                        val locationRequest = com.google.android.gms.location.LocationRequest()
                            .setPriority(LocationRequest.QUALITY_HIGH_ACCURACY)
                            .setInterval(10000)
                            .setFastestInterval(1000)
                            .setNumUpdates(1)

                        val locationCallback: LocationCallback = object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                val location = locationResult.lastLocation
                                val geocoder: Geocoder
                                var addresses: List<Address>? = null
                                geocoder = Geocoder(applicationContext, Locale.getDefault())
                                try {
                                    addresses = geocoder.getFromLocation(location!!.latitude, location.longitude, 1)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                val address = addresses!![0].getAddressLine(0)

                                bindLocationApi(location!!.latitude, location.longitude,address)

                            }
                        }

                        // Request location updates
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                    }
                })
        } else {
            this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    //todo view model observer...
    private fun bindLocationApi(latitude: Double, longitude: Double,address:String) {
        var jsonObject : JsonObject = JsonObject()
        jsonObject.addProperty("SalesPersonCode", sessionManagement.getPreferencesString(applicationContext, ApiPayloadKeys.EMPLOYEE_ID)!!)
        jsonObject.addProperty("Lat", latitude)
        jsonObject.addProperty("Long", longitude)

        val mapData = MapData()
        mapData.Emp_Id=Prefs.getString(Global.MyID, "")
        mapData.Emp_Name=Prefs.getString(Global.Employee_Name, "")
        mapData.Lat=latitude.toString()
        mapData.Long=longitude.toString()
        mapData.UpdateDate=Global.getTodayDateDashFormatReverse()
        mapData.UpdateTime=Global.getTCurrentTime()
        mapData.Address=address
        mapData.shape="meeting"
        mapData.type=""
        mapData.remark=""
        mapData.ResourceId=""
        mapData.SourceType=""
        mapData.ContactPerson=""

        val call: Call<ResponseAddTicket>? = ApiClient().service.sendMaplatlong(mapData)
        call?.enqueue(object : Callback<ResponseAddTicket> {
            override fun onResponse(call: Call<ResponseAddTicket>, response: Response<ResponseAddTicket>) {
                if (response != null) {
                    try {
                        if (response.body()!!.status.equals(200)) {
                            Log.e("success", "success")
                        }else{
                            Log.e("error", response.body()!!.message)

                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseAddTicket>, t: Throwable) {}
        })

    }



}