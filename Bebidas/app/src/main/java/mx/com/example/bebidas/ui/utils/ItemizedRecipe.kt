package mx.com.example.bebidas.ui.utils

import androidx.core.text.HtmlCompat
import mx.com.example.bebidas.data.internal.db.model.Drink
import mx.com.example.bebidas.data.internal.db.model.Recipe
import java.text.DateFormat
import java.util.*


class ItemizedRecipe private constructor(
    val drink: Drink,
    val ingredientsList: CharSequence?,
    val dateModified: CharSequence?
) {
    companion object {
        private val LISTITEM_MARK = HtmlCompat.fromHtml("&#8226;", 0)

        fun fromEntity(entity: Recipe): ItemizedRecipe {
            if(entity.ingredients.isEmpty())
                return ItemizedRecipe(entity.drink, null, null)

            val sb = StringBuilder(16 * entity.ingredients.size)
            for(pair in entity.ingredients) {
                if(sb.isNotEmpty())
                    sb.append(System.lineSeparator())
                sb.append("$LISTITEM_MARK ${pair.ingredient.name}: ${pair.lot.measure}")
            }

            val date = entity.drink.dateModified?.time
            val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
            return ItemizedRecipe(entity.drink, sb, date?.let { dateFormat.format(it) })
        }
    }
}