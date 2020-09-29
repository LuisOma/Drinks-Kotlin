package mx.com.example.bebidas.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.main_activity.*
import mx.com.example.bebidas.R
import mx.com.example.bebidas.ui.utils.PopUpOnSnackbarBehaviour
import mx.com.example.bebidas.ui.utils.SyncState


class CocktailDbMainActivity: AppCompatActivity() {
    private fun notifySyncSucceeded() {
        Snackbar.make(cdltRoot, R.string.sync_succeeded, 2000).show()
    }

    private fun styleWindow() {
        val visibilityFlags =
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        val decorView = super.getWindow().decorView
        decorView.systemUiVisibility = visibilityFlags
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if(visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0)
                decorView.systemUiVisibility = visibilityFlags
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        styleWindow()
        super.setContentView(R.layout.main_activity)

        val vm = CocktailDbViewModel.get(this)
        vm.syncStateLD.observe(this, Observer { state ->
            if(state == SyncState.SUCCEEDED)
                notifySyncSucceeded()
        })

        val params = navHost.view!!.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = PopUpOnSnackbarBehaviour(this, null)
        navHost.view!!.layoutParams = params
    }
}
