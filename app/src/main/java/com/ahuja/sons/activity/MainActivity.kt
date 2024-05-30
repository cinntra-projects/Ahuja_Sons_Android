package com.ahuja.sons.activity


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.customer.customerapp.globals.ApiScheduler
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.pixplicity.easyprefs.library.Prefs
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.ahuja.sons.R
import com.ahuja.sons.ahujaSonsClasses.activity.AddSalesOrderActivity
import com.ahuja.sons.ahujaSonsClasses.fragments.order.OrderFragment
import com.ahuja.sons.animation.ViewAnimationUtils
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.databinding.ActivityMainBinding
import com.ahuja.sons.fragment.AccountFragment
import com.ahuja.sons.fragment.HomeFragment
import com.ahuja.sons.fragment.ProfileFragment
import com.ahuja.sons.fragment.TicketFragment
import com.ahuja.sons.globals.Global
import com.ahuja.sons.globals.MainBaseActivity
import com.ahuja.sons.service.repository.DefaultMainRepositories
import com.ahuja.sons.service.repository.MainRepos
import com.ahuja.sons.viewmodel.MainViewModel
import com.ahuja.sons.viewmodel.MainViewModelProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.IOException
import java.util.*
import kotlin.math.abs


class MainActivity : MainBaseActivity(), View.OnClickListener {

    var toggle: ActionBarDrawerToggle? = null
    private lateinit var binding: ActivityMainBinding
    var permission_granted = false

    lateinit var viewModel: MainViewModel

    val LOCATION_PERMISSION_REQUEST = 11111
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private const val TAG = "MainActivity"
    }

    private fun showCustomDialog(context: Context) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_anouncement)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()
        enableMyLocation()
