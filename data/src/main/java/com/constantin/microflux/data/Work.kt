package com.constantin.microflux.data

inline class WorkType(val type: Long){
    companion object{
        val STATUS_MARK_AS_READ = WorkType(0)
        val STATUS_MARK_AS_UNREAD = WorkType(1)
        val STAR = WorkType(2)
    }
}

fun EntryStatus.toWorkType() = if (this == EntryStatus.READ) WorkType.STATUS_MARK_AS_READ else WorkType.STATUS_MARK_AS_UNREAD