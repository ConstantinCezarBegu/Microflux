package com.example.constaflux2.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.constaflux2.R
import com.example.constaflux2.data.EntryId
import com.example.constaflux2.databinding.FragmentEntryDescriptionPagerBinding
import com.example.constaflux2.ui.adapters.EntryDescriptionPageAdapter
import com.example.constaflux2.util.BindingFragment

class EntryDescriptionPagerFragment() : BindingFragment<FragmentEntryDescriptionPagerBinding>(
    FragmentEntryDescriptionPagerBinding::inflate
) {

    private val args: EntryDescriptionPagerFragmentArgs by navArgs()
    private lateinit var entryIds: List<EntryId>
    private var selectedEntryId = EntryId.NO_ENTRY


    override fun onAttach(context: Context) {
        super.onAttach(context)
        entryIds = args.entryIds.map { EntryId(it) }
        selectedEntryId = args.selectedEntryId.let(::EntryId)
    }

    override fun onBindingCreated(
        binding: FragmentEntryDescriptionPagerBinding,
        savedInstanceState: Bundle?
    ) {
        binding.run {
            setAppBar()
            entriesPager()
        }
    }

    private fun FragmentEntryDescriptionPagerBinding.setAppBar() {
        toolBar.run {
            inflateMenu(R.menu.menu_entry_description)
            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun FragmentEntryDescriptionPagerBinding.entriesPager() {
        entryDescriptionViewPager.run {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = EntryDescriptionPageAdapter(
                fragment = this@EntryDescriptionPagerFragment,
                entryIds = entryIds.toList()
            )
            setCurrentItem(entryIds.indexOf(selectedEntryId), false)
        }
    }
}

