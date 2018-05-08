package com.example.aalap.weatherk.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
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
import android.widget.*
import com.example.aalap.weatherk.Adapters.AdapterDaily
import com.example.aalap.weatherk.Adapters.AdapterHourly
import com.example.aalap.weatherk.Model.Forecast.Forecast
import com.example.aalap.weatherk.Model.WeatherModel
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
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.tbruyelle.rxpermissions2.RxPermissions
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MainView {

    private lateinit var recyclerHourly: RecyclerView
    private lateinit var recyclerDaily: RecyclerView
    private lateinit var managerDaily: LinearLayoutManager
    private lateinit var managerHourly: LinearLayoutManager
    private lateinit var locationManager: LocationManager
    private lateinit var presenter: Presenter
    private lateinit var locationProvider: ReactiveLocationProvider
    lateinit var progress: ProgressBar
    private lateinit var locationRequest: LocationRequest
    private val TAG = "MainActivity:"
    private lateinit var navView: NavigationView
    lateinit var menu: Menu
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var feelsLike: TextView
    private lateinit var currentTemperature: TextView
    lateinit var background: ImageView
    private lateinit var addCity: ImageView
    private lateinit var removeCity: ImageView
    lateinit var refresh: ImageView
    private lateinit var pref: Preference
    var compositeDisposable = CompositeDisposable()
    lateinit var navIcon:ImageView
    lateinit var navCityName:TextView
    lateinit var navCurrentTemp:TextView
    lateinit var navBackground:ImageView
    lateinit var drawerMain:ViewGroup


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
        drawerMain = bind(R.id.drawer_layout)
        collapsingToolbarLayout = bind(R.id.collapsing_toolbar)
        collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(assets, "fonts/Lato-Black.ttf"))
        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(assets, "fonts/Lato-Black.ttf"))
        feelsLike = bind(R.id.feels_like)
        background = bind(R.id.background)
        currentTemperature = bind(R.id.current_temp)

        navView = findViewById(R.id.nav_view)

        val navMainView = navView.getHeaderView(0)

        navBackground = navMainView.findViewById(R.id.nav_background)
        navCityName = navMainView.findViewById(R.id.nav_current_city_name)
        navCurrentTemp = navMainView.findViewById(R.id.nav_current_temperature)
        navIcon = navMainView.findViewById(R.id.nav_current_weather_icon)


        menu = navView.menu

        addCity = bind(R.id.add_city)
        removeCity = bind(R.id.remove_city)
        refresh = bind(R.id.refresh)

        addCity.setOnClickListener({ openLocationSearchScreen() })
        removeCity.setOnClickListener({ startActivity(Intent(this, RemoveCity::class.java)) })
        refresh.setOnClickListener({ v -> refreshWeather() })


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

    private fun refreshWeather() {

        Toast.makeText(this, "Refreshing.....",Toast.LENGTH_SHORT).show()

        progress.visibility = View.VISIBLE

        val selectedLatitude = pref.getSelectedtLatitude()
        val selectedLongitude = pref.getSelectedLongitude()

        if (selectedLatitude != -1.0 && selectedLongitude != -1.0)
            presenter.initiateWeatherRequest(selectedLatitude, selectedLongitude)

        val selectedCity = City()
                .queryFirst { equalTo("latitude", selectedLatitude).equalTo("longitude", selectedLongitude)}

        if(selectedCity != null) {
            requestPlacePhoto(selectedCity.id)
        }
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
            presenter.requestPlacePhoto(city.id)
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
        progress.visibility = View.GONE
        Log.d(TAG, "Error:!!!!!!!!!!!!$errorMsg")
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
        navCurrentTemp.text = forecast.currently.getCurrentTemperature().toString()
        navIcon.setImageResource(Utils.getIcon(forecast.currently.icon))
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

        compositeDisposable.add(RxPermissions(this).request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ granted ->
                    if (granted) {

                        //gps on
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                            compositeDisposable.add(locationProvider.getUpdatedLocation(locationRequest)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ location: Location? ->

                                        presenter.initiateWeatherRequest(location!!.latitude, location.longitude)

                                        presenter.initiatePlaceAPICall(location.latitude, location.longitude)

                                        pref.setUserLocation(location.latitude, location.longitude)

                                    },
                                            { throwable: Throwable? -> presenter.showErrorMsg(throwable!!.localizedMessage) }))
                        }
                        //gps off
                        else
                            presenter.openGpsOrSavedLocationWeather()

                    } else {
                        Toast.makeText(this, "Can not load without location permission", Toast.LENGTH_LONG)
                                .show()
                        finish()
                    }
                }, { t: Throwable? -> showError(t!!.localizedMessage) }))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PLACE_SEARCH_REQUEST) {
            when (resultCode) {

                RESULT_OK -> {

                    val place = PlaceAutocomplete.getPlace(this, data)

                    presenter.initiateWeatherRequest(place.latLng.latitude, place.latLng.longitude)
                    showPlaceName(place.name as String?)

                    City(place.id,
                            place.latLng.latitude,
                            place.latLng.longitude,
                            place.name.toString()).save()

                    Log.d(WeatherModel.TAG, "RealmCity:"+City().queryAll().size)

                    val placeId = place.id
                    presenter.requestPlacePhoto(placeId)
                }

                PlaceAutocomplete.RESULT_ERROR -> {
                    val status = PlaceAutocomplete.getStatus(this, data)
                    Log.i(TAG, status.statusMessage)
                }
                RESULT_CANCELED -> Log.d(TAG, "cancelled")
            }
        }
    }

    override fun requestPlacePhoto(placeId: String?) {
        Log.d(TAG, "onView:$placeId")
        runOnUiThread({
            presenter.requestPlacePhoto(placeId)
        })
    }

    override fun noPlaceId() {
        runOnUiThread({
            Toast.makeText(this, "No placeId", Toast.LENGTH_SHORT).show()
        })
    }

    override fun showPlaceName(cityName: String?) {
        collapsingToolbarLayout.title = cityName
        navCityName.text = cityName
    }

    override fun showPlacePhoto(bitmap: Bitmap?) {
        if(bitmap != null) {
            background.setImageBitmap(bitmap)
            navBackground.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "No photo", Toast.LENGTH_SHORT).show()
            background.setImageResource(0)
            navBackground.setImageResource(0)
        }

    }

    override fun openGpsDialog() {
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
