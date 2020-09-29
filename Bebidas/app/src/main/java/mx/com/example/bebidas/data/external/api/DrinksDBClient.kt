package mx.com.example.bebidas.data.external.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DrinksDBClient {
    private var instance: Retrofit? = null

    val retrofit
        get() = instance ?: synchronized(this) {
            instance ?: Retrofit.Builder()
                .baseUrl("https://www.thecocktaildb.com/api/json/v1/1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .also { instance = it }
        }
}