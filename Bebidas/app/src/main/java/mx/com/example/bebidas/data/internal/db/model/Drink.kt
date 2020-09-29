package mx.com.example.bebidas.data.internal.db.model

import java.util.Calendar
import androidx.room.*


@Entity(tableName = "Drinks")
class Drink (
    @PrimaryKey
    @ColumnInfo(name="id")
    val ID: Int,

    @ColumnInfo(index=true)
    val name: String,

    @ColumnInfo(name="thumb")
    val thumbURL: String?,

    val dateModified: Calendar?,

    val recipe: String?
)