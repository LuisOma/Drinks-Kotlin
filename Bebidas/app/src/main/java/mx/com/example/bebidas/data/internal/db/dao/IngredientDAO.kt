package mx.com.example.bebidas.data.internal.db.dao

import androidx.room.*
import mx.com.example.bebidas.data.internal.db.model.Ingredient
import mx.com.example.bebidas.data.internal.db.model.IngredientRecipe
import mx.com.example.bebidas.data.internal.db.model.IngredientLot


@Dao
interface IngredientDAO {
    @Query("select name from Ingredients")
    fun getNames(): List<String>

    @Query("select * from Ingredients where lower(name)=lower(:name)")
    fun getByName(name: String): List<Ingredient>

    @Query("select :drinkID as lot_drink, " +
           "IngredientLots.ingredient as lot_ingredient, " +
           "IngredientLots.measure as lot_measure, " +
           "Ingredients.id as ingredient_id, " +
           "Ingredients.name as ingredient_name " +
           "from Ingredients inner join IngredientLots on Ingredients.id=IngredientLots.ingredient " +
           "where IngredientLots.drink=:drinkID" )
    fun getIngredientsInRecipe(drinkID: Int): List<IngredientRecipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(ingredients: List<Ingredient>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateILots(ilots: List<IngredientLot>)
}