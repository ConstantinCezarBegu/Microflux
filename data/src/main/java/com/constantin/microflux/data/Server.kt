package com.constantin.microflux.data

inline class ServerUrl(val url: String)

inline class ServerId(val id: Long){
    companion object{
        val NO_SERVER = ServerId(-1L)
    }
}