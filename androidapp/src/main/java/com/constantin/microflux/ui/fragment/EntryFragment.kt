package com.constantin.microflux.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.ImageLoader
import com.constantin.microflux.R
import com.constantin.microflux.data.*
import com.constantin.microflux.databinding.FragmentListContentBinding
import com.constantin.microflux.module.EntryViewModel
import com.constantin.microflux.module.State
import com.constantin.microflux.module.ViewmodelFactory
import com.constantin.microflux.ui.adapters.*
import com.constantin.microflux.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class EntryFragment : BindingFragment<FragmentListContentBinding>(
    FragmentListContentBinding::inflate
), IOnBackPressed {

    companion object {
        private const val STATUS_RESTORE_NAME = "entryStatusMode"
        private const val STARRED_RESTORE_NAME = "entryStarredMode"
        private const val SELECTION_RESTORE_NAME = "entrySelectionMode"
    }

    @Inject
    lateinit var viewModelFactory: ViewmodelFactory
    private lateinit var viewmodel: EntryViewModel

    @Inject
    lateinit var imageLoader: ImageLoader

    private val args: EntryFragmentArgs by navArgs()
    private var feedId = FeedId.NO_FEED

    private val eventSnackbar = EventSnackbar()
    private lateinit var recyclerViewAdapter: EntryListRecyclerViewAdapter
    private lateinit var recyclerViewSwipeSimpleCallbacks: ItemTouchHelper.SimpleCallback
    private lateinit var recyclerViewLayoutManager: StaggeredGridLayoutManager

    private var entryStatus: EntryStatus = EntryStatus.UN_READ
        set(value) {
            field = value
            if (isResumed) {
                loadEntries()
                getNavigationAppBar().menu[1].changeMenu(
                    drawableRes = value.statusIcon(requireContext()),
                    resId = value.statusTitle()
                )
            }
        }

    private var entryStarred: EntryStarred = EntryStarred.UN_STARRED
        set(value) {
            field = value
            if (isResumed) {
                loadEntries()
                getNavigationAppBar().menu[0].changeMenu(
                    drawableRes = value.starIcon(requireContext()),
                    resId = value.starTitle()
                )
            }
        }

    override fun onBackPressed() =
        !(this::recyclerViewAdapter.isInitialized && recyclerViewAdapter.selection).also {
            if (it) recyclerViewAdapter.clearSelection()
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        feedId = args.feedId.let(::FeedId)
        viewmodel =
            if (feedId == FeedId.NO_FEED) viewModelFactory.create(State.Entries) as EntryViewModel
            else viewModelFactory.create(State.FeedEntries(feedId)) as EntryViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.run {
            entryStatus = EntryStatus(getString(STATUS_RESTORE_NAME, entryStatus.status))
            entryStarred = EntryStarred(getBoolean(STARRED_RESTORE_NAME, entryStarred.starred))
        }
    }

    override fun onBindingCreated(
        binding: FragmentListContentBinding,
        savedInstanceState: Bundle?
    ) {
        binding.run {
            attachUserTheme()
            attachEmptyState()
            attachRecyclerView()
            observeFetchEntriesErrors()
            observeUpdateEntryStatusErrors()
            observeUpdateEntryStarredErrors()
            attachAppBar()
            attachRefresh()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putString(STATUS_RESTORE_NAME, entryStatus.status)
            putBoolean(STARRED_RESTORE_NAME, entryStarred.starred)
            if (this@EntryFragment::recyclerViewAdapter.isInitialized) {
                putLongArray(
                    SELECTION_RESTORE_NAME,
                    recyclerViewAdapter.selectionList.toLongArray()
                )
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.run {
            getLongArray(SELECTION_RESTORE_NAME)?.let {
                recyclerViewAdapter.bulkSelection(it.toList())
            }
        }
        autoLogin(savedInstanceState == null)
    }

    private fun attachUserTheme() {
        viewLifecycleOwner.lifecycleScope.launch {
            AppCompatDelegate.setDefaultNightMode(
                viewmodel.currentTheme.await().toAndroidDelegate()
            )
        }
    }

    private fun FragmentListContentBinding.attachEmptyState() {
        emptyStateTextTitle.text =
            requireContext().getString(R.string.empty_state_title_entry.toAndroidString())
        emptyStateTextSubtitle.text =
            requireContext().getString(R.string.empty_state_subtitle_entry.toAndroidString())
    }

    private fun FragmentListContentBinding.attachRecyclerView() {
        contentList.disableAnimations()

        recyclerViewSwipeSimpleCallbacks = contentList.onSwipe { viewHolder, _ ->
            val position = viewHolder.bindingAdapterPosition
            val itemId = recyclerViewAdapter.currentList[position].entryId
            viewmodel.updateEntryStatus(listOf(itemId), entryStatus)
        }

        recyclerViewAdapter = EntryListRecyclerViewAdapter(
            imageLoader = imageLoader,
            itemClickCallback = { entryId, _, _ ->
                findNavController().navigate(
                    EntryFragmentDirections.actionEntryFragmentToEntryDescriptionPagerFragment(
                        recyclerViewAdapter.currentList.map { it.entryId.id }.toLongArray(), entryId
                    )
                )
            },
            itemCountCallback = { itemCount ->
                toolBar.title = if (itemCount != 0) itemCount.toString() else ""
            },
            itemShareClickCallback = { entryTitle, entryUrl, _ ->
                startActivity(shareArticleIntent(entryTitle.title, entryUrl.url))
            },
            itemStarClickCallback = { entryId, _ ->
                viewmodel.updateEntryStarred(listOf(entryId))
            },
            selectionCallback = { selected ->
                contentRefresh.isEnabled = !selected

                if (selected) {
                    recyclerViewSwipeSimpleCallbacks.stopSwipes()
                    getNavigationAppBar().visibility = View.GONE
                    attachSelectionAppBar()
                } else {
                    recyclerViewSwipeSimpleCallbacks.setSwipes()
                    toolBar.visibility = View.GONE
                    attachAppBar()
                }
            }
        ).also {
            it.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            contentList.adapter = it
        }

        contentList.addPagination(
            pageSize = 20,
            firstVisiblePosition = {
                with(recyclerViewLayoutManager) {
                    findFirstVisibleItemPositions(IntArray(spanCount)).first()
                }
            },
            loadMoreItems = { _ ->
                Log.d("test", "END OF PAGE")
                viewmodel.fetchEntry(
                    entryStatus = entryStatus,
                    entryStarred = entryStarred,
                    entryAfter = with(recyclerViewAdapter) {
                        currentList.last().entryPublishedAtUnix
                    },
                    clearPrevious = false,
                    showAnimations = false
                )

            }
        )

        viewmodel.entries.observeFilter(
            owner = viewLifecycleOwner,
            stateChangeAction = {
                contentList.scrollToPosition(0)
            },
            action = { entries ->
                emptyStateContainer.isGone = entries.isNotEmpty()
                recyclerViewAdapter.submitList(entries)
                showBottomAppBarIfNoItemToScroll()
            }
        )

        recyclerViewLayoutManager =
            StaggeredGridLayoutManager(
                when (resources.configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> 1
                    Configuration.ORIENTATION_LANDSCAPE -> 2
                    else -> 1
                }
                , StaggeredGridLayoutManager.VERTICAL
            ).also {
                contentList.layoutManager = it
            }
    }

    private fun FragmentListContentBinding.observeFetchEntriesErrors() {
        viewmodel.fetchEntryProgression.onEach { result ->
            onRefresh(result = result)
            onError(result = result, eventSnackbar = eventSnackbar)
            onInvalidCredentials(result = result)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeUpdateEntryStatusErrors() {
        viewmodel.updateEntryStatusProgression.onEach { result ->
            onInvalidCredentials(result = result)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeUpdateEntryStarredErrors() {
        viewmodel.updateEntryStarredProgression.onEach { result ->
            onInvalidCredentials(result = result)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun FragmentListContentBinding.attachAppBar() {
        val appBar = if (feedId == FeedId.NO_FEED) {
            bottomAppBar.run {
                navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_hamburger)!!
                setNavigationOnClickListener {
                    BottomNavigationDrawerFragment().also {
                        findNavController().navigate(R.id.bottomNavigationDrawerFragment)
                    }
                }
            }
            bottomAppBar
        } else {
            toolBar.run {
                navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back)!!
                setNavigationOnClickListener {
                    requireActivity().onBackPressed()
                }
            }
            toolBar
        }

        appBar.run {
            title = ""
            visibility = View.VISIBLE
            replaceMenu(R.menu.menu_list_entry)
            menu[0].changeMenu(
                drawableRes = entryStarred.starIcon(requireContext()),
                resId = entryStarred.starTitle()
            )
            menu[1].changeMenu(
                drawableRes = entryStatus.statusIcon(requireContext()),
                resId = entryStatus.statusTitle()
            )
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.starMenuItem -> {
                        entryStarred = entryStarred.not()
                    }
                    R.id.statusMenuItem -> {
                        entryStatus = entryStatus.not()
                    }
                }
                true
            }
            setOnClickListener { contentList.scrollToPosition(0) }
        }
    }

    private fun FragmentListContentBinding.attachRefresh() {
        contentRefresh.setOnRefreshListener {
            viewmodel.fetchEntry()
        }
    }

    private fun FragmentListContentBinding.attachSelectionAppBar() {
        toolBar.run {
            visibility = View.VISIBLE
            navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_close)!!
            replaceMenu(R.menu.menu_list_selection)
            setNavigationOnClickListener {
                recyclerViewAdapter.clearSelection()
            }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.recycler_view_selection_star -> {
                        viewmodel.updateEntryStarred(
                            recyclerViewAdapter.selectionList.toList().map { EntryId(it) }
                        )
                    }
                    R.id.recycler_view_selection_reading -> {
                        viewmodel.updateEntryStatus(
                            recyclerViewAdapter.selectionList.toList().map { EntryId(it) },
                            entryStatus
                        )
                    }
                    R.id.recycler_view_select_all -> {
                        recyclerViewAdapter.bulkSelection()
                    }
                }
                true
            }
        }
    }

    private fun autoLogin(isFirstLaunch: Boolean) {
        if (feedId == FeedId.NO_FEED && isFirstLaunch) {
            if (viewmodel.isLogin) {
                loadEntries(fetch = true, animate = true)
            } else {
                findNavController().navigate(
                    EntryFragmentDirections.actionEntryFragmentToAccountFragment(firstTimeLaunch = true)
                )
            }
        } else loadEntries()
    }

    private fun onInvalidCredentials(result: Result<Unit>) {
        if (result.isAccountError()) {
            findNavController().navigate(
                EntryFragmentDirections.actionEntryFragmentToAccountFragment(
                    serverId = viewmodel.currentAccount.serverId.id,
                    userId = viewmodel.currentAccount.userId.id,
                    firstTimeLaunch = true
                )
            )
        }
    }

    private fun loadEntries(
        fetch: Boolean = true,
        animate: Boolean = true
    ) {
        viewmodel.run {
            getEntries(
                entryStatus = entryStatus,
                entryStarred = entryStarred
            )
            if (fetch) viewmodel.fetchEntry(
                entryStatus = entryStatus,
                entryStarred = entryStarred,
                showAnimations = animate
            )
        }
    }

    private fun getNavigationAppBar() =
        if (feedId == FeedId.NO_FEED) requireBinding().bottomAppBar else requireBinding().toolBar

    private fun FragmentListContentBinding.showBottomAppBarIfNoItemToScroll() {
        if (!contentList.canScrollVertically(0) && feedId == FeedId.NO_FEED) bottomAppBar.performShow()
    }
}
