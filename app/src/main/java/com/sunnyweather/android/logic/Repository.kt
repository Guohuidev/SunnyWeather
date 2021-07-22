package com.sunnyweather.android.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData

import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.PlaceResponse
import com.sunnyweather.android.logic.network.ServiceCreator

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object Repository {

    fun searchPlaces(query: String): LiveData<Result<List<Place>>> {
        return fire(Dispatchers.Main){
            val placeResponse: PlaceResponse = ServiceCreator.placeService.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is${placeResponse.status}"))
            }
        }
    }

    /**
     * 统一异常处理入口
     */
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>): LiveData<Result<T>> {
        return liveData(context) {
            val result: Result<T> = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
    }

}