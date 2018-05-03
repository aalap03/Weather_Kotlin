package com.example.aalap.weatherk.Activity

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.aalap.weatherk.Adapters.AdapterDaily
import com.example.aalap.weatherk.Adapters.AdapterHourly
import com.example.aalap.weatherk.Model.Forecast.Forecast
import com.example.aalap.weatherk.Presenter
import com.example.aalap.weatherk.R
import com.example.aalap.weatherk.View.MainView
import com.google.android.gms.location.LocationRequest
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

private val MainActivity.reactiveLocationProvider1: ReactiveLocationProvider
    get() {
        val reactiveLocationProvider = ReactiveLocationProvider(this)
        return reactiveLocationProvider
    }

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MainView {

    lateinit var recyclerHourly: RecyclerView
    lateinit var recyclerDaily: RecyclerView
    lateinit var managerDaily: LinearLayoutManager
    lateinit var managerHourly: LinearLayoutManager
    lateinit var locationManager: LocationManager
    lateinit var presenter: Presenter
    lateinit var locationProvider: ReactiveLocationProvider
    lateinit var progress: ProgressBar
    lateinit var locationRequest: LocationRequest
    val TAG = "MainActivity:"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar1)
        presenter = Presenter(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        progress = bind(R.id.progress)
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
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
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
        handlePermission()
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
                                    .subscribe({ location: Location? -> presenter.initiateWeatherRequest(location!!.latitude, location!!.longitude) }
                                            , { throwable: Throwable? -> presenter.showErrorMsg(throwable!!.localizedMessage) })
                        } else {

                        }
                    } else {
                        Toast.makeText(this, "Can not load without location permission", Toast.LENGTH_LONG)
                                .show()
                        finish()
                    }
                })
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

    }

    override fun showProgress(visible: Boolean) {
        progress.visibility = if(visible) View.VISIBLE else View.GONE
    }

    override fun showError(errorMsg: String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
    }

    override fun showForecast(forecast: Forecast?) {

        showProgress(false)

        val data = forecast!!.hourly.data
        val adapter = AdapterHourly(this, data)
        recyclerHourly.adapter = adapter

        val dataDaily = forecast!!.daily.data
        val adapterDaily = AdapterDaily(this, dataDaily)
        recyclerDaily.adapter = adapterDaily
    }
}
