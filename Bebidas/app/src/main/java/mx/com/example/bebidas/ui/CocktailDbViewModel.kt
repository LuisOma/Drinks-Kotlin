package mx.com.example.bebidas.ui

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*

import kotlinx.coroutines.*
import mx.com.example.bebidas.data.internal.db.model.DrinkHeader
import mx.com.example.bebidas.data.repository.CocktailRepository
import mx.com.example.bebidas.ui.utils.*


class CocktailDbViewModel(app: Application): AndroidViewModel(app) {
    companion object {
        fun get(owner: ViewModelStoreOwner, app: Application)
            = ViewModelProvider(owner, ViewModelProvider.AndroidViewModelFactory(app))
                .get(CocktailDbViewModel::class.java)

        fun get(activity: AppCompatActivity)
            = get(activity, activity.application)
    }

    private var lastSyncMillis = 0L
    private val SYNC_STATE_LOCK = Any()

    private val syncStateMLD = MutableLiveData<SyncState>()
    val syncStateLD: LiveData<SyncState>
        get() = syncStateMLD

    val currentSyncState
        get() = syncStateMLD.value ?: SyncState.NO_SYNC


    private fun setSyncState(state: SyncState): Boolean {
        synchronized(SYNC_STATE_LOCK) {
            if(state == SyncState.IN_PROGRESS && syncStateMLD.value == SyncState.IN_PROGRESS)
                return false
            syncStateMLD.postValue(state)
            return true
        }
    }

    private fun sync(minIntervalMillis: Long, notifyAboutResult: Boolean = false): Boolean {
        val now = System.currentTimeMillis()
        if(now - lastSyncMillis < minIntervalMillis ||
            !setSyncState(SyncState.IN_PROGRESS) )
            return false
        lastSyncMillis = now

        try {
            cocktailRepo.sync()
            setSyncState(
                if(notifyAboutResult) SyncState.SUCCEEDED else SyncState.NO_SYNC
            )
        } catch(exc: Exception) {
            Log.w("Sync", "${exc.javaClass.name}: ${exc.message}")
            setSyncState(
                if(notifyAboutResult) SyncState.FAILED else SyncState.NO_SYNC
            )
        }

        return true
    }

    fun forceSync() {
        viewModelScope.launch(Dispatchers.IO) {
            sync(0L, notifyAboutResult = true)
        }
    }

    fun clearSyncState() {
        setSyncState(SyncState.NO_SYNC)
    }

    val isLaunchInitial: Boolean
        get() = CocktailDbAppLoads.isLaunchInitial(this.app)

    fun unsetInitialLaunch()
        = CocktailDbAppLoads.unsetInitialLaunch(this.app)

    private val app: Application
        get() = super.getApplication()

    private val cocktailRepo = CocktailRepository(this.app)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            while(true) {
                sync(180000L)
                delay(4000L)
            }
        }
    }

    private var allDrinks: List<DrinkHeader>? = null
    private val DRINKS_SEARCH_LOCK = Any()

    private val drinksSearchQueryMLD = MutableLiveData<CharSequence>().apply {
        value = null
    }

    private val drinksSearchStateMLD = MutableLiveData<SearchState>().apply {
        value = SearchState.INACTIVE
    }

    private suspend fun mapAllDrinksToDrinksList(): DrinksList {
        val drinks = allDrinks ?: return DrinksList(listOf())

        fun createList(): DrinksList {
            val query = drinksSearchQueryMLD.value
            if(query == null) {
                drinksSearchStateMLD.postValue(SearchState.INACTIVE)
                return DrinksList(drinks)
            }
            drinksSearchStateMLD.postValue(SearchState.IN_PROGRESS)
            val searchResult = DrinksList( cocktailRepo.filterDrinks(drinks, query) )
            drinksSearchStateMLD.postValue(SearchState.ACTIVE)
            return searchResult
        }

        return withContext(Dispatchers.Default) {
            synchronized(DRINKS_SEARCH_LOCK) {
                createList()
            }
        }
    }

    private val drinksListMLD = MediatorLiveData<DrinksList>().apply {
        addSource(cocktailRepo.drinkHeadersLD) { headers ->
            viewModelScope.launch(Dispatchers.Main) {
                allDrinks = headers
                value = mapAllDrinksToDrinksList()
            }
        }
        addSource(drinksSearchQueryMLD) { query ->
            viewModelScope.launch(Dispatchers.Main) {
                value = mapAllDrinksToDrinksList()
            }
        }
    }

    val totalDrinkCount
        get() = allDrinks?.size ?: 0

    val drinksSearchStateLD
        get() = drinksSearchStateMLD

    val currentDrinksSearchState
        get() = drinksSearchStateMLD.value ?: SearchState.INACTIVE

    fun searchDrinks(query: CharSequence) {
        drinksSearchQueryMLD.value = query
    }

    fun stopSearchDrinks() {
        drinksSearchQueryMLD.value = null
    }

    val drinksListLD: LiveData<DrinksList>
        get() = drinksListMLD


    private val drinkThumbMLD = MutableLiveData<DrinkThumb>()

    val drinkThumbLD: LiveData<DrinkThumb>
        get() = drinkThumbMLD

    fun requestThumb(id: Int) {
        drinkThumbMLD.value?.takeIf {
            it.drinkID == id
        }?.also {
            drinkThumbMLD.value = it
        } ?: viewModelScope.launch(Dispatchers.Main) {
            val url = drinksListMLD.value?.byID(id)?.thumbURL ?: return@launch
            withContext(Dispatchers.IO) {
                cocktailRepo.getThumb(url)
            }?.also {
                drinkThumbMLD.value = DrinkThumb(id, it)
            }
        }
    }

    private val recipeMLD = MutableLiveData<ItemizedRecipe>()

    suspend fun getRecipe(drinkID: Int)
        = withContext(Dispatchers.IO) {
            ItemizedRecipe.fromEntity(cocktailRepo.getRecipe(drinkID))
        }
}