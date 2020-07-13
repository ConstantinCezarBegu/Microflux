package com.constantin.microflux.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.constantin.microflux.R
import com.constantin.microflux.data.SettingsAllowImagePreview
import com.constantin.microflux.data.SettingsTheme
import com.constantin.microflux.databinding.FragmentSettingsBinding
import com.constantin.microflux.module.SettingsViewModel
import com.constantin.microflux.module.State
import com.constantin.microflux.module.ViewmodelFactory
import com.constantin.microflux.util.BindingFragment
import com.constantin.microflux.util.toAndroidDelegate
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsFragment() : BindingFragment<FragmentSettingsBinding>(
    FragmentSettingsBinding::inflate
) {

    @Inject
    lateinit var viewModelFactory: ViewmodelFactory
    private lateinit var viewmodel: SettingsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewmodel = viewModelFactory.create(State.Settings) as SettingsViewModel
    }

    override fun onBindingCreated(binding: FragmentSettingsBinding, savedInstanceState: Bundle?) {
        binding.run {
            attachAccount()
            attachSettings()
            attachAppBar()
            attachLogoutButton()
            attachAllowImagePreviewButton()
            attachThemeSpinner()
        }
    }

    private fun FragmentSettingsBinding.attachAccount() {
        viewmodel.user.run {
            settingsUsername.text = userName.name
            settingsUserUrl.text = serverUrl.url
        }
    }

    private fun FragmentSettingsBinding.attachSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            val settings = viewmodel.settings.await()
            spinnerThemeMode.setSelection(settings.settingsTheme.theme)
            allowImagePreviewButton.isChecked = settings.settingsAllowImagePreview.allowImagePreview
        }
    }

    private fun FragmentSettingsBinding.attachAppBar() {
        toolBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun FragmentSettingsBinding.attachLogoutButton() {
        logoutButton.setOnClickListener {
            showLogoutAlertDialog()
        }
    }

    private fun FragmentSettingsBinding.attachAllowImagePreviewButton(){
        allowImagePreviewButton.setOnCheckedChangeListener { _, isChecked ->
            viewmodel.updateAllowImagePreview(
                settingsAllowImagePreview = SettingsAllowImagePreview(isChecked)
            )
        }
    }

    private fun FragmentSettingsBinding.attachThemeSpinner() {
        spinnerThemeMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewmodel.updateSettingsTheme(SettingsTheme(position))
                AppCompatDelegate.setDefaultNightMode(SettingsTheme(position).toAndroidDelegate())
            }
        }
    }

    private fun showLogoutAlertDialog() {
        AlertDialog.Builder(requireContext()).run {
            setMessage(R.string.logout_question)
            setCancelable(true)
            setPositiveButton(android.R.string.ok) { _, _ ->
                viewmodel.logout().invokeOnCompletion {
                    findNavController().navigate(
                        SettingsFragmentDirections.actionSettingsFragmentToEntryFragment()
                    )
                }
            }
            setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
            create()
            show()
        }
    }
}