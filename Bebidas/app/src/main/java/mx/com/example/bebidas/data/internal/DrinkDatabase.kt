package mx.com.example.bebidas.data.internal

import android.content.Context
import androidx.room.*
import mx.com.example.bebidas.data.internal.db.dao.DrinkDAO
import mx.com.example.bebidas.data.internal.db.dao.IngredientDAO
import mx.com.example.bebidas.data.internal.db.model.Drink
import mx.com.example.bebidas.data.internal.db.model.Ingredient
import mx.com.example.bebidas.data.internal.db.model.IngredientLot


@Database(
    entities = [
        Drink::class,
        Ingredient::class,
        IngredientLot::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DrinkTypeConverters::class)
abstract class DrinkDatabase: RoomDatabase() {
    abstract val drinkDAO: DrinkDAO
    abstract val ingredientDAO: IngredientDAO
    
    
    companion object {
        private var instance: DrinkDatabase? = null

        fun get(appContext: Context)
            = instance ?: synchronized(this) {
                instance ?: Room
                    .databaseBuilder(appContext, DrinkDatabase::class.java, "cocktail")
                    .build()
                    .also { instance = it }
            }
    }
}