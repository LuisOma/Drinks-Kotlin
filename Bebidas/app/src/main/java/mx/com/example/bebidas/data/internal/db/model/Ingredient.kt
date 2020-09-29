package mx.com.example.bebidas.data.internal.db.model

import androidx.room.*


@Entity(tableName = "Ingredients")
class Ingredient(
    @PrimaryKey @ColumnInfo(name = "id")
    val ID: Int,

    @ColumnInfo(index = true)
    val name: String
)