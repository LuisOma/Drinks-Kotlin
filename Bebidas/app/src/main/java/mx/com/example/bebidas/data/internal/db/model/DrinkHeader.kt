package mx.com.example.bebidas.data.internal.db.model

import androidx.room.*


class DrinkHeader(
    @ColumnInfo(name="id")
    val ID: Int,

    @ColumnInfo(index = true)
    val name: String,

    @ColumnInfo(name="thumb")
    val thumbURL: String?
)