package com.example.aalap.weatherk.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.IdRes
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.aalap.weatherk.Adapters.AdapterDaily
import com.example.aalap.weatherk.Adapters.AdapterHourly
import com.example.aalap.weatherk.Model.Forecast.Forecast
import com.example.aalap.weatherk.Presenter
import com.example.aalap.weatherk.R
import com.example.aalap.weatherk.Utils.App
import com.example.aalap.weatherk.Utils.City
import com.example.aalap.weatherk.Utils.Preference
import com.example.aalap.weatherk.Utils.Utils
import com.example.aalap.weatherk.View.MainView
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.tbruyelle.rxpermissions2.RxPermissions
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MainView {

    private lateinit var recyclerHourly: RecyclerView
    private lateinit var recyclerDaily: RecyclerView
    private lateinit var managerDaily: LinearLayoutManager
    private lateinit var managerHourly: LinearLayoutManager
    lateinit var locationManager: LocationManager
    lateinit var presenter: Presenter
    lateinit var locationProvider: ReactiveLocationProvider
    lateinit var progress: ProgressBar
    lateinit var locationRequest: LocationRequest
    private val TAG = "MainActivity:"
    lateinit var navView: NavigationView
    lateinit var menu: Menu
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    lateinit var feelsLike: TextView
    lateinit var currentTemperature: TextView
    lateinit var background: ImageView
    lateinit var addCity: ImageView
    lateinit var removeCity: ImageView
    lateinit var refresh: ImageView
    lateinit var pref:Preference


    companion object {
        var PLACE_SEARCH_REQUEST = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar1)

        pref = App.pref()
        presenter = Presenter(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        progress = bind(R.id.progress)
        collapsingToolbarLayout = bind(R.id.collapsing_toolbar)
        collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(assets, "fonts/Lato-Black.ttf"))
        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(assets, "fonts/Lato-Black.ttf"))
        feelsLike = bind(R.id.feels_like)
        background = bind(R.id.background)
        currentTemperature = bind(R.id.current_temp)

        navView = findViewById(R.id.nav_view)
        menu = navView.menu

        addCity = bind(R.id.add_city)
        removeCity = bind(R.id.remove_city)
        refresh = bind(R.id.refresh)

        addCity.setOnClickListener({ openLocationSearchScreen() })
        removeCity.setOnClickListener({ startActivity(Intent(this, RemoveCity::class.java)) })


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        locationRequest = LocationRequest.create()
                .setNumUpdates(1)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationProvider = ReactiveLocationProvider(this)
        setRecyclerViews()

        handlePermission()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val city = City().queryFirst { equalTo("name", item.title.toString()) }

        if (city != null) {
            presenter.initiateWeatherRequest(city.latitude, city.longitude)
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun <T : View> bind(@IdRes res: Int): T {
        @Suppress("UNCHECKED_CAST")
        return findViewById(res)
    }

    override fun onResume() {
        super.onResume()
        setDrawerItems()
    }

    override fun setRecyclerViews() {
        recyclerHourly = bind(R.id.recycler_hourly)
        recyclerDaily = bind(R.id.recycler_daily)

        managerHourly = LinearLayoutManager(this)
        managerHourly.orientation = LinearLayoutManager.HORIZONTAL
        managerDaily = LinearLayoutManager(this)
        recyclerDaily.layoutManager = managerDaily
        recyclerHourly.layoutManager = managerHourly

    }

    override fun setDrawerItems() {
        menu.clear()
        for (city in City().queryAll()) {
            menu.add(city.name)
        }
    }



    override fun showProgress(visible: Boolean) {
        progress.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun showError(errorMsg: String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
    }

    override fun showForecast(forecast: Forecast?) {

        showProgress(false)

        //hourly
        val data = forecast!!.hourly.data
        val adapter = AdapterHourly(this, data)
        recyclerHourly.adapter = adapter

        //daily
        val dataDaily = forecast.daily.data
        val adapterDaily = AdapterDaily(this, dataDaily)
        recyclerDaily.adapter = adapterDaily

        //current
        feelsLike.text = "Feels like " + forecast.currently.getFeelsLike()
        currentTemperature.text = forecast.currently.getCurrentTemperature().toString()

    }

    private fun openLocationSearchScreen() {
        Log.d(TAG, "openLocationSearchScreen: ")
        try {
            val intentTest = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this)
            startActivityForResult(intentTest, PLACE_SEARCH_REQUEST)

        } catch (e: GooglePlayServicesRepairableException) {
            Log.d(TAG, e.toString())
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.d(TAG, e.toString())
            e.printStackTrace()
        }

    }

    private fun handlePermission() {
        val permissions = RxPermissions(this)

        permissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe({ granted ->
                    if (granted) {

                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                            locationProvider.getUpdatedLocation(locationRequest)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ location: Location? ->

                                        presenter.initiateWeatherRequest(location!!.latitude, location.longitude)

                                        presenter.initiatePlaceAPICall(location.latitude, location.longitude)

                                        pref.setUserLocation(location.latitude, location.longitude)
                                        pref.setSelectedLocation(location.latitude, location.longitude)
                                    }
                                            , { throwable: Throwable? -> presenter.showErrorMsg(throwable!!.localizedMessage) })
                        } else {

                            val currentLatitude = pref.getSelectedtLatitude()
                            val currentLongitude = pref.getSelectedLongitude()

                            val userLatitude = pref.getUserLatitude()
                            val userLongitude = pref.getUserLongitude()

                            //get last saved user location weather...
                            if(userLatitude != -1.0 || userLongitude != -1.0) {
                                presenter.initiateWeatherRequest(userLatitude
                                        , userLongitude)

                                val queryFirst = City().queryFirst {
                                    equalTo("latitude", userLatitude)
                                            .equalTo("longitude", userLongitude)
                                }

                                showPlaceInfo(queryFirst!!.id)
                            }

                            //get last opened location weather...
                            else if (currentLatitude != -1.0 || currentLongitude != -1.0) {
                                presenter.initiateWeatherRequest(currentLatitude
                                        , currentLongitude)

                                val queryFirst = City().queryFirst {
                                    equalTo("latitude", currentLatitude)
                                            .equalTo("longitude", currentLongitude)
                                }

                                showPlaceInfo(queryFirst!!.id)
                            }
                            //make user open GPS, this case will be for the first timers...
                            else {
                                AlertDialog.Builder(this)
                                        .setMessage("Please Open GPS to access Location")
                                        .setNeutralButton("Go to Settings", { dialog, _ ->
                                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                            dialog.dismiss()
                                        })
                                        .setCancelable(false)
                                        .create()
                                        .show()
                            }

                        }
                    } else {
                        Toast.makeText(this, "Can not load without location permission", Toast.LENGTH_LONG)
                                .show()
                        finish()
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PLACE_SEARCH_REQUEST) {
            when (resultCode) {

                RESULT_OK -> {

                    val place = PlaceAutocomplete.getPlace(this, data)

                    presenter.initiateWeatherRequest(place.latLng.latitude, place.latLng.longitude)
                    pref.setSelectedLocation(place.latLng.latitude, place.latLng.longitude)
                    collapsingToolbarLayout.title = place.name

                    City(place.id,
                            place.latLng.latitude,
                            place.latLng.longitude,
                            place.name.toString()).save()

                    var placeId = place.id
                    showPlacePhoto(placeId)

                }
                PlaceAutocomplete.RESULT_ERROR -> {
                    val status = PlaceAutocomplete.getStatus(this, data)
                    Log.i(TAG, status.statusMessage)
                }
                RESULT_CANCELED -> Log.d(TAG, "cancelled")
            }
        }
    }

    private fun showPlacePhoto(placeId: String) {
        val geoDataClient = Places.getGeoDataClient(this)

        val placePhotos = geoDataClient.getPlacePhotos(placeId)

        Log.d(TAG, "showPlacePhoto$placeId")

        placePhotos.addOnCompleteListener({ task ->
            val result = task.result
            val photoMetadata = result.photoMetadata
            var totalPhotos = photoMetadata.count

            if (totalPhotos > 0) {
                var random = Random()
                var randomIndex = 0
                Log.d(TAG, "Photos: Total " + totalPhotos)
                Log.d(TAG, "Photos: RandomIndex" + randomIndex)
                if (totalPhotos > 1) {
                    randomIndex = random.nextInt(totalPhotos - 1)
                }

                geoDataClient.getPhoto(photoMetadata[randomIndex])
                        .addOnCompleteListener({ task ->
                            val bitmap = task.result.bitmap
                            background.setImageBitmap(bitmap)
                        })
            } else {
                Toast.makeText(this, "Show Default ohoto", Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun showPlaceInfo(placeId: String?) {
        Log.d(TAG, "onView:$placeId")
        runOnUiThread({
            showPlacePhoto(placeId!!)
        })
    }

    override fun noPlaceId() {
        runOnUiThread({
            Toast.makeText(this, "No placeId", Toast.LENGTH_SHORT).show()
        })
    }

    override fun showPlaceName(locality: String?) {
        collapsingToolbarLayout.title = locality
    }
}
