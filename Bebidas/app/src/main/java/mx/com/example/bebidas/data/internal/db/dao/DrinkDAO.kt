package mx.com.example.bebidas.data.internal.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import mx.com.example.bebidas.data.internal.db.model.Drink
import mx.com.example.bebidas.data.internal.db.model.DrinkHeader


@Dao
interface DrinkDAO {
    @Query("select count(*) from Drinks")
    fun count(): Int

    @Query("select id, name, thumb from Drinks order by dateModified desc")
    fun getHeadersLD(): LiveData< List<DrinkHeader> >

    @Query("select * from Drinks where id=:id")
    fun get(id: Int): Drink

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(drinks: List<Drink>)

    @Delete
    fun delete(drinks: List<Drink>): Int
}