package com.ahuja.sons.ahujaSonsClasses.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.Interface.LocationPermissionHelper
import com.ahuja.sons.ahujaSonsClasses.model.RouteListModel
import com.ahuja.sons.ahujaSonsClasses.model.TripDetailModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityReturnAutoTrackingBinding
import com.ahuja.sons.globals.Global
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import com.google.android.gms.location.*
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class ReturnAutoTrackingActivity : AppCompatActivity() {

    lateinit var binding: ActivityReturnAutoTrackingBinding
    lateinit var viewModel: MainViewModel
    lateinit var client: FusedLocationProviderClient
    var orderID = ""


    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }

    companion object {
        private const val TAG = "ReturnAutoTrackingActiv"
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReturnAutoTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderID = intent.getStringExtra("id").toString()

        setUpViewModel()

        checkAndRequestPermissions()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Handle the back arrow click
        toolbar.setNavigationOnClickListener {
            onBackPressed() // or use finish() to close the activity
        }

        client = LocationServices.getFusedLocationProviderClient(this)

        var jsonObject = JsonObject()
        jsonObject.addProperty("id", orderID)
        viewModel.callWorkQueueDetailApi(jsonObject)
        bindWorkQueueDetail()

        binding.btnStartTrip.setOnClickListener {
            if (!LocationPermissionHelper.hasLocationPermission(this)) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                getMyCurrentLocation("StartTrip")
            }
        }

        binding.btnReachedOffice.setOnClickListener {
            if (!LocationPermissionHelper.hasLocationPermission(this)) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                getMyCurrentLocation("EndTrip")
            }
        }

    }


    var globalDataWorkQueueList = AllWorkQueueResponseModel.Data()
    var globalDataWorkValue = AllWorkQueueResponseModel()

    //todo work queue detail api --
    private fun bindWorkQueueDetail() {
        viewModel.workQueueOne.observe(this, Event.EventObserver(
            onError = {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Global.warningmessagetoast(this, it)
            },
            onLoading = {
                binding.loadingBackFrame.visibility = View.VISIBLE
                binding.loadingView.start()
            },
            onSuccess = { response ->
                if (response.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                    if (response.data.size > 0) {
                        var modelData = response.data[0]

                        globalDataWorkQueueList = response.data[0]
                        globalDataWorkValue = response

                        callReturnTripDetailApi("")

                    }


                }


            }

        ))
    }


    @SuppressLint("MissingPermission")
    private fun getMyCurrentLocation(type: String) {
        // Initialize Location manager
        val locationManager =
            this@ReturnAutoTrackingActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            // When location service is enabled
            // Get last location
            client.lastLocation?.addOnCompleteListener { task ->
                // Initialize location
                val location: Location? = task.result
                // Check condition
                if (location != null) {
                    Log.e(TAG, "onComplete: locationNotNull")
                    // When location result is not null, set latitude
                    val geocoder = Geocoder(this, Locale.getDefault())
                    var addresses: List<Address>? = null

                    try {
                        addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        ) // Here 1 represents max location result to return, as per documentation recommended 1 to 5
                        val address = addresses?.get(0)
                            ?.getAddressLine(0) // If any additional address line present, check with max available address lines by getMaxAddressLineIndex()
                        val city = addresses?.get(0)?.locality
                        val state = addresses?.get(0)?.adminArea
                        val country = addresses?.get(0)?.countryName
                        val postalCode = addresses?.get(0)?.postalCode
                        val knownName = addresses?.get(0)?.featureName

                        Log.e(TAG, "onComplete: Call Api" + address)
                        if (type == "StartTrip") {
                            startTripApiCall(location?.latitude!!, location?.longitude!!, address)
                        } else {
                            reachedTripApiCall(location?.latitude!!, location?.longitude!!, address)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e(TAG, "onComplete: locationNull")
                    // When location result is null, initialize location request
                    val locationRequest =
                        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
                            .setMinUpdateIntervalMillis(1000L)
                            .setMaxUpdates(1)
                            .build()

                    // Initialize location callback
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            // Initialize location
                            val location1 = locationResult.lastLocation
                            val geocoder =
                                Geocoder(this@ReturnAutoTrackingActivity, Locale.getDefault())
                            var addresses: List<Address>? = null

                            try {
                                addresses = geocoder.getFromLocation(
                                    location1!!.latitude,
                                    location1.longitude,
                                    1
                                ) // Here 1 represent max location result to return, as per documentation recommended 1 to 5
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            val address = addresses?.get(0)
                                ?.getAddressLine(0) // If any additional address line present, check with max available address lines by getMaxAddressLineIndex()
                            val city = addresses?.get(0)?.locality
                            val state = addresses?.get(0)?.adminArea
                            val country = addresses?.get(0)?.countryName
                            val postalCode = addresses?.get(0)?.postalCode
                            val knownName = addresses?.get(0)?.featureName
                            Log.e(TAG, "onComplete: Call Api123" + address)

                            if (type == "StartTrip") {
                                startTripApiCall(
                                    location?.latitude!!,
                                    location?.longitude!!,
                                    address
                                )
                            } else {
                                reachedTripApiCall(
                                    location?.latitude!!,
                                    location?.longitude!!,
                                    address
                                )
                            }

                        }
                    }

                    // Request location updates
                    client.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.myLooper()
                    )
                }
            }
        } else {
            // When location service is not enabled, open location setting
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }


    //todo start trip for delivery person--
    private fun startTripApiCall(latitude: Double, longitude: Double, address: String?) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()

        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("WorkQueueId", globalDataWorkQueueList.id)
        jsonObject1.addProperty("DeliveryAssigned", globalDataWorkQueueList.DeliveryAssigned)
        jsonObject1.addProperty("DeliveryNote", "")
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest?.id)
        jsonObject1.addProperty("StartAt", Global.getTodayDateDashFormatReverse() + " " + Global.getfullformatCurrentTime())
        jsonObject1.addProperty("StartLocation", address)//address
        jsonObject1.addProperty("DepositedBy", Prefs.getString(Global.Employee_Code, ""))
        jsonObject1.addProperty("is_return", globalDataWorkQueueList.is_return)
        jsonObject1.addProperty("is_return_to_office", globalDataWorkQueueList.is_return_to_office)

        val call: Call<RouteListModel> = ApiClient().service.startTripForDeliveryPerson(jsonObject1)
        call.enqueue(object : Callback<RouteListModel?> {
            override fun onResponse(
                call: Call<RouteListModel?>,
                response: Response<RouteListModel?>
            ) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                    binding.apply {
                        tvDuration.visibility = View.VISIBLE
                        startLocationLayout.visibility = View.VISIBLE
                        startTimeLayout.visibility = View.VISIBLE
                        btnStartTrip.visibility = View.GONE
                        btnReachedOffice.visibility = View.VISIBLE
                    }

                    running = true

                    callReturnTripDetailApi("StartTrip")
                } else {

                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(
                        this@ReturnAutoTrackingActivity,
                        response.body()!!.errors
                    );

                }
            }

            override fun onFailure(call: Call<RouteListModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: " + t.message)
            }
        })

    }


    //todo end trip
    private fun reachedTripApiCall(latitude: Double, longitude: Double, address: String?) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()

        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("id", globalDataWorkQueueList.id)
        jsonObject1.addProperty("CreatedBy", Prefs.getString(Global.Employee_Code, ""))
        jsonObject1.addProperty("is_return", globalDataWorkQueueList.is_return)


        val call: Call<RouteListModel> = ApiClient().service.reachedOffice(jsonObject1)
        call.enqueue(object : Callback<RouteListModel?> {
            override fun onResponse(
                call: Call<RouteListModel?>,
                response: Response<RouteListModel?>
            ) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()

                    running = false

                    callReturnTripDetailApi("EndTrip")


                    Log.e("data", response.body()!!.data.toString())



                    onBackPressed()
                    finish()


                } else {

                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(
                        this@ReturnAutoTrackingActivity,
                        response.body()!!.errors
                    );

                }
            }

            override fun onFailure(call: Call<RouteListModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: " + t.message)
            }
        })


    }


    //todo call trip detail--
    private fun callReturnTripDetailApi(flag: String) {
        binding.loadingBackFrame.visibility = View.VISIBLE
        binding.loadingView.start()
        var jsonObject1 = JsonObject()
        jsonObject1.addProperty("WorkQueueId", globalDataWorkQueueList.id)
        jsonObject1.addProperty("OrderID", globalDataWorkQueueList.OrderRequest!!.id)

        val call: Call<TripDetailModel> = ApiClient().service.getReturnOrderTripApi(jsonObject1)
        call.enqueue(object : Callback<TripDetailModel?> {
            override fun onResponse(
                call: Call<TripDetailModel?>,
                response: Response<TripDetailModel?>
            ) {
                if (response.body()!!.status == 200) {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Log.e("data", response.body()!!.data.toString())

                    var listData = response.body()!!.data

                    if (listData.size > 0) {

                        var data = listData[0]


                        if (data.StartAt.isNotEmpty()) {

                            val dateStr = data.StartAt
                            val secondsTimeer = Global.secondsBetween(dateStr)
                            println("Seconds between $dateStr and now: $secondsTimeer")

                            try {
                                if (data.EndAt.toString().isBlank() && data.StartAt.toString().isNotBlank()) { //2024_09_24 11:20:00
                                    if (data.EndAt.toString() != "foo" && data.StartAt.toString() != "foo") {
                                        running = true
                                        seconds = secondsTimeer.toLong()
                                    }
                                    Log.e("sec", seconds.toString())
                                } else if (data.EndAt.toString().isNotBlank() && data.StartAt.toString().isBlank()) {
                                    if (data.EndAt.toString() != "foo" && data.StartAt.toString() != "foo") {
                                        seconds = secondsTimeer.toLong()
                                        Log.e("sec", seconds.toString())
                                    }
                                }
                            } catch (e: NumberFormatException) {
                                e.printStackTrace()
                            }


                            binding.apply {

                                if (data.StartAt.isNotEmpty() && data.EndAt.isEmpty()) {
                                    tvStartLocation.setText(data.StartLocation)
                                    tvStartTime.setText(Global.convert_yy_MM_dd_HH_mm_ss_into_dd_MM_yy_HH_mm_ss(data.StartAt))

                                    binding.apply {
                                        tvDuration.visibility = View.VISIBLE
                                        startLocationLayout.visibility = View.VISIBLE
                                        startTimeLayout.visibility = View.VISIBLE
                                        btnStartTrip.visibility = View.GONE
                                        btnReachedOffice.visibility = View.VISIBLE
                                    }

                                }

                                if (data.StartAt.isNotEmpty() && data.EndAt.isNotEmpty()) {

                                }


                            }

                            runTimer()

                        }
                    }

                } else {
                    binding.loadingBackFrame.visibility = View.GONE
                    binding.loadingView.stop()
                    Global.warningmessagetoast(
                        this@ReturnAutoTrackingActivity,
                        response.body()!!.message
                    );

                }
            }

            override fun onFailure(call: Call<TripDetailModel?>, t: Throwable) {
                binding.loadingBackFrame.visibility = View.GONE
                binding.loadingView.stop()
                Log.e(TAG, "onFailure: " + t.message)
                Toast.makeText(this@ReturnAutoTrackingActivity, t.message, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


    private fun checkAndRequestPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val write =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val read =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        val listPermissionsNeeded = mutableListOf<String>()

        if (write != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        return if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            false
        } else {
            true
        }
    }


    private var isRunning = false
    private var elapsedTime = 0L
    private val handler = Handler(Looper.getMainLooper())


    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedTime += 1000
                val hours = (elapsedTime / (1000 * 60 * 60)).toInt()
                val minutes = ((elapsedTime / (1000 * 60)) % 60).toInt()
                val seconds = ((elapsedTime / 1000) % 60).toInt()
                binding.tvDuration.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        }
    }

    private var seconds: Long = 0

    // Is the stopwatch running?
    private var running = false

    private fun runTimer() {

// Get the text view.

// Creates a new Handler
        val handler = Handler()

        handler.post(object : Runnable {
            override fun run() {
                val hours: Long = seconds / 3600
                val minutes: Long = seconds % 3600 / 60
                val secs: Long = seconds % 60

                // Format the seconds into hours, minutes,
                // and seconds.
                val time: String = java.lang.String
                    .format(
                        Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs
                    )

                // Set the text view text.
                binding.tvDuration.text = time

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000)
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
    }


}