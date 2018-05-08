package com.example.aalap.weatherk.Model

import android.graphics.Bitmap
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import com.example.aalap.weatherk.Model.Forecast.Forecast
import com.example.aalap.weatherk.Presenter
import com.example.aalap.weatherk.RetrofitCreator.RetrofitServicePhotos
import com.example.aalap.weatherk.RetrofitCreator.RetrofitServiceWeather
import com.example.aalap.weatherk.Utils.App
import com.example.aalap.weatherk.Utils.City
import com.example.aalap.weatherk.Utils.Preference
import com.google.android.gms.location.places.PlacePhotoMetadataResponse
import com.google.android.gms.location.places.Places
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.queryFirst
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class WeatherModel(var presenter: Presenter) {

    var retrofitServiceWeather: RetrofitServiceWeather = App.retrofit()
    var retrofitServicePhotos: RetrofitServicePhotos = App.retrofitPhotos()
    var pref: Preference = App.pref()
    val geoDataClient = Places.getGeoDataClient(App.applicationContext())


    companion object {
        var TAG = "WeatherModel:"
    }

    fun getForecast(latitude: Double, longitude: Double) {

        retrofitServiceWeather.getWeather(latitude, longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: Response<Forecast>? ->
                    if (response!!.isSuccessful) {
                        val forecast = response.body()
                        val timeZone = forecast!!.timezone

                        for (dailyData in forecast.daily.data) {
                            dailyData.timeZone = timeZone
                        }
                        for (hourlyData in forecast.hourly.data) {
                            hourlyData.timeZone = timeZone
                        }

                        presenter.showForecast(forecast)

                    } else {
                        throw Exception(response.errorBody()!!.string())
                    }
                }, { throwable -> presenter.showErrorMsg(throwable.localizedMessage) })

        getPlaceName(latitude, longitude)
    }

    fun getPlaceName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(App.applicationContext(), Locale.getDefault())
        try {

            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val obj = addresses.get(0)
            var add = obj.getAddressLine(0)
            add = add + "\n" + obj.countryName
            add = add + "\n" + obj.countryCode
            add = add + "\n" + obj.adminArea
            add = add + "\n" + obj.postalCode
            add = add + "\n" + obj.subAdminArea
            add = add + "\n" + obj.locality
            add = add + "\n" + obj.subThoroughfare

            presenter.ShowPlaceName(obj.locality)

            Log.v(TAG, "Address:$add")

        } catch (e: IOException) {
            e.printStackTrace()
            presenter.ShowPlaceName("City Name Not found")
        }
    }

    fun placeName(latitude: Double, longitude: Double): String {

        var cityName = "Not found"
        val geocoder = Geocoder(App.applicationContext(), Locale.getDefault())
        try {

            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val obj = addresses.get(0)
            var add = obj.getAddressLine(0)
            cityName = obj.locality
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return cityName
    }

    fun placePhotosCall(latitude: Double, longitude: Double) {

        var latLngConcat = latitude.toString() + "," + longitude.toString()

        Log.d(TAG, "Photos:$latLngConcat")
        retrofitServicePhotos.getResult(latLngConcat, "AIzaSyC-H5AtUPSdpX7ZnCG7YsKeX4vpJQpGni0")
                .flatMap { result ->

                    var placeIds: ArrayList<String> = ArrayList()

                    if (result.isSuccessful) {
                        var resultMain = result.body()!!.string()
                        Log.d(TAG, "Photos: Suc")
                        val mainResult = JSONObject(resultMain)
                        val resultsArray = mainResult.getJSONArray("results")


                        if (resultsArray.length() > 0) {
                            for (i in 0..(resultsArray.length() - 1)) {
                                val result = resultsArray.getJSONObject(i)
                                val placeId = result.getString("place_id")
                                placeIds.add(result.getString("place_id"))

                            }
                        } else {

                        }

                    } else {
                        throw RuntimeException(result.errorBody()!!.string())
                    }

                    Observable.fromIterable(placeIds)
                }.flatMap { placeId: String ->

                    Observable.just(geoDataClient.getPlacePhotos(placeId).addOnCompleteListener({ task ->
                        var total = task.result.photoMetadata.count
                        Log.d(TAG, "Photos: flatmap Listener"+total)
                    }))
                }


//                    placePhotos.addOnCompleteListener({ task ->
//                        val result = task.result
//                        val photoMetadata = result.photoMetadata
//                        val totalPhotos = photoMetadata.count
//
//                        Log.d(TAG, "showPlacePhoto:$placeId$:Photos:$totalPhotos")
//
//                        if (totalPhotos > 0) {
//                            val random = Random()
//                            var randomIndex = 0
//                            if (totalPhotos > 1) {
//                                randomIndex = random.nextInt(totalPhotos - 1)
//                            }
//
//                            geoDataClient.getPhoto(photoMetadata[randomIndex])
//                                    .addOnCompleteListener({ task ->
//                                        val bitmap = task.result.bitmap
//                                        presenter.showPlacePhoto(bitmap)
//                                    })
//                        }
//                    })

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    Log.d(TAG, "Photos: :onNxt")
                },
                        { throwable: Throwable? -> Log.d(TAG, "Photos: Th " + throwable!!.localizedMessage) })
    }



    fun getPlaceBitmap(placeId: String) {
        val geoDataClient = Places.getGeoDataClient(App.applicationContext())

        val placePhotos = geoDataClient.getPlacePhotos(placeId)

        Log.d(TAG, "showPlacePhoto$placeId")

        placePhotos.addOnCompleteListener({ task ->
            val result = task.result
            val photoMetadata = result.photoMetadata
            val totalPhotos = photoMetadata.count

            if (totalPhotos > 0) {
                val random = Random()
                var randomIndex = 0
                if (totalPhotos > 1) {
                    randomIndex = random.nextInt(totalPhotos - 1)
                }

                geoDataClient.getPhoto(photoMetadata[randomIndex])
                        .addOnCompleteListener({ task ->
                            val bitmap = task.result.bitmap
                            presenter.showPlacePhoto(bitmap)
                        })
            } else {
                presenter.showPlacePhoto(null)
            }
        })
    }

    fun openGpsOrSavedLocationWeather() {

        Toast.makeText(App.applicationContext(), "Forecast new way", Toast.LENGTH_SHORT).show()

        val selectedLatitude = pref.getSelectedtLatitude()
        val selectedLongitude = pref.getSelectedLongitude()

        val userLatitude = pref.getUserLatitude()
        val userLongitude = pref.getUserLongitude()

        if (userLatitude != -1.0 && userLongitude != -1.0) {

            getForecast(userLatitude
                    , userLongitude)

            val queryFirst = City().queryFirst {
                equalTo("latitude", userLatitude)
                        .equalTo("longitude", userLongitude)
            }
            if (queryFirst != null) {
                getPlaceBitmap(queryFirst.id)
            }
        }

        //get last opened location
        else if (selectedLatitude != -1.0 && selectedLongitude != -1.0) {
            getForecast(selectedLatitude
                    , selectedLongitude)

            val queryFirst = City().queryFirst {
                equalTo("latitude", selectedLatitude)
                        .equalTo("longitude", selectedLongitude)
            }

            if (queryFirst != null) {
                getPlaceBitmap(queryFirst.id)
            }

        }
        //open GPS.. this case will be for first times only
        else
            presenter.openGpsDialog()
    }

    fun lookupPlacePhotos(placeIds: ArrayList<String>) {
        for (placeId in placeIds) {

            val placePhotos = geoDataClient.getPlacePhotos(placeId)



            placePhotos.addOnCompleteListener({ task ->
                val result = task.result
                val photoMetadata = result.photoMetadata
                val totalPhotos = photoMetadata.count

                Log.d(TAG, "showPlacePhoto:$placeId$:Photos:$totalPhotos")

                if (totalPhotos > 0) {
                    val random = Random()
                    var randomIndex = 0
                    if (totalPhotos > 1) {
                        randomIndex = random.nextInt(totalPhotos - 1)
                    }

                    geoDataClient.getPhoto(photoMetadata[randomIndex])
                            .addOnCompleteListener({ task ->
                                val bitmap = task.result.bitmap
                                presenter.showPlacePhoto(bitmap)
                            })
                    exitProcess(0)
                }
            })
        }
    }
}