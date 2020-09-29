package mx.com.example.bebidas.data.internal.db.model

import androidx.room.*


@Entity(
    tableName="IngredientLots",
    primaryKeys = [ "drink", "ingredient" ]
) class IngredientLot(
    @ColumnInfo(name="drink")
    val drinkID: Int,

    @ColumnInfo(name="ingredient")
    val ingredientID: Int,

    val measure: String
)