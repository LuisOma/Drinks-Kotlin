package mx.com.example.bebidas.data.external.model

import com.google.gson.annotations.SerializedName


class Ingredient(
    @SerializedName("idIngredient")
    val ID: Int,

    @SerializedName("strIngredient")
    val name: String
)