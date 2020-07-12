package com.example.constaflux2.data

inline class EntryId(val id: Long){
    companion object{
        val NO_ENTRY = EntryId(-1L)
    }
}

inline class EntryTitle(val title: String)

inline class EntryUrl(val url: String)

inline class EntryPreviewImage(val previewImage: String)

inline class EntryAuthor(val author: String)

inline class EntryContent(val content: String)

inline class EntryPublishedAtDisplay(val publishedAt: String) {
    operator fun compareTo(entryPublishedAt: EntryPublishedAtDisplay): Int {
        return this.publishedAt.compareTo(entryPublishedAt.publishedAt)
    }
}

inline class EntryPublishedAtRaw(val publishedAt: String) {
    operator fun compareTo(entryPublishedAt: EntryPublishedAtRaw): Int {
        return this.publishedAt.compareTo(entryPublishedAt.publishedAt)
    }
}

inline class EntryStatus(val status: String) {
    companion object {
        val READ = EntryStatus("read")
        val UN_READ = EntryStatus("unread")
        val ALL = EntryStatus("all")
    }

    fun not() = if (this == READ) UN_READ else if (this == UN_READ) READ else ALL
}

inline class EntryStarred(val starred: Boolean) {
    companion object {
        val STARRED = EntryStarred(true)
        val UN_STARRED = EntryStarred(false)
    }
    fun not() = if (this == STARRED) UN_STARRED else STARRED
}

inline class EntryPublishedAtUnix(val publishedAt: Long) {
    companion object {
        val EMPTY = EntryPublishedAtUnix(0)
    }
}