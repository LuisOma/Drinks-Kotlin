package mx.com.example.bebidas.data.internal.db.model

import androidx.room.Embedded


class IngredientRecipe(
    @Embedded(prefix = "lot_")
    val lot: IngredientLot,

    @Embedded(prefix = "ingredient_")
    val ingredient: Ingredient
)