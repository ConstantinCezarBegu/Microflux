package com.example.constaflux2.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.constaflux2.R
import com.example.constaflux2.data.Result
import com.example.constaflux2.databinding.FragmentListContentBinding
import com.example.constaflux2.module.CategoryViewModel
import com.example.constaflux2.module.State
import com.example.constaflux2.module.ViewmodelFactory
import com.example.constaflux2.ui.adapters.CategoryListRecyclerViewAdapter
import com.example.constaflux2.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class CategoryFragment() : BindingFragment<FragmentListContentBinding>(
    FragmentListContentBinding::inflate
) {

    @Inject
    lateinit var viewModelFactory: ViewmodelFactory
    private lateinit var viewmodel: CategoryViewModel

    private val eventSnackbar = EventSnackbar()
    private lateinit var recyclerViewAdapter: CategoryListRecyclerViewAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewmodel = viewModelFactory.create(State.Category) as CategoryViewModel
    }

    override fun onBindingCreated(
        binding: FragmentListContentBinding,
        savedInstanceState: Bundle?
    ) {
        binding.run {
            attachEmptyState()
            attachRecyclerView()
            observeFetchFeedsErrors()
            attachAppBar()
            attachRefresh()
        }
    }

    private fun FragmentListContentBinding.attachEmptyState() {
        emptyStateTextTitle.text =
            requireContext().getString(R.string.empty_state_title_category.toAndroidString())
        emptyStateTextSubtitle.text =
            requireContext().getString(R.string.empty_state_subtitle_category.toAndroidString())
    }

    private fun FragmentListContentBinding.attachRecyclerView() {
        contentList.disableAnimations()

        recyclerViewAdapter = CategoryListRecyclerViewAdapter(
            itemClickCallback = { categoryId, _, _ ->
                findNavController().navigate(
                    CategoryFragmentDirections.actionCategoryFragmentToFeedFragment(
                        categoryId
                    )
                )
            },
            itemLongClickCallback = { categoryId, _, _ ->
                findNavController().navigate(
                    CategoryFragmentDirections.actionCategoryFragmentToCategoryDialog(categoryId)
                )
            }
        ).also {
            it.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            contentList.adapter = it
        }

        viewmodel.categories.onEach { category ->
            emptyStateContainer.isGone = category.isNotEmpty()
            recyclerViewAdapter.submitList(category)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        contentList.layoutManager =
            StaggeredGridLayoutManager(
                when (resources.configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> 2
                    Configuration.ORIENTATION_LANDSCAPE -> 3
                    else -> 2
                }
                , StaggeredGridLayoutManager.VERTICAL
            )
    }

    private fun FragmentListContentBinding.observeFetchFeedsErrors() {
        viewmodel.fetchCategoryProgression.onEach { result ->
            onRefresh(result = result)
            onError(result = result, eventSnackbar = eventSnackbar)
            onInvalidCredentials(result = result)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun FragmentListContentBinding.attachAppBar() {
        bottomAppBar.run {
            visibility = View.VISIBLE
            inflateMenu(R.menu.menu_list_category_feed)
            setNavigationOnClickListener {
                BottomNavigationDrawerFragment().also {
                    findNavController().navigate(R.id.bottomNavigationDrawerFragment)
                }
            }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.addMenuItem -> {
                        findNavController().navigate(
                            CategoryFragmentDirections.actionCategoryFragmentToCategoryDialog()
                        )
                    }
                }
                true
            }
            setOnClickListener {
                contentList.scrollToPosition(0)
            }
        }
    }

    private fun FragmentListContentBinding.attachRefresh() {
        contentRefresh.setOnRefreshListener {
            viewmodel.fetchCategory()
        }
    }

    private fun onInvalidCredentials(result: Result<Unit>) {
        if (result.isAccountError()) {
            findNavController().navigate(
                CategoryFragmentDirections.actionCategoryFragmentToAccountFragment(
                    serverId = viewmodel.currentAccount.serverId.id,
                    userId = viewmodel.currentAccount.userId.id,
                    firstTimeLaunch = true
                )
            )
        }
    }

}