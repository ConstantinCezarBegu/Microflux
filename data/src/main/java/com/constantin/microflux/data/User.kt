package com.constantin.microflux.data

inline class UserId(val id: Long){
    companion object{
        val NO_USER = UserId(-1L)
    }
}

inline class UserName(val name: String)

inline class UserPassword(val password: String)

inline class UserSelected(val selected: Boolean) {
    companion object {
        val SELECTED = UserSelected(true)
        val UNSELECTED = UserSelected(false)
    }
}

inline class UserFirstTimeRun (val firstTimeRun: Boolean){
    companion object {
        val TRUE = UserFirstTimeRun(true)
        val FALSE = UserFirstTimeRun(false)
    }
}