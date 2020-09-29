package mx.com.example.bebidas.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment


abstract class CocktailDbAbstractFragment(private val layout: Int): DialogFragment() {
    private var vm: CocktailDbViewModel? = null

    protected val viewModel: CocktailDbViewModel
        get() = vm ?: CocktailDbViewModel.get(super.requireActivity() as AppCompatActivity)
            .also { vm = it }


    override fun onCreateView
    (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        = inflater.inflate(layout, container, false)
}