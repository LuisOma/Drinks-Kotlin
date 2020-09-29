package mx.com.example.bebidas.data.external.api

import mx.com.example.bebidas.data.external.model.Ingredients
import retrofit2.Call
import retrofit2.http.*


interface IngredientService {
    @GET("search.php")
    fun search(@Query("i") query: String): Call<Ingredients>
}