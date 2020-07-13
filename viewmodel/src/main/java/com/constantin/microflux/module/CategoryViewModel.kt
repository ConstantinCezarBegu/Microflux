package com.constantin.microflux.module

import androidx.lifecycle.viewModelScope
import com.constantin.microflux.data.CategoryId
import com.constantin.microflux.data.CategoryTitle
import com.constantin.microflux.data.Result
import com.constantin.microflux.database.Category
import com.constantin.microflux.module.util.BaseViewModel
import com.constantin.microflux.module.util.load
import com.constantin.microflux.network.data.CategoryRequest
import com.constantin.microflux.repository.ConstafluxRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CategoryViewModel(
    private val context: CoroutineContext,
    private val repository: ConstafluxRepository
) : BaseViewModel() {

    val currentAccount get() = repository.accountRepository.currentAccount

    val categories = repository.categoryRepository.getAllCategories()

    private val _fetchCategoryProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val fetchCategoryProgression: StateFlow<Result<Unit>> = _fetchCategoryProgression

    fun fetchCategory() = viewModelScope.launch {
        _fetchCategoryProgression.load {
            repository.categoryRepository.fetch()
        }
    }
}

class CategoryDialogViewModel(
    private val context: CoroutineContext,
    private val repository: ConstafluxRepository,
    categoryId: CategoryId
) : BaseViewModel() {

    val currentAccount get() = repository.accountRepository.currentAccount

    val category = viewModelScope.async(context) {
        repository.categoryRepository.getCategory(categoryId = categoryId).first()
    }

    private val _addCategoryProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val addCategoryProgression: StateFlow<Result<Unit>> = _addCategoryProgression

    fun addCategory(
        categoryTitle: CategoryTitle
    ) = viewModelScope.launch {
        _addCategoryProgression.load {
            repository.categoryRepository.add(categoryRequest = CategoryRequest(categoryTitle.title))
        }
    }

    private val _updateCategoryProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val updateCategoryProgression: StateFlow<Result<Unit>> = _updateCategoryProgression

    fun updateCategory(
        categoryTitle: CategoryTitle
    ) = viewModelScope.launch {
        val category = category.await()
        _updateCategoryProgression.load {
            repository.categoryRepository.update(
                category = Category(
                    serverId = category.serverId,
                    userId = category.userId,
                    categoryId = category.categoryId,
                    categoryTitle = categoryTitle
                )
            )
        }
    }

    private val _deleteCategoryProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val deleteCategoryProgression: StateFlow<Result<Unit>> = _deleteCategoryProgression

    fun deleteCategory() = viewModelScope.launch {
        val category = category.await()
        _deleteCategoryProgression.load {
            repository.categoryRepository.delete(
                serverId = category.serverId,
                categoryId = category.categoryId
            )
        }
    }
}