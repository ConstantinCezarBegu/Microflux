package com.example.constaflux2.data

fun Long.toBoolean(): Boolean {
    return (this > 0)
}

fun Boolean.toLong(): Long {
    return if (this) 1 else 0
}
