package com.constantin.microflux.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.constantin.microflux.R
import com.constantin.microflux.databinding.FragmentNavigationBottomsheetBinding
import com.constantin.microflux.module.NavigationViewModel
import com.constantin.microflux.module.State
import com.constantin.microflux.module.ViewmodelFactory
import com.constantin.microflux.util.BindingBottomSheetDialogFragment
import javax.inject.Inject

class BottomNavigationDrawerFragment() :
    BindingBottomSheetDialogFragment<FragmentNavigationBottomsheetBinding>(
        FragmentNavigationBottomsheetBinding::inflate
    ) {

    @Inject
    lateinit var viewModelFactory: ViewmodelFactory
    private lateinit var viewmodel: NavigationViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewmodel = viewModelFactory.create(State.Navigation) as NavigationViewModel
    }

    override fun onBindingCreated(
        binding: FragmentNavigationBottomsheetBinding,
        savedInstanceState: Bundle?
    ) {
        binding.run {
            attachCurrentAccount()
            attachAccountSelectionButton()
            attachNavigation()
        }
    }

    private fun FragmentNavigationBottomsheetBinding.attachCurrentAccount() {
        viewmodel.currentAccount.run {
            username.text = userName.name
            userUrl.text = serverUrl.url
        }
    }

    private fun FragmentNavigationBottomsheetBinding.attachAccountSelectionButton() {
        root.setOnClickListener {
            findNavController().navigate(
                BottomNavigationDrawerFragmentDirections.actionBottomNavigationDrawerFragmentToAccountDialog()
            )
        }
    }

    private fun FragmentNavigationBottomsheetBinding.attachNavigation() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            findNavController().navigate(
                when (menuItem.itemId) {
                    R.id.nav_all -> BottomNavigationDrawerFragmentDirections.actionBottomNavigationDrawerFragmentToEntryFragment()
                    R.id.nav_feeds -> BottomNavigationDrawerFragmentDirections.actionBottomNavigationDrawerFragmentToFeedFragment()
                    else -> BottomNavigationDrawerFragmentDirections.actionBottomNavigationDrawerFragmentToCategoryFragment()
                }
            )
            dismiss()
            true
        }
    }
}