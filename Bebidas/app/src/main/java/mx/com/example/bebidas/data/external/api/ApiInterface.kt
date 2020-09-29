package mx.com.example.bebidas.data.external.api

import mx.com.example.bebidas.data.external.model.Drinks
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("search.php")
    fun search(@Query("s") query: String): Call<Drinks>

    @GET("search.php")
    fun searchByFirstLetter(@Query("f") letter: String): Call<Drinks>
}