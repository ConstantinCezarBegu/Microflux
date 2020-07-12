package com.example.constaflux2.data

inline class FeedId(val id: Long){
    companion object{
        val NO_FEED = FeedId(-1L)
    }
}

inline class FeedTitle(val title: String)

inline class FeedSiteUrl(val siteUrl: String)

inline class FeedUrl(val url: String)

inline class FeedCheckedAtDisplay(val checkedAt: String)

inline class FeedLastUpdateAtUnix(val lastUpdateAtUnix: Long) {
    companion object {
        val EMPTY = FeedLastUpdateAtUnix(0)
    }
}

inline class FeedIcon(val icon: ByteArray)

inline class FeedScraperRules(val scraperRules: String)

inline class FeedRewriteRules(val rewriteRules: String)

inline class FeedCrawler(val crawler: Boolean) {
    companion object {
        val ON = FeedCrawler(true)
        val OFF = FeedCrawler(false)
    }
}

inline class FeedUsername(val username: String)

inline class FeedPassword(val password: String)

inline class FeedUserAgent(val userAgent: String)

inline class FeedAllowNotification(val notification: Boolean) {
    companion object {
        val ON = FeedAllowNotification(true)
        val OFF = FeedAllowNotification(false)
    }
}

inline class FeedAllowImagePreview(val allowImagePreview: Boolean) {
    companion object {
        val ON = FeedAllowImagePreview(true)
        val OFF = FeedAllowImagePreview(false)
    }
}

inline class FeedNotificationCount(val count: Long) {
    companion object {
        val INVALID = FeedNotificationCount(0)
    }
}

inline class FeedNotified(val notified: Boolean) {
    companion object {
        val ON = FeedNotified(true)
        val OFF = FeedNotified(false)
    }
}