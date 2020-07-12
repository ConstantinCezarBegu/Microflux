package com.example.constaflux2.data

inline class CategoryId(val id: Long){
    companion object{
        val NO_CATEGORY = CategoryId(-1L)
    }
}

inline class CategoryTitle(val title: String)

