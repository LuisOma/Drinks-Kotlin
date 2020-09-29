package mx.com.example.bebidas.ui.recipes

import android.util.DisplayMetrics
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import mx.com.example.bebidas.R


internal object RecipeToolbarUtils {
    private val ViewGroup.coordinatorParams: CoordinatorLayout.LayoutParams
        get() = this.layoutParams as CoordinatorLayout.LayoutParams?
            ?: CoordinatorLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)


    fun setMaximumHeight(appbar: AppBarLayout, display: Display) {
        val displayHeight = DisplayMetrics().also {
            display.getMetrics(it)
        }.heightPixels
        val params = appbar.coordinatorParams.apply {
            height = displayHeight / 2
        }
    }

    fun setupOffsetListener(appbar: AppBarLayout, content: ViewGroup) {
        fun setContentMargin(margin: Int) {
            content.layoutParams = content.coordinatorParams.apply {
                topMargin = margin
            }
        }

        val res = appbar.context.resources
        val baseHeight = res.getDimensionPixelSize(R.dimen.min_recipe_toolbar_height)
        appbar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { abLayout, offset ->
                setContentMargin(baseHeight + abLayout.totalScrollRange + offset)
            }
        )
    }
}