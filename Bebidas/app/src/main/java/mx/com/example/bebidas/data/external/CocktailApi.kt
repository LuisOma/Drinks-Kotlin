package mx.com.example.bebidas.data.external

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import mx.com.example.bebidas.data.external.api.DrinkService
import mx.com.example.bebidas.data.external.api.DrinksDBClient
import mx.com.example.bebidas.data.external.api.IngredientService
import mx.com.example.bebidas.data.external.model.Drinks
import mx.com.example.bebidas.data.external.model.Ingredients
import java.net.URL


object CocktailApi {
    private val drinkService
        get() = DrinksDBClient.retrofit.create(DrinkService::class.java)

    private val ingredientService
        get() = DrinksDBClient.retrofit.create(IngredientService::class.java)


    fun searchDrinks(query: String): Drinks?
        = this.drinkService.search(query).execute().body()

    fun searchDrinksByFirstLetter(letter: Char): Drinks?
        = this.drinkService.searchByFirstLetter(letter.toString()).execute().body()

    fun searchIngredients(query: String): Ingredients?
        = this.ingredientService.search(query).execute().body()

    fun getDrinkThumb(host: String): Bitmap? {
        // Retrofit doesn't help much at fetching binary data, so use this legacy approach.
        return try {
            URL(host).openConnection().getInputStream().use { instream ->
                BitmapFactory.decodeStream(instream)
            }
        } catch(exc: Exception) {
            null
        }
    }
}