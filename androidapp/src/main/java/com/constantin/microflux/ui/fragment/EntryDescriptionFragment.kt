package com.constantin.microflux.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.webkit.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.constantin.microflux.R
import com.constantin.microflux.data.EntryId
import com.constantin.microflux.data.EntryStarred
import com.constantin.microflux.data.EntryStatus
import com.constantin.microflux.data.Result
import com.constantin.microflux.database.EntryDescription
import com.constantin.microflux.databinding.FragmentEntryDescriptionBinding
import com.constantin.microflux.module.EntryDescriptionViewModel
import com.constantin.microflux.module.State
import com.constantin.microflux.module.ViewmodelFactory
import com.constantin.microflux.ui.adapters.*
import com.constantin.microflux.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.dankito.readability4j.Readability4J
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import kotlin.math.abs


class EntryDescriptionFragment() : BindingFragment<FragmentEntryDescriptionBinding>(
    FragmentEntryDescriptionBinding::inflate
) {

    companion object {
        private const val IS_SEEN_RESTORE_NAME = "isSeen"
        private const val IS_FETCH_ORIGINAL_RESTORE_NAME = "isFetchOriginal"
        fun createFragment(entryId: EntryId) =
            EntryDescriptionFragment().also { fragment ->
                fragment.arguments = bundleOf("entryId" to entryId.id)
            }
    }

    @Inject
    lateinit var viewModelFactory: ViewmodelFactory
    private lateinit var viewmodel: EntryDescriptionViewModel

    private val eventSnackbar = EventSnackbar()

    private val args: EntryDescriptionFragmentArgs by navArgs()
    private var entryId = EntryId.NO_ENTRY

    @Inject
    lateinit var customTabsIntent: CustomTabsIntent

    @Inject
    lateinit var webViewClient: WebViewClient

    @Inject
    lateinit var okHttpClient: OkHttpClient

    private var entryStatus: EntryStatus = EntryStatus.UN_READ
        set(value) {
            field = value
            if (isResumed) {
                viewmodel.updateEntryStatus(entryStatus = value)
                (parentFragment as EntryDescriptionPagerFragment).requireBinding().toolBar.menu[1].changeMenu(
                    drawableRes = value.statusIcon(requireContext()),
                    resId = value.statusTitle()
                )
            }
        }

    private var entryStarred: EntryStarred = EntryStarred.UN_STARRED
        set(value) {
            field = value
            if (isResumed) {
                viewmodel.updateEntryStarred()
                (parentFragment as EntryDescriptionPagerFragment).requireBinding().toolBar.menu[0].changeMenu(
                    drawableRes = value.starIcon(requireContext()),
                    resId = value.starTitle()
                )
            }
        }

    private var isFetchedOriginal = false
        set(value) {
            field = value
            if (isResumed) {
                requireBinding().getOriginalContent(value)
                (parentFragment as EntryDescriptionPagerFragment).requireBinding().toolBar.menu[3].changeMenu(
                    drawableRes = value.fetchIcon(requireContext()),
                    resId = value.fetchOriginalTitle()
                )
            }
        }

    private var isArticleSeen = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        entryId = args.entryId.let(::EntryId)
        viewmodel =
            viewModelFactory.create(State.EntryDescription(entryId)) as EntryDescriptionViewModel
    }

    override fun onBindingCreated(
        binding: FragmentEntryDescriptionBinding,
        savedInstanceState: Bundle?
    ) {
        setEntryState()
        binding.run {
            attachWebView(savedInstanceState)
        }
    }

    override fun onResume() {
        super.onResume()
        requireBinding().run {
            observeUpdateEntryStatusError()
            observeUpdateEntryStarredError()
            attachAppBar()
            markAsRead()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putBoolean(IS_FETCH_ORIGINAL_RESTORE_NAME, isFetchedOriginal)
            putBoolean(IS_SEEN_RESTORE_NAME, isArticleSeen)
            requireBinding().entryContentWebView.saveState(outState)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.run {
            isFetchedOriginal = getBoolean(IS_FETCH_ORIGINAL_RESTORE_NAME, isFetchedOriginal)
            isArticleSeen = getBoolean(IS_SEEN_RESTORE_NAME, isArticleSeen)
        }
    }

    private fun setEntryState() {
        viewLifecycleOwner.lifecycleScope.launch {
            val entry = viewmodel.entry.await()
            entryStatus = entry.entryStatus
            entryStarred = entry.entryStarred
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    private fun FragmentEntryDescriptionBinding.attachWebView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) getOriginalContent(isFetchedOriginal)
        else entryContentWebView.restoreState(savedInstanceState)
        entryContentWebView.run {
            setBackgroundColor(Color.TRANSPARENT)
            webViewClient = this@EntryDescriptionFragment.webViewClient
            setOnCreateContextMenuListener { _, _, _ ->
                val hitTestResult: WebView.HitTestResult = hitTestResult
                val url: String? = hitTestResult.extra
                if (url != null && URLUtil.isValidUrl(url)) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        share(url)
                    }
                }
            }
            keepScreenOn = true
            settings.run {
                javaScriptEnabled = true
                addJavascriptInterface(object : Any() {
                    @JavascriptInterface
                    fun reloadTheme() {
                        entryContentWebView.run {
                            val htmlTextColor = String.format(
                                "#%06X",
                                0xFFFFFF and context.getAttributeColor(android.R.attr.textColorPrimary)
                            )
                            val htmlTextAccentColor = String.format(
                                "#%06X",
                                0xFFFFFF and context.getAttributeColor(R.attr.colorPrimary)
                            )
                            lifecycleScope.launch(Dispatchers.Main) {
                                evaluateJavascript(
                                    """
                                    var sheet = document.createElement('style');
                                    console.log("test");
                                    sheet.innerHTML = 
                                    `
                                        ::selection { background:${htmlTextAccentColor} !important; color:#FFFFFF !important; }
                                        body { color:${htmlTextColor} !important; } 
                                        a {color:${htmlTextAccentColor} !important;}
                                    `
                                    
                                    document.body.appendChild(sheet);
                                    """.trimIndent(),
                                    null
                                )
                            }
                        }
                    }

                    @JavascriptInterface
                    fun preClick() {
                        (parentFragment as EntryDescriptionPagerFragment)
                            .requireBinding()
                            .entryDescriptionViewPager
                            .isUserInputEnabled = false

                        entryContentWebView.isNestedScrollingEnabled = false
                    }
                }, "webviewCommunication")
                cacheMode = WebSettings.LOAD_NO_CACHE
                setGeolocationEnabled(false)
                setNeedInitialFocus(false)
            }
            var oldY = 0F
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) oldY = event.y
                else if ((abs(oldY - event.y) > 20) && (event.action == MotionEvent.ACTION_MOVE)) {
                    (parentFragment as EntryDescriptionPagerFragment)
                        .requireBinding()
                        .entryDescriptionViewPager
                        .isUserInputEnabled = false

                } else if (event.action == MotionEvent.ACTION_UP) {
                    (parentFragment as EntryDescriptionPagerFragment)
                        .requireBinding()
                        .entryDescriptionViewPager
                        .isUserInputEnabled = true

                    entryContentWebView.isNestedScrollingEnabled = true
                }
                false
            }
        }
    }

    private fun observeUpdateEntryStatusError() {
        viewmodel.updateEntryStatusProgression.onEach { result ->
            onInvalidCredentials(result)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeUpdateEntryStarredError() {
        viewmodel.updateEntryStarredProgression.onEach { result ->
            onInvalidCredentials(result)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun attachAppBar() {
        (parentFragment as EntryDescriptionPagerFragment).requireBinding().toolBar.run {
            menu[1].changeMenu(
                drawableRes = entryStatus.statusIcon(requireContext()),
                resId = entryStatus.statusTitle()
            )
            menu[0].changeMenu(
                drawableRes = entryStarred.starIcon(requireContext()),
                resId = entryStarred.starTitle()
            )
            menu[3].changeMenu(
                drawableRes = isFetchedOriginal.fetchIcon(requireContext()),
                resId = isFetchedOriginal.fetchOriginalTitle()
            )
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.starMenuItem -> {
                        entryStarred = entryStarred.not()
                    }
                    R.id.statusMenuItem -> {
                        entryStatus = entryStatus.not()
                    }
                    R.id.shareMenuItem -> {
                        share()
                    }
                    R.id.fetchOriginalMenuItem -> {
                        isFetchedOriginal = !isFetchedOriginal
                    }
                    R.id.openChromeMenuItem -> {
                        launchInBrowser()
                    }
                }
                true
            }
        }
    }

    private fun markAsRead() {
        if (isArticleSeen.not() && entryStatus == EntryStatus.UN_READ) {
            entryStatus = entryStatus.not()
            isArticleSeen = true
        }
    }

    private fun onInvalidCredentials(result: Result<Unit>) {
        if (result.isAccountError()) {
            findNavController().navigate(
                EntryDescriptionFragmentDirections.actionEntryDescriptionFragmentToAccountFragment(
                    serverId = viewmodel.currentAccount.serverId.id,
                    userId = viewmodel.currentAccount.userId.id,
                    firstTimeLaunch = true
                )
            )
        }
    }

    private fun share(url: String = "") {
        viewLifecycleOwner.lifecycleScope.launch {
            val entry = viewmodel.entry.await()
            val entryTitle = entry.entryTitle.title
            val entryUrl = if (url.isBlank()) entry.entryUrl.url else url
            startActivity(shareArticleIntent(entryTitle, entryUrl))
        }
    }

    private fun launchInBrowser() {
        viewLifecycleOwner.lifecycleScope.launch {
            customTabsIntent.launchUrl(
                requireContext(),
                Uri.parse(viewmodel.entry.await().entryUrl.url)
            )
        }
    }

    private fun FragmentEntryDescriptionBinding.getOriginalContent(isFetchedOriginal: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            if (isFetchedOriginal) {
                try {
                    val url = viewmodel.entry.await().entryUrl.url
                    val request = Request.Builder()
                        .url(url)
                        .build()

                    val response = okHttpClient.newCall(request).execute()
                    if (response.body() != null) {
                        val readability4J = Readability4J(url, response.body()!!.string())
                        val article = readability4J.parse()
                        val extractedContentHtmlWithUtf8Encoding = article.contentWithUtf8Encoding!!

                        launch(Dispatchers.Main) {
                            entryContentWebView.loadDataWithBaseURL(
                                "",
                                viewmodel.entry.await()
                                    .toHtml(extractedContentHtmlWithUtf8Encoding),
                                "text/html;",
                                "UTF-8",
                                ""
                            )
                        }
                    }
                } catch (e: IOException) {

                }
            } else {
                launch(Dispatchers.Main) {
                    entryContentWebView.loadDataWithBaseURL(
                        "",
                        viewmodel.entry.await().toHtml(),
                        "text/html;",
                        "UTF-8",
                        ""
                    )
                }
            }
            launch(Dispatchers.Main) {
                (parentFragment as EntryDescriptionPagerFragment).requireBinding().toolBar.run {
                    menu[3].icon = isFetchedOriginal.fetchIcon(requireContext())
                }
            }

        }
        entryContentWebView.setInitialScale(0)
    }

    private fun EntryDescription.toHtml(
        body: String = entryContent.content
            .replace("&lt;a", "<pre><a")
            .replace("a&gt;", "a></pre>")
            .replace("&lt;", "<")
            .replace("&#34;", "\"")
            .replace("&gt;", ">")
    ): String {
        val author =
            if (entryAuthor.author.isNotBlank()) " - ${entryAuthor.author}" else ""
        val category =
            if (categoryTitle.title.isNotBlank()) " - ${categoryTitle.title}" else ""
        val title = "<div><h1>${entryTitle.title}</h1></div>"
        val subtitle = "<p>${feedTitle.title}$author$category</p>"

        return """
                <html>
                    <head>
                        <style type="text/css">
                            * { max-width:100% !important; }
                            img { display: block; height: auto; }
                            pre {overflow-x:scroll;}
                            div {overflow-x:scroll;}
                            code {overflow-x:scroll;}
                            body { padding: 1em 1em; }
                        </style>
                    </head>

                    <body>
                        $title
                        $subtitle
                        <hr>
                        <br>
                        $body
                        <br>
                        <br>
                        <br>
                        <script>
                            document.querySelectorAll('pre, code, div').forEach(element => {
                                element.addEventListener('touchstart', event => {
                                    webviewCommunication.preClick();
                                });
                            });
                            
                            webviewCommunication.reloadTheme();
                        </script>
                    </body>
                </html>
            """
    }
}