package com.constantin.microflux.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.constantin.microflux.data.EntryId
import com.constantin.microflux.ui.fragment.EntryDescriptionFragment

class EntryDescriptionPageAdapter(
    fragment: Fragment,
    private val entryIds: List<EntryId>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = entryIds.size
    override fun createFragment(position: Int): Fragment =
        EntryDescriptionFragment.createFragment(entryIds[position])
}
