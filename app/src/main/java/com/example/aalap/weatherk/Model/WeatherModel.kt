package com.example.aalap.weatherk.Model

import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import com.example.aalap.weatherk.Model.Forecast.Forecast
import com.example.aalap.weatherk.Presenter
import com.example.aalap.weatherk.RetrofitCreator.RetrofitService
import com.example.aalap.weatherk.Utils.App
import com.example.aalap.weatherk.Utils.City
import com.google.android.gms.location.places.Places
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.save
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

class WeatherModel(var presenter: Presenter) {

    var retrofitService:RetrofitService = App.retrofit()

    companion object {
        var TAG = "WeatherModel:"
    }

    fun getForecast(latitude:Double, longitude:Double) {

        retrofitService.getWeather(latitude, longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response: Response<Forecast>? ->
                    if (response!!.isSuccessful) {
                        val forecast = response.body()
                        val timeZone = forecast!!.timezone

                        for (dailyData in forecast.daily.data){
                            dailyData.timeZone = timeZone
                        }
                        for (hourlyData in forecast.hourly.data){
                            hourlyData.timeZone = timeZone
                        }

                        presenter.showForecast(forecast)

                    } else {
                        throw Exception(response.errorBody()!!.string())
                    }
                }, { throwable -> presenter.showErrorMsg(throwable.localizedMessage) })
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

    fun placeName(latitude: Double, longitude: Double):String {

        var cityName = "Not found"
        val geocoder = Geocoder(App.applicationContext(), Locale.getDefault())
        try {

            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val obj = addresses.get(0)
            var add = obj.getAddressLine(0)
            cityName = obj.locality
        }
        catch (e:IOException) {
            e.printStackTrace()
        }

        return cityName
    }

    fun getPlaceInfo(latitude: Double, longitude: Double) {

        val api2 = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+
        "&key="+"AIzaSyC-H5AtUPSdpX7ZnCG7YsKeX4vpJQpGni0"

        Log.d(TAG, "Key:$api2")

        val client = OkHttpClient()
        val request = Request.Builder()
                .url(api2)
                .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                presenter.noPlaceId()
            }

            override fun onResponse(call: Call?, response: okhttp3.Response?) {
                if(response!!.isSuccessful){

                    val result = response.body()!!.string()
                    val mainResult = JSONObject(result)
                    val resultsArray = mainResult.getJSONArray("results")
                    var placeId:String = ""
                    Log.d(TAG, "Main:$result")
                    Log.d(TAG, "Results:$resultsArray")

                    if(resultsArray.length() > 0) {
                        placeId = resultsArray.getJSONObject(0).getString("place_id")
                        Log.d(TAG, "PlaceId:$placeId")
                        presenter.getPlaceId(placeId)
                    } else {
                        Log.d(TAG, "No placeId")
                        presenter.noPlaceId()
                    }

                    City(placeId, latitude, longitude, placeName(latitude, longitude))
                            .save()

                    Log.d(TAG, "RealmCity:"+City().queryAll().size)

                }else{
                    Log.d(TAG, "Er:"+response.code())
                    presenter.noPlaceId()
                }
            }
        })
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
                Log.d(TAG, "Photos: Total " + totalPhotos)
                Log.d(TAG, "Photos: RandomIndex" + randomIndex)
                if (totalPhotos > 1) {
                    randomIndex = random.nextInt(totalPhotos - 1)
                }

                geoDataClient.getPhoto(photoMetadata[randomIndex])
                        .addOnCompleteListener({ task ->
                            val bitmap = task.result.bitmap
                            presenter.showPlacePhoto(bitmap)
                        })
            } else {
                //TODO show default photo
            }
        })
    }
}