//        val headerview : View = binding.navView.getHeaderView(0)
        setSupportActionBar(binding.appbarMain.toolbar)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //todo calling job schedular for current location..
        ApiScheduler.schedularCall(this)
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding.navigationView.background = null
        binding.appbarMain.heading.text = Prefs.getString(Global.Employee_Name, "")


        binding.appbarMain.tvAnnouncement.setOnClickListener {
            showCustomDialog(this)
        }

        /* if(Prefs.getString("NIGHT","No").equals("Yes")){
             binding.navDrawer.dayNightSwitch.setIsNight(true,true)
         }else{
             binding.navDrawer.dayNightSwitch.setIsNight(false,true)
         }*/

        toggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appbarMain.toolbar,
            R.string.open,
            R.string.close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                val inputMethodManager: InputMethodManager =
                    getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            }
        }

        binding.drawerLayout.addDrawerListener(toggle!!)
        toggle!!.syncState()
        val text = arrayOf("Search", "Your products", "Here")
        binding.appbarMain.fadigtextview.setTexts(text)
        //   val headerText : TextView = headerview.findViewById(R.id.header_title)
        callhomefragment(HomeFragment())

        clickeventlistener()
        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    callhomefragment(HomeFragment())
                }
                R.id.order -> {
                    loadfragment(OrderFragment())
                }
              /*  R.id.tickets -> {
                    // binding.container.setPadding(0,0,0,45)
                    loadfragment(TicketFragment(0))
                }
                R.id.customer -> {
                    //  binding.container.setPadding(0,0,0,45)
                    *//*val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)*//*
                    loadfragment(AccountFragment())

                }
                R.id.profile -> {
                    //  binding.container.setPadding(0,0,0,45)
                    loadfragment(ProfileFragment())
                }*/
            }
            true
        }


        binding.appbarMain.notificationView.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        makepowermenu()


    }


    private fun setUpViewModel() {
        val dispatchers: CoroutineDispatcher = Dispatchers.Main
        val mainRepos = DefaultMainRepositories() as MainRepos
        val fanxApi: Apis = ApiClient().service
        val viewModelProviderfactory =
            MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
        viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

    }


    open fun getMyCurrentLocation() {
        val locationManager = this.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
//                askPermissionForBackgroundUsage()
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
                            addresses =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        val address = addresses!![0].getAddressLine(0)
                        Log.e("manager_current_lat", location.latitude.toString())
                        Log.e("manager_current_long", location.longitude.toString())

                        //       bindLocationApi(location.latitude, location.longitude,address)

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
                                    addresses = geocoder.getFromLocation(
                                        location!!.latitude,
                                        location.longitude,
                                        1
                                    )
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                val address = addresses!![0].getAddressLine(0)
                                Log.e(
                                    "manager_current_lat_second_one",
                                    location!!.latitude.toString()
                                )
                                Log.e(
                                    "manager_current_long_second_one",
                                    location!!.longitude.toString()
                                )

                                //   bindLocationApi(location!!.latitude, location.longitude,address)

                            }
                        }

                        // Request location updates
                        fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper()
                        )
                    }
                })
        } else {
            this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    //todo get current location access..
    private fun enableMyLocation() {
        if (Global.checkForInternet(this)) {
            //todo check permissions..
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //todo when permission granted , access current location
                Log.e("access===>", "access")
//                askPermissionForBackgroundUsage()
            } else {
                //todo when permission denied, request permission..
                Log.e("denied===>", "denied")
                getMyCurrentLocation()
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),LOCATION_PERMISSION_REQUEST)
            }
        } else {
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Internet Connection Alert")
                .setMessage("Please Check Your Internet Connection")
                .setPositiveButton("Close") { dialogInterface, i ->
                    this.finish()
                }.show()
        }
    }


    private val BACKGROUND_LOCATION_PERMISSION_CODE = 1000021

    private fun askPermissionForBackgroundUsage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            AlertDialog.Builder(this)
                .setTitle("Permission Needed!")
                .setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
                .setPositiveButton("OK") { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        BACKGROUND_LOCATION_PERMISSION_CODE
                    )
                }
                .setNegativeButton("CANCEL") { dialog, which ->
                    // User declined Background Location Permission.
                }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BACKGROUND_LOCATION_PERMISSION_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                //todo when permission granted, call method
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                getMyCurrentLocation()
            }
        } else {
            Global.warningmessagetoast(this, "user has not granted location access permission")
            finish()
        }
    }


    private fun subscribeToDashboardObserver() {
        viewModel.dashboardCounter.observe(this, Event.EventObserver(
            onError = {
                // binding.loader.visibility = View.VISIBLE
                Global.errormessagetoast(this, it)
                //  Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }, onLoading = {
                //  binding.loader.visibility = View.VISIBLE
            }, {
                //  binding.loader.visibility = View.GONE
                if (it.status == 200) {
                    binding.appbarMain.totalPoints.text = it.data.Assigned.toString()
                    binding.appbarMain.activePoints.text = it.data.Pending.toString()
                    binding.appbarMain.inactive.text = it.data.In_Progress.toString()
                    binding.appbarMain.resolved.text = it.data.Resolved.toString()
                } else {
                    Global.errormessagetoast(this, it.message)
                }

            }

        ))

    }


    private fun clickeventlistener() {
        binding.navDrawer.home.setOnClickListener(this)
        binding.navDrawer.service.setOnClickListener(this)
        binding.navDrawer.accounts.setOnClickListener(this)
        binding.navDrawer.notification.setOnClickListener(this)
        binding.navDrawer.preventiveMaintence.setOnClickListener(this)
        binding.navDrawer.machineInstallation.setOnClickListener(this)
        binding.navDrawer.customerComplaints.setOnClickListener(this)
        binding.navDrawer.tvRepairMenu.setOnClickListener(this)
        binding.navDrawer.tvManTrapMenu.setOnClickListener(this)
        binding.navDrawer.contacts.setOnClickListener(this)
        binding.navDrawer.setting.setOnClickListener(this)
        binding.navDrawer.lightview.setOnClickListener(this)
        binding.navDrawer.darkview.setOnClickListener(this)
        binding.navDrawer.linearAnnouncment.setOnClickListener(this)
        binding.navDrawer.employeeLayout.setOnClickListener(this)
        binding.navDrawer.OrderLayout.setOnClickListener(this)
        binding.navDrawer.productLayout.setOnClickListener(this)
        binding.navDrawer.serviceContractLayout.setOnClickListener(this)
        binding.appbarMain.search.setOnClickListener(this)
        binding.appbarMain.searchIcon.setOnClickListener(this)
        binding.addOrderRequest.setOnClickListener(this)


    }


    private fun makepowermenu() {
        val powerMenu = PowerMenu.Builder(this) // list has "Novel", "Poerty", "Art"
            .addItem(PowerMenuItem("Last 7 days", true)) // add an item.
            .addItem(PowerMenuItem("Last month", false)) // aad an item list.
            .addItem(PowerMenuItem("Last 6 month", false)) // aad an item list.
            .addItem(PowerMenuItem("Last year", false)) // aad an item list.
            .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
            .setMenuRadius(10f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setTextColor(ContextCompat.getColor(this, R.color.black))
            .setTextGravity(Gravity.CENTER)
            .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
            .setSelectedTextColor(Color.WHITE)
            .setMenuColor(Color.WHITE)
            .setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .build()

        val onMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem?> =
            OnMenuItemClickListener<PowerMenuItem?> { position, item ->
                powerMenu.selectedPosition = position // change selected item
                powerMenu.dismiss()
            }
        powerMenu.onMenuItemClickListener = onMenuItemClickListener


        /* binding.appbarMain.dropdownMenu.setOnClickListener {
             powerMenu.showAsDropDown(binding.appbarMain.dropdownMenu)
         }*/

        binding.appbarMain.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->


            if (abs(verticalOffset) > 50) {

                binding.appbarMain.searchIcon.visibility = View.VISIBLE

            } else {
                binding.appbarMain.searchIcon.visibility = View.GONE

            }
        }
    }


    private fun callhomefragment(homeFragment: HomeFragment) {
//       // calldashboardcounterapi()
//        var hashMap = HashMap<String, String>()
//        hashMap.put(ApiPayloadKeys.EMPLOYEE_ID, Prefs.getString(Global.Employee_Code))
        viewModel.getDashboardCounter()

        subscribeToDashboardObserver()


        viewModel.getDashboardNotifictaionCount()
        notificationCountObserver()

        binding.container.setPadding(0, 0, 0, 0)

        /*   if (Global.classname == homeFragment.javaClass.name)
               return*/
        //    Global.hideKeybaord(binding.search,this)
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_quotes, homeFragment)

        transaction.commit()
    }

    private fun notificationCountObserver() {
        viewModel.notificationCount.observe(this, Event.EventObserver(
            onError = {
                Global.errormessagetoast(this, it)
            }, onLoading = {
            }, {
                if (it.status == 200) {
                    Log.d(TAG, "notificationCountObserver: "+Global.countNotification(it.data[0].notification))
                    binding.appbarMain.count.text = Global.countNotification(it.data[0].notification)
                    binding.navDrawer.noirificationCount.text = Global.countNotification(it.data[0].notification)

                } else {
                    Global.errormessagetoast(this, it.message)
                }

            }

        ))
    }




    private fun loadfragment(homeFragment: Fragment) {
        /*   if (Global.classname == homeFragment.javaClass.name)
               return*/
        //    Global.hideKeybaord(binding.search,this)
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, homeFragment).addToBackStack(null)
        transaction.commit()
    }


    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val fragments: Int = supportFragmentManager.backStackEntryCount
            if (fragments >= 0) {
                // finish()
                if (doubleBackToExitPressedOnce) {
                    /*   moveTaskToBack(true)
                    exitProcess(-1)*/
                    finishAffinity()
                    return
                }

                this.doubleBackToExitPressedOnce = true
                Global.infomessagetoast(this, resources.getString(R.string.exitmsz))
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    doubleBackToExitPressedOnce = false
                }, 2000)
            } else if (fragmentManager.backStackEntryCount > 1) {
                fragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.home -> {
                changenavbackground(
                    binding.navDrawer.home,
                    binding.navDrawer.accounts,
                    binding.navDrawer.notification,
                    binding.navDrawer.preventiveMaintence,
                    binding.navDrawer.machineInstallation,
                    binding.navDrawer.customerComplaints,
                    binding.navDrawer.contacts,
                    binding.navDrawer.setting
                )
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                binding.drawerLayout.closeDrawers()

            }
            R.id.addOrderRequest -> {

                val intent = Intent(this, AddSalesOrderActivity::class.java)
                startActivity(intent)

//                checkpermission()
            }
            R.id.contacts -> {
                val intent = Intent(this, ContactActivity::class.java)
                startActivity(intent)
                /*changenavbackground(binding.navDrawer.contacts,binding.navDrawer.accounts,binding.navDrawer.notification,
                    binding.navDrawer.preventiveMaintence,binding.navDrawer.machineInstallation,
                    binding.navDrawer.customerComplaints,binding.navDrawer.home,binding.navDrawer.setting)*/
                binding.drawerLayout.closeDrawers()
            }

            R.id.serviceContractLayout -> {
                val intent = Intent(this, ServiceContractActivity::class.java)
                startActivity(intent)
                binding.drawerLayout.closeDrawers()
            }

            R.id.employeeLayout -> {
                val intent = Intent(this, EmployeeActivity::class.java)
                startActivity(intent)
                binding.drawerLayout.closeDrawers()
            }

            R.id.productLayout -> {
                val intent = Intent(this, ProductListActivity::class.java)
                startActivity(intent)
                binding.drawerLayout.closeDrawers()
            }
            R.id.OrderLayout -> {
                val intent = Intent(this, OrderListActivity::class.java)
                startActivity(intent)
                binding.drawerLayout.closeDrawers()
            }

            R.id.linearAnnouncment -> {
                val intent = Intent(this, AnnouncementActivity::class.java)
                startActivity(intent)

                binding.drawerLayout.closeDrawers()
            }

            R.id.search,
            R.id.search_icon -> {

                val intent = Intent(this, Searchactivity::class.java)
                startActivity(intent)
            }
            R.id.accounts -> {
                /*  changenavbackground(
                      binding.navDrawer.accounts,
                      binding.navDrawer.home,
                      binding.navDrawer.notification,
                      binding.navDrawer.preventiveMaintence,
                      binding.navDrawer.machineInstallation,
                      binding.navDrawer.customerComplaints,
                      binding.navDrawer.contacts,
                      binding.navDrawer.setting

                  )*/

                loadfragment(AccountFragment())

//                binding.navigationView.menu.findItem(R.id.customer).isChecked = true //todo comment by me
                binding.drawerLayout.closeDrawers()
            }
            R.id.notification -> {
                val intent = Intent(this, NotificationActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Notification", Toast.LENGTH_SHORT).show()
                binding.drawerLayout.closeDrawers()
            }
            R.id.preventive_maintence -> {

                loadfragment(TicketFragment(1))
//                binding.navigationView.menu.findItem(R.id.tickets).isChecked = true;//todo comment by me
                binding.drawerLayout.closeDrawers()

            }

            R.id.tvRepairMenu -> {
                Intent(this, OtherTypeTicketActivity::class.java).also { intent ->
                    intent.putExtra(Global.INTENT_WHERE_STATUS, "repair")
                    startActivity(intent)
                }
//                loadfragment(TicketFragment(0))
//                binding.navigationView.menu.findItem(R.id.tickets).isChecked = true;//todo comment by me
                binding.drawerLayout.closeDrawers()

            }

            R.id.tvManTrapMenu -> {
                Intent(this, OtherTypeTicketActivity::class.java).also { intent ->
                    intent.putExtra(Global.INTENT_WHERE_STATUS, "mantrap")
                    startActivity(intent)
                }
//                loadfragment(TicketFragment(0))
//                binding.navigationView.menu.findItem(R.id.tickets).isChecked = true;//todo comment by me
                binding.drawerLayout.closeDrawers()

            }


            R.id.service -> {
                manageserviceview()
            }
            R.id.customer_complaints -> {

                loadfragment(TicketFragment(3))
//                binding.navigationView.menu.findItem(R.id.tickets).isChecked = true;//todo comment by me
                binding.drawerLayout.closeDrawers()

            }
            R.id.machine_installation -> {
                loadfragment(TicketFragment(2))
//                binding.navigationView.menu.findItem(R.id.tickets).isChecked = true;//todo comment by me
                binding.drawerLayout.closeDrawers()

            }
            R.id.setting -> {

                loadfragment(ProfileFragment())
//                binding.navigationView.menu.findItem(R.id.profile).isChecked = true;//todo comment by me
                binding.drawerLayout.closeDrawers()
            }
            R.id.darkview -> {
                changemodebackground(binding.navDrawer.darkview, binding.navDrawer.lightview)


            }
            R.id.lightview -> {
                changemodebackground(binding.navDrawer.lightview, binding.navDrawer.darkview)

            }


        }
    }

    private fun checkpermission() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    permission_granted = true
                    if (permission_granted) {

                        val intent = Intent(this@MainActivity, ScannerAct::class.java)
                        startActivity(intent)
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        permission_granted = false
                        Global.warningmessagetoast(
                            this@MainActivity,
                            "Please Allow Camera Permission from Setting"
                        )

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun changemodebackground(darkview: FrameLayout, lightview: FrameLayout) {
        darkview.background.setTint(darkview.resources.getColor(R.color.white))
        lightview.background.setTint(lightview.resources.getColor(R.color.veryLightGrey))


    }

    private fun manageserviceview() {
        if (binding.navDrawer.serviceView.isVisible) {
            ViewAnimationUtils.collapse(binding.navDrawer.serviceView)
        } else {
            ViewAnimationUtils.expand(binding.navDrawer.serviceView)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun changenavbackground(
        textvi1: LinearLayout,
        textvi2: LinearLayout,
        textvi3: LinearLayout,
        textvi4: TextView,
        textvi5: TextView,
        textvi6: TextView,
        textvi7: LinearLayout,
        textvi8: LinearLayout
    ) {


        /****Selected****/
        textvi1.setBackgroundResource(R.color.colorPrimary)
        // textvi1.setTextColor(ContextCompat.getColor(this, R.color.white))

        /****unSelected****/
        textvi2.setBackgroundResource(R.color.white)
        textvi3.setBackgroundResource(R.color.white)
        textvi4.setBackgroundResource(R.color.white)
        textvi5.setBackgroundResource(R.color.white)
        textvi6.setBackgroundResource(R.color.white)
        textvi7.setBackgroundResource(R.color.white)
        textvi8.setBackgroundResource(R.color.white)
        /*   textvi2.setTextColor(ContextCompat.getColor(this, R.color.black))
           textvi3.setTextColor(ContextCompat.getColor(this, R.color.black))
           textvi4.setTextColor(ContextCompat.getColor(this, R.color.black))
           textvi5.setTextColor(ContextCompat.getColor(this, R.color.black))
           textvi6.setTextColor(ContextCompat.getColor(this, R.color.black))
           textvi7.setTextColor(ContextCompat.getColor(this, R.color.black))
           textvi8.setTextColor(ContextCompat.getColor(this, R.color.black))
   */
        /* for (drawable in textvi1.compoundDrawables) {
             if (drawable != null) {
                 drawable.colorFilter =
                     PorterDuffColorFilter(R.color.white, PorterDuff.Mode.SRC_IN)
             }
         }*/

    }


}