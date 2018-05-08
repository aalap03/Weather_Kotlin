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
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer
import com.google.android.gms.location.places.PlacePhotoMetadataResponse
import com.google.android.gms.location.places.Places
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.queryFirst
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.single.SingleToObservable
import io.reactivex.schedulers.Schedulers
import okhttp3.*
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

            if (obj.locality != null)
                presenter.ShowPlaceName(obj.locality)
            else if (obj.subAdminArea != null)
                presenter.ShowPlaceName(obj.subAdminArea)
            else if (obj.adminArea != null)
                presenter.ShowPlaceName(obj.adminArea)
            else
                presenter.ShowPlaceName(obj.postalCode + obj.countryName)

            Log.v(TAG, "Address:$add")

        } catch (e: IOException) {
            e.printStackTrace()
            presenter.ShowPlaceName("City Name Not found")
        }
    }

    fun placePhotosCall(latitude: Double, longitude: Double) {

        var latLngConcat = latitude.toString() + "," + longitude.toString()

        Log.d(TAG, "Photos:$latLngConcat")
        retrofitServicePhotos.getResult(latLngConcat, "AIzaSyC-H5AtUPSdpX7ZnCG7YsKeX4vpJQpGni0")
                .flatMap { result -> listOfPlaceIds(result) }
                .map { placeId: String -> taskCompleteObservable(placeId) }
                .filter { t: Observable<PlacePhotoMetadataBuffer> -> t.blockingFirst().count > 0 }
                .firstElement()
                .map { task ->
                    task.map { t: PlacePhotoMetadataBuffer ->
                        val random = Random()
                        val total = t.count
                        var randomIndex = random.nextInt(total - 1)

                        Log.d(TAG, "Photos: mapping inside map:$total:random:$randomIndex")
                        geoDataClient.getPhoto(t[randomIndex])
                                .addOnCompleteListener({ task ->
                                    val bitmap = task.result.bitmap
                                    presenter.showPlacePhoto(bitmap)
                                })

                    }.subscribe()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ task ->
                    Log.d(TAG, "Photos: onNxt")
                },
                        { throwable: Throwable? -> Log.d(TAG, "Photos: Th " + throwable!!.localizedMessage) })

    }

    fun taskCompleteObservable(placeId: String): Observable<PlacePhotoMetadataBuffer>? {
        return Observable.create<PlacePhotoMetadataBuffer> { subscriber ->
            geoDataClient.getPlacePhotos(placeId).addOnCompleteListener({ task ->
                subscriber.onNext(task.result.photoMetadata)
            })
        }
    }

    private fun listOfPlaceIds(result: Response<ResponseBody>): Observable<String>? {
        val placeIds: ArrayList<String> = ArrayList()

        if (result.isSuccessful) {
            val resultMain = result.body()!!.string()
            Log.d(TAG, "Photos: Suc")
            val mainResult = JSONObject(resultMain)
            val resultsArray = mainResult.getJSONArray("results")


            if (resultsArray.length() > 0) {
                for (i in 0..(resultsArray.length() - 1)) {
                    placeIds.add(resultsArray.getJSONObject(i).getString("place_id"))
                }
            } else {

            }

        } else {
            throw RuntimeException(result.errorBody()!!.string())
        }

        Log.d(TAG, "Photos: total ids " + placeIds.size)
        return Observable.fromIterable(placeIds)
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
}