package com.constantin.microflux.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.ImageLoader
import com.constantin.microflux.R
import com.constantin.microflux.data.CategoryId
import com.constantin.microflux.data.Result
import com.constantin.microflux.databinding.FragmentListContentBinding
import com.constantin.microflux.module.FeedViewModel
import com.constantin.microflux.module.State
import com.constantin.microflux.module.ViewmodelFactory
import com.constantin.microflux.ui.adapters.FeedListRecyclerViewAdapter
import com.constantin.microflux.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class FeedFragment() : BindingFragment<FragmentListContentBinding>(
    FragmentListContentBinding::inflate
) {

    @Inject
    lateinit var viewModelFactory: ViewmodelFactory
    private lateinit var viewmodel: FeedViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    private val args: FeedFragmentArgs by navArgs()
    private var categoryId = CategoryId.NO_CATEGORY

    private val eventSnackbar = EventSnackbar()
    private lateinit var recyclerViewAdapter: FeedListRecyclerViewAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        categoryId = args.categoryId.let(::CategoryId)
        viewmodel =
            if (categoryId == CategoryId.NO_CATEGORY) viewModelFactory.create(State.Feed) as FeedViewModel
            else viewModelFactory.create(State.CategoryFeeds(categoryId)) as FeedViewModel
    }

    override fun onBindingCreated(
        binding: FragmentListContentBinding, savedInstanceState: Bundle?
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
            requireContext().getString(R.string.empty_state_title_feed.toAndroidString())
        emptyStateTextSubtitle.text =
            requireContext().getString(R.string.empty_state_subtitle_feed.toAndroidString())
    }

    private fun FragmentListContentBinding.attachRecyclerView() {
        contentList.disableAnimations()

        recyclerViewAdapter = FeedListRecyclerViewAdapter(
            imageLoader = imageLoader,
            itemClickCallback = { feedId, _, _ ->
                findNavController().navigate(
                    FeedFragmentDirections.actionFeedFragmentToEntryFragment(
                        feedId
                    )
                )
            },
            itemLongClickCallback = { feedId, _, _ ->
                findNavController().navigate(
                    FeedFragmentDirections.actionFeedFragmentToFeedDialog(
                        feedId = feedId,
                        categoryId = categoryId.id
                    )
                )
            }
        ).also {
            it.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            contentList.adapter = it
        }

        viewmodel.feeds.onEach { feed ->
            emptyStateContainer.isGone = feed.isNotEmpty()
            recyclerViewAdapter.submitList(feed)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        contentList.layoutManager = StaggeredGridLayoutManager(
            when (resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 2
                Configuration.ORIENTATION_LANDSCAPE -> 3
                else -> 2
            }
            , StaggeredGridLayoutManager.VERTICAL
        )
    }

    private fun FragmentListContentBinding.observeFetchFeedsErrors() {
        viewmodel.fetchFeedProgression.onEach { result ->
            onRefresh(result = result)
            onError(result = result, eventSnackbar = eventSnackbar)
            onInvalidCredentials(result = result)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun FragmentListContentBinding.attachAppBar() {
        val appBar = if (categoryId == CategoryId.NO_CATEGORY) {
            bottomAppBar.setNavigationOnClickListener {
                BottomNavigationDrawerFragment().also {
                    findNavController().navigate(R.id.bottomNavigationDrawerFragment)
                }
            }
            bottomAppBar
        } else {
            toolBar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
            toolBar
        }
        appBar.run {
            visibility = View.VISIBLE
            inflateMenu(R.menu.menu_list_category_feed)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.addMenuItem -> {
                        findNavController().navigate(
                            FeedFragmentDirections.actionFeedFragmentToFeedDialog(
                                categoryId = categoryId.id
                            )
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
            viewmodel.fetchFeed()
        }
    }

    private fun Fragment.onInvalidCredentials(result: Result<Unit>) {
        if (result.isAccountError()) {
            findNavController().navigate(
                FeedFragmentDirections.actionFeedFragmentToAccountFragment(
                    serverId = viewmodel.currentAccount.serverId.id,
                    userId = viewmodel.currentAccount.userId.id,
                    firstTimeLaunch = true
                )
            )
        }
    }
}