package com.example.constaflux2.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.constaflux2.data.EntryId
import com.example.constaflux2.ui.fragment.EntryDescriptionFragment

class EntryDescriptionPageAdapter(
    fragment: Fragment,
    private val entryIds: List<EntryId>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = entryIds.size
    override fun createFragment(position: Int): Fragment =
        EntryDescriptionFragment.createFragment(entryIds[position])
}
