package mx.com.example.bebidas.data.repository

import android.annotation.SuppressLint
import android.util.Log
import mx.com.example.bebidas.data.external.model.Drink
import mx.com.example.bebidas.data.external.model.Ingredient
import java.util.Calendar
import java.text.SimpleDateFormat


object RepoTransformations {
    @SuppressLint("SimpleDateFormat")
    private val DRINK_DATE_MODIFIED_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")


    fun drinkPojoToEntity(pojo: Drink)
        = mx.com.example.bebidas.data.internal.db.model.Drink(
        pojo.ID,
        pojo.name,
        pojo.thumbURL,
        pojo.dateModified?.let {
            try {
                val parsedTime = DRINK_DATE_MODIFIED_FORMAT.parse(it)
                Calendar.getInstance().apply { time = parsedTime }
            } catch(exc: Exception) {
                Log.w("RepoTransformations", "Date not parsed: ${exc.message}")
                null
            }
        },
        pojo.recipe
    )

    fun ingredientPojoToEntity(pojo: Ingredient)
        = mx.com.example.bebidas.data.internal.db.model.Ingredient(pojo.ID, pojo.name)
}