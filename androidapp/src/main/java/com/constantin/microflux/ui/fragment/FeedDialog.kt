package com.constantin.microflux.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.constantin.microflux.R
import com.constantin.microflux.data.*
import com.constantin.microflux.database.toCategoryId
import com.constantin.microflux.databinding.DialogFeedBinding
import com.constantin.microflux.module.FeedDialogViewModel
import com.constantin.microflux.module.State
import com.constantin.microflux.module.ViewmodelFactory
import com.constantin.microflux.repository.transformation.toCategoryTitleList
import com.constantin.microflux.util.BindingDialogFragment
import com.constantin.microflux.util.EventSnackbar
import com.constantin.microflux.util.makeSnackbar
import com.constantin.microflux.util.toAndroidString
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class FeedDialog() : BindingDialogFragment<DialogFeedBinding>(
    DialogFeedBinding::inflate
) {

    companion object {
        private const val SHOW_ADVANCED_RESTORE_NAME = "showAdvanced"
        private const val CATEGORY_INDEX_SELECTION_RESTORE_NAME = "categoryIndex"
    }

    @Inject
    lateinit var viewModelFactory: ViewmodelFactory
    private lateinit var viewmodel: FeedDialogViewModel

    private val eventSnackbar = EventSnackbar()

    private val args: FeedDialogArgs by navArgs()
    private var feedId = FeedId.NO_FEED
    private var categoryId = CategoryId.NO_CATEGORY

    private var feedCategoryIdArray = arrayOf<CategoryId>()

    private var showAdvancedFeatures = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        feedId = args.feedId.let(::FeedId)
        categoryId = args.categoryId.let(::CategoryId)
        viewmodel =
            viewModelFactory.create(State.FeedDialog(feedId)) as FeedDialogViewModel
    }

    override fun onBindingCreated(binding: DialogFeedBinding, savedInstanceState: Bundle?) {
        binding.run {
            attachCategorySpinner()
            if (savedInstanceState == null && feedId != FeedId.NO_FEED) attachFeed()
            observeAddErrors()
            observeUpdateErrors()
            observeDeleteErrors()
            attachAppBar()
            attachDeleteButton()
            attachShowAdvanceFeaturesButton()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putBoolean(SHOW_ADVANCED_RESTORE_NAME, showAdvancedFeatures)
            putInt(
                CATEGORY_INDEX_SELECTION_RESTORE_NAME,
                requireBinding().feedCategorySelectionSpinner.selectedItemPosition
            )
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.run {
            showAdvancedFeatures = getBoolean(SHOW_ADVANCED_RESTORE_NAME)
        }
    }

    private fun DialogFeedBinding.attachCategorySpinner() {
        viewLifecycleOwner.lifecycleScope.launch {
            val categories = viewmodel.categories.await()
            feedCategoryIdArray = categories.toCategoryId().toTypedArray()
            val feedCategoryString = categories.toCategoryTitleList().toTypedArray()
            if (feedCategoryString.isNotEmpty()) {
                feedCategorySelectionSpinner.adapter =
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        feedCategoryString
                    ).also {
                        it.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                        )
                    }
                if (categoryId != CategoryId.NO_CATEGORY) feedCategorySelectionSpinner.setSelection(
                    feedCategoryIdArray.indexOf(categoryId)
                )
            }
        }
    }

    private fun DialogFeedBinding.attachFeed() {
        viewLifecycleOwner.lifecycleScope.launch {
            val feed = viewmodel.feed.await()
            val categories = viewmodel.categories.await()
            titleFeedEditText.setText(feed.feedTitle.title)
            siteUrlEditText.setText(feed.feedSiteUrl.siteUrl)
            feedUrlEditText.setText(feed.feedUrl.url)
            scrapperSwitch.isChecked = feed.feedCrawler.crawler
            feedUserAgentEditText.setText(feed.feedUserAgent.userAgent)
            feedUsernameEditText.setText(feed.feedUsername.username)
            feedPasswordEditText.setText(feed.feedPassword.password)
            scraperRulesEditText.setText(feed.feedScraperRules.scraperRules)
            rewriteRulesEditText.setText(feed.feedRewriteRules.rewriteRules)
            feedCategorySelectionSpinner.setSelection(
                categories.toCategoryTitleList().indexOf(feed.categoryTitle.title)
            )
            feedShowPreviewSwitch.run {
                isChecked = feed.feedAllowImagePreview.allowImagePreview
                isEnabled = feed.settingsAllowImagePreview.allowImagePreview
            }
            feedNotificationSwitch.isChecked = feed.feedAllowNotification.notification

        }
    }

    private fun DialogFeedBinding.observeAddErrors() {
        viewmodel.addFeedProgression.onEach { result ->
            onResult(result)
            onInvalidCredentials(result)
            onError(result, eventSnackbar)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun DialogFeedBinding.observeUpdateErrors() {
        viewmodel.updateFeedProgression.onEach { result ->
            onResult(result)
            onInvalidCredentials(result)
            onError(result, eventSnackbar)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun DialogFeedBinding.observeDeleteErrors() {
        viewmodel.deleteFeedProgression.onEach { result ->
            onResult(result)
            onInvalidCredentials(result)
            onError(result, eventSnackbar)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun DialogFeedBinding.attachAppBar() {
        toolbar.run {
            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
            if (feedId != FeedId.NO_FEED) {
                title = getString(R.string.update_feed)
                updateFeedLayout.visibility = View.VISIBLE
                updateFeedLayoutAdvanced.visibility = View.VISIBLE
            }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.dialog_accept_action -> {
                        if (feedId == FeedId.NO_FEED) viewmodel.addFeed(
                            feedUrl = FeedUrl(feedUrlEditText.text.toString()),
                            categoryId = feedCategoryIdArray[feedCategorySelectionSpinner.selectedItemPosition],
                            feedUserName = FeedUsername(feedUsernameEditText.text.toString()),
                            feedPassword = FeedPassword(feedPasswordEditText.text.toString()),
                            feedCrawler = FeedCrawler(scrapperSwitch.isChecked),
                            feedUserAgent = FeedUserAgent(feedUserAgentEditText.text.toString())
                        )
                        else viewmodel.updateFeed(
                            categoryId = feedCategoryIdArray[feedCategorySelectionSpinner.selectedItemPosition],
                            feedTitle = FeedTitle(titleFeedEditText.text.toString()),
                            feedSiteUrl = FeedSiteUrl(siteUrlEditText.text.toString()),
                            feedUrl = FeedUrl(feedUrlEditText.text.toString()),
                            feedScraperRules = FeedScraperRules(scraperRulesEditText.text.toString()),
                            feedRewriteRules = FeedRewriteRules(rewriteRulesEditText.text.toString()),
                            feedCrawler = FeedCrawler(scrapperSwitch.isChecked),
                            feedUsername = FeedUsername(feedUsernameEditText.text.toString()),
                            feedPassword = FeedPassword(feedPasswordEditText.text.toString()),
                            feedUserAgent = FeedUserAgent(feedUserAgentEditText.text.toString()),
                            feedAllowNotification = FeedAllowNotification(feedNotificationSwitch.isChecked),
                            feedAllowImagePreview = FeedAllowImagePreview(feedShowPreviewSwitch.isChecked)
                        )
                    }
                }
                true
            }
        }
    }

    private fun DialogFeedBinding.attachDeleteButton() {
        deleteFeedButton.setOnClickListener {
            showDeleteAlertDialog()
        }
    }

    private fun DialogFeedBinding.attachShowAdvanceFeaturesButton() {
        showAdvanced(showAdvancedFeatures)
        showAdvancedOptionsFeedButton.setOnClickListener {
            showAdvancedFeatures = !showAdvancedFeatures
            showAdvanced(showAdvancedFeatures)
        }
    }

    private fun onResult(result: Result<Unit>) {
        if (result is Result.Success) {
            dismiss()
        }
    }

    private fun onInvalidCredentials(result: Result<Unit>) {
        if (result.isAccountError()) {
            findNavController().navigate(
                FeedDialogDirections.actionFeedDialogToAccountFragment(
                    serverId = viewmodel.currentAccount.serverId.id,
                    userId = viewmodel.currentAccount.userId.id,
                    firstTimeLaunch = true
                )
            )
        }
    }

    private fun DialogFeedBinding.onError(result: Result<Unit>, eventSnackbar: EventSnackbar) {
        if (result is Result.Error) {
            val stringRes = if (result is Result.Error.NetworkError) R.string.no_connectivity_error
            else R.string.no_connectivity_error
            val snackbar = root.makeSnackbar(stringRes.toAndroidString())
            eventSnackbar.set(snackbar)
        }
    }

    private fun DialogFeedBinding.showAdvanced(showAdvancedFeatures: Boolean) {
        showAdvancedOptionsFeedButton.text = getString(
            if (showAdvancedFeatures) R.string.hide_advanced_options
            else R.string.show_advanced_options
        )
        showAdvancedOptionsFeedLayout.visibility =
            if (showAdvancedFeatures) View.VISIBLE
            else View.GONE
    }

    private fun showDeleteAlertDialog() {
        AlertDialog.Builder(requireContext()).run {
            setMessage(R.string.delete_feed_question)
            setCancelable(true)
            setPositiveButton(android.R.string.ok) { dialog, _ ->
                viewmodel.deleteFeed()
                dialog.cancel()
            }
            setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
            create()
            show()
        }
    }
}