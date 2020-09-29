package mx.com.example.bebidas.data.repository

import android.content.Context
import mx.com.example.bebidas.data.internal.DrinkDatabase


abstract class CocktailAbstractRepository(protected val appContext: Context) {
    protected val drinkDAO
        get() = DrinkDatabase.get(appContext).drinkDAO

    protected val ingredientDAO
        get() = DrinkDatabase.get(appContext).ingredientDAO
}