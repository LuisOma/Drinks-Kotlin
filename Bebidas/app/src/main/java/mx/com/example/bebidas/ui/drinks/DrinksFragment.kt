package mx.com.example.bebidas.ui.drinks

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Surface
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.drinks_fragment.*
import mx.com.example.bebidas.R
import mx.com.example.bebidas.ui.CocktailDbAbstractFragment
import mx.com.example.bebidas.ui.adapters.DrinksAdapter
import mx.com.example.bebidas.ui.utils.DrinksList
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.com.example.bebidas.ui.CocktailDbViewModel
import mx.com.example.bebidas.ui.utils.DrinkThumb
import mx.com.example.bebidas.ui.utils.SearchState
import mx.com.example.bebidas.ui.utils.SyncState


class DrinksFragment: CocktailDbAbstractFragment(R.layout.drinks_fragment), View.OnClickListener {
    companion object {
        private var listPosition = -1

        private fun trySaveListPosition(list: RecyclerView) {
            val layman = list.layoutManager as GridLayoutManager?
            layman?.findFirstVisibleItemPosition()?.also {
                listPosition = it
            }
        }
    }

    private fun displayDrinks(drinks: DrinksList) {
        trySaveListPosition(recvDrinks)

        val newAdapter =
            DrinksAdapter(super.requireContext(), drinks, findNavController() ) { id ->
                super.viewModel.requestThumb(id)
            }

        val layman = recvDrinks.layoutManager as GridLayoutManager?
        recvDrinks.layoutManager = layman ?: run {
            val metrs = DisplayMetrics()
            super.requireActivity().windowManager.defaultDisplay.getMetrics(metrs)
            val cardHeight = super.getResources()
                .getDimensionPixelSize(R.dimen.cocktail_card_height)
            GridLayoutManager(super.requireContext(), metrs.widthPixels / cardHeight)
        }
        recvDrinks.swapAdapter(newAdapter, true)

        if(listPosition > 0)
            recvDrinks.layoutManager?.scrollToPosition(listPosition)
    }

    private fun applyDrinkThumb(thumb: DrinkThumb) {
        val adapter = recvDrinks.adapter as DrinksAdapter? ?: return
        recvDrinks.post {
            adapter.addThumb(thumb)
        }
    }

    private fun notifySyncFailed() {
        AlertDialog.Builder(super.requireContext())
            .setTitle(R.string.error)
            .setMessage(R.string.sync_failed)
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showInitialLoadNotification() {
        lifecycleScope.launch(Dispatchers.Main) {
            FirstLaunchNotifUtils.showLoading(super.requireActivity())
        }
    }

    private fun modifyInitialLoadNotification() {
        if(!FirstLaunchNotifUtils.needNotifyLoadFinished)
            return
        lifecycleScope.launch(Dispatchers.Main) {
            FirstLaunchNotifUtils.modifyLoadFinished(super.requireActivity())
        }
    }

    private fun updateStateDependentUi(vm: CocktailDbViewModel) {
        prBarCentral.isVisible =
            (vm.currentSyncState == SyncState.IN_PROGRESS && vm.isLaunchInitial) ||
            (vm.currentDrinksSearchState == SearchState.IN_PROGRESS)
        tvNothingFound.isVisible = (vm.currentDrinksSearchState == SearchState.ACTIVE) &&
            (recvDrinks.adapter?.itemCount == 0)
        prBarSync.isVisible = (vm.currentSyncState == SyncState.IN_PROGRESS)
        fabSync.isVisible = !prBarSync.isVisible
        tvDataWillArrive.isVisible = (!prBarCentral.isVisible) &&
            (!tvNothingFound.isVisible) && (vm.totalDrinkCount == 0)
    }

    private fun setSearchViewDimensions(width: Int, height: Int) {
        vSearch.layoutParams = (vSearch.layoutParams as Toolbar.LayoutParams?)?.apply {
            this.width = width
            this.height = height
        } ?: Toolbar.LayoutParams(width, height)
    }

    private fun setSeachViewListeners() {
        vSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                this@DrinksFragment.viewModel.searchDrinks(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
        })

        vSearch.setOnSearchClickListener {
            setSearchViewDimensions(MATCH_PARENT, WRAP_CONTENT)
        }

        vSearch.setOnCloseListener {
            setSearchViewDimensions(WRAP_CONTENT, WRAP_CONTENT)
            super.viewModel.stopSearchDrinks()
            false
        }
    }

    private fun setSyncFabMargins(bottom: Int, right: Int) {
        val params = (fabSync.layoutParams as CoordinatorLayout.LayoutParams?)
            ?: CoordinatorLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        params.bottomMargin = bottom
        params.rightMargin = right
        fabSync.layoutParams = params
    }

    private fun layoutSyncFab() {
        val small = super.getResources().getDimensionPixelSize(R.dimen.fab_small_margin)
        val big   = super.getResources().getDimensionPixelSize(R.dimen.fab_big_margin)
        when(super.requireActivity().windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0   -> setSyncFabMargins(bottom = big,   right = small)
            Surface.ROTATION_90  -> setSyncFabMargins(bottom = small, right = big)
            Surface.ROTATION_180 -> setSyncFabMargins(bottom = big,   right = small)
            Surface.ROTATION_270 -> setSyncFabMargins(bottom = small, right = small)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm = super.viewModel; val owner = super.getViewLifecycleOwner()

        layoutSyncFab()
        updateStateDependentUi(vm)
        if(vm.isLaunchInitial)
            showInitialLoadNotification()

        vm.drinksListLD.observe(owner, Observer { drinks ->
            displayDrinks(drinks)
            updateStateDependentUi(vm)
            if(vm.isLaunchInitial && vm.totalDrinkCount != 0) {
                modifyInitialLoadNotification()
                vm.unsetInitialLaunch()
            }
        })

        vm.drinkThumbLD.observe(owner, Observer { thumb ->
            applyDrinkThumb(thumb)
        })

        fabSync.setOnClickListener {
            super.viewModel.forceSync()
        }

        vm.syncStateLD.observe(owner, Observer { newState ->
            updateStateDependentUi(vm)
            if(newState == SyncState.FAILED) {
                notifySyncFailed()
                vm.clearSyncState()
            }
        })

        setSeachViewListeners()
        vm.drinksSearchStateLD.observe(super.getViewLifecycleOwner(), Observer { _ ->
            updateStateDependentUi(vm)
        })

        fabScroll.setOnClickListener(this)

        recvDrinks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    fabScroll.show()
                } else {
                    fabScroll.hide()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        RequestThumbsCallbackUtils.register(
            super.requireActivity() as AppCompatActivity, recvDrinks )
    }

    override fun onStop() {
        RequestThumbsCallbackUtils.unregister(super.requireContext())
        trySaveListPosition(recvDrinks)
        super.onStop()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fabScroll -> {
                recvDrinks.post {
                    recvDrinks.smoothScrollToPosition(0)
                }
            }
        }
    }
}