package com.constantin.microflux.data

inline class CategoryId(val id: Long){
    companion object{
        val NO_CATEGORY = CategoryId(-1L)
    }
}

inline class CategoryTitle(val title: String)

