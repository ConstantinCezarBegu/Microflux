package com.constantin.microflux.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.constantin.microflux.R
import com.constantin.microflux.data.CategoryId
import com.constantin.microflux.data.CategoryTitle
import com.constantin.microflux.data.Result
import com.constantin.microflux.databinding.DialogCategoryBinding
import com.constantin.microflux.module.CategoryDialogViewModel
import com.constantin.microflux.module.State
import com.constantin.microflux.module.ViewmodelFactory
import com.constantin.microflux.util.BindingDialogFragment
import com.constantin.microflux.util.EventSnackbar
import com.constantin.microflux.util.makeSnackbar
import com.constantin.microflux.util.toAndroidString
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


class CategoryDialog() : BindingDialogFragment<DialogCategoryBinding>(
    DialogCategoryBinding::inflate
) {

    @Inject
    lateinit var viewModelFactory: ViewmodelFactory
    private lateinit var viewmodel: CategoryDialogViewModel

    private val eventSnackbar = EventSnackbar()

    private val args: CategoryDialogArgs by navArgs()
    private var categoryId = CategoryId.NO_CATEGORY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        categoryId = args.categoryId.let(::CategoryId)
        viewmodel =
            viewModelFactory.create(State.CategoryDialog(categoryId)) as CategoryDialogViewModel
    }

    override fun onBindingCreated(binding: DialogCategoryBinding, savedInstanceState: Bundle?) {
        binding.run {
            if (savedInstanceState == null && categoryId != CategoryId.NO_CATEGORY) attachCategory()
            observeAddErrors()
            observeUpdateErrors()
            observeDeleteErrors()
            attachAppBar()
            attachDeleteButton()
        }
    }

    private fun DialogCategoryBinding.attachCategory() {
        viewLifecycleOwner.lifecycleScope.launch {
            val category = viewmodel.category.await()
            categoryTitleEditText.setText(
                category.categoryTitle.title
            )
        }
    }

    private fun DialogCategoryBinding.observeAddErrors() {
        viewmodel.addCategoryProgression.onEach { result ->
            onResult(result)
            onInvalidCredentials(result)
            onError(result, eventSnackbar)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun DialogCategoryBinding.observeUpdateErrors() {
        viewmodel.updateCategoryProgression.onEach { result ->
            onResult(result)
            onInvalidCredentials(result)
            onError(result, eventSnackbar)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun DialogCategoryBinding.observeDeleteErrors() {
        viewmodel.deleteCategoryProgression.onEach { result ->
            onResult(result)
            onInvalidCredentials(result)
            onError(result, eventSnackbar)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun DialogCategoryBinding.attachAppBar() {
        toolbar.run {
            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
            if (categoryId != CategoryId.NO_CATEGORY) {
                title = getString(R.string.update_category)
                deleteCategoryButton.visibility = View.VISIBLE
            }
            setOnMenuItemClickListener { menuItem ->
                val categoryTitle = CategoryTitle(categoryTitleEditText.text.toString())
                when (menuItem.itemId) {
                    R.id.dialog_accept_action -> {
                        if (categoryId == CategoryId.NO_CATEGORY) viewmodel.addCategory(
                            categoryTitle
                        )
                        else viewmodel.updateCategory(categoryTitle)
                    }
                }
                true
            }
        }
    }

    private fun DialogCategoryBinding.attachDeleteButton() {
        deleteCategoryButton.setOnClickListener {
            showDeleteAlertDialog()
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
                CategoryDialogDirections.actionCategoryDialogToAccountFragment(
                    serverId = viewmodel.currentAccount.serverId.id,
                    userId = viewmodel.currentAccount.userId.id,
                    firstTimeLaunch = true
                )
            )
        }
    }

    private fun DialogCategoryBinding.onError(result: Result<Unit>, eventSnackbar: EventSnackbar) {
        if (result is Result.Error) {
            val stringRes = if (result is Result.Error.NetworkError) R.string.no_connectivity_error
            else R.string.no_connectivity_error
            val snackbar = root.makeSnackbar(stringRes.toAndroidString())
            eventSnackbar.set(snackbar)
        }
    }

    private fun showDeleteAlertDialog() {
        AlertDialog.Builder(requireContext()).also {
            it.run {
                setMessage(R.string.delete_category_question)
                setCancelable(true)
                setPositiveButton(android.R.string.ok) { dialog, _ ->
                    viewmodel.deleteCategory()
                    dialog.cancel()
                }
                setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                create()
                show()
            }
        }
    }
}