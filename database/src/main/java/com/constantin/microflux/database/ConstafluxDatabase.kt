package com.constantin.microflux.database

import com.constantin.microflux.data.*
import com.constantin.microflux.encryption.AesEncryption
import com.google.crypto.tink.subtle.Base64
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver

class NoUserException(message: String = "Cannot select new User since none are available.") :
    Exception(message)

class ConstafluxDatabase(
    sqlDriver: SqlDriver,
    aesEncryption: AesEncryption
) {
    companion object {
        const val DB_NAME = "constaflux2.db"
    }

    private val entryAdapter = Entry.Adapter(
        serverIdAdapter = object : ColumnAdapter<ServerId, Long> {
            override fun decode(databaseValue: Long) =
                ServerId(databaseValue)

            override fun encode(value: ServerId) = value.id
        },
        entryIdAdapter = object : ColumnAdapter<EntryId, Long> {
            override fun decode(databaseValue: Long) =
                EntryId(databaseValue)

            override fun encode(value: EntryId) = value.id
        },
        feedIdAdapter = object : ColumnAdapter<FeedId, Long> {
            override fun decode(databaseValue: Long) =
                FeedId(databaseValue)

            override fun encode(value: FeedId) = value.id
        },
        entryTitleAdapter = object : ColumnAdapter<EntryTitle, String> {
            override fun decode(databaseValue: String) =
                EntryTitle(databaseValue)

            override fun encode(value: EntryTitle) = value.title
        },
        entryUrlAdapter = object : ColumnAdapter<EntryUrl, String> {
            override fun decode(databaseValue: String) =
                EntryUrl(databaseValue)

            override fun encode(value: EntryUrl) = value.url
        },
        entryPreviewImageAdapter = object : ColumnAdapter<EntryPreviewImage, String> {
            override fun decode(databaseValue: String) =
                EntryPreviewImage(
                    databaseValue
                )

            override fun encode(value: EntryPreviewImage) = value.previewImage
        },
        entryAuthorAdapter = object : ColumnAdapter<EntryAuthor, String> {
            override fun decode(databaseValue: String) =
                EntryAuthor(databaseValue)

            override fun encode(value: EntryAuthor) = value.author
        },
        entryContentAdapter = object : ColumnAdapter<EntryContent, String> {
            override fun decode(databaseValue: String) =
                EntryContent(databaseValue)

            override fun encode(value: EntryContent) = value.content
        },
        entryPublishedAtDisplayAdapter = object : ColumnAdapter<EntryPublishedAtDisplay, String> {
            override fun decode(databaseValue: String) =
                EntryPublishedAtDisplay(databaseValue)

            override fun encode(value: EntryPublishedAtDisplay) =
                value.publishedAt
        },
        entryPublishedAtRawAdapter = object : ColumnAdapter<EntryPublishedAtRaw, String> {
            override fun decode(databaseValue: String) =
                EntryPublishedAtRaw(databaseValue)

            override fun encode(value: EntryPublishedAtRaw) =
                value.publishedAt
        },
        entryStatusAdapter = object : ColumnAdapter<EntryStatus, String> {
            override fun decode(databaseValue: String) =
                EntryStatus(databaseValue)

            override fun encode(value: EntryStatus) = value.status
        },
        entryStarredAdapter = object : ColumnAdapter<EntryStarred, Long> {
            override fun decode(databaseValue: Long) =
                EntryStarred(databaseValue.toBoolean())

            override fun encode(value: EntryStarred) = value.starred.toLong()
        },
        entryPublishedAtUnixAdapter = object : ColumnAdapter<EntryPublishedAtUnix, Long> {
            override fun decode(databaseValue: Long) =
                EntryPublishedAtUnix(databaseValue)

            override fun encode(value: EntryPublishedAtUnix) = value.publishedAt
        }
    )

    private val feedAdapter = Feed.Adapter(
        serverIdAdapter = object : ColumnAdapter<ServerId, Long> {
            override fun decode(databaseValue: Long) =
                ServerId(databaseValue)

            override fun encode(value: ServerId) = value.id
        },
        feedIdAdapter = object : ColumnAdapter<FeedId, Long> {
            override fun decode(databaseValue: Long) =
                FeedId(databaseValue)

            override fun encode(value: FeedId) = value.id
        },
        categoryIdAdapter = object : ColumnAdapter<CategoryId, Long> {
            override fun decode(databaseValue: Long) =
                CategoryId(databaseValue)

            override fun encode(value: CategoryId) = value.id
        },
        feedTitleAdapter = object : ColumnAdapter<FeedTitle, String> {
            override fun decode(databaseValue: String) =
                FeedTitle(databaseValue)

            override fun encode(value: FeedTitle) = value.title
        },
        feedSiteUrlAdapter = object : ColumnAdapter<FeedSiteUrl, String> {
            override fun decode(databaseValue: String) =
                FeedSiteUrl(databaseValue)

            override fun encode(value: FeedSiteUrl) = value.siteUrl
        },
        feedUrlAdapter = object : ColumnAdapter<FeedUrl, String> {
            override fun decode(databaseValue: String) =
                FeedUrl(databaseValue)

            override fun encode(value: FeedUrl) = value.url
        },
        feedCheckedAtDisplayAdapter = object : ColumnAdapter<FeedCheckedAtDisplay, String> {
            override fun decode(databaseValue: String) =
                FeedCheckedAtDisplay(databaseValue)

            override fun encode(value: FeedCheckedAtDisplay) = value.checkedAt
        },
        feedIconAdapter = object : ColumnAdapter<FeedIcon, ByteArray> {
            override fun decode(databaseValue: ByteArray) =
                FeedIcon(databaseValue)

            override fun encode(value: FeedIcon) = value.icon
        },
        feedScraperRulesAdapter = object : ColumnAdapter<FeedScraperRules, String> {
            override fun decode(databaseValue: String) =
                FeedScraperRules(
                    databaseValue
                )

            override fun encode(value: FeedScraperRules) = value.scraperRules
        },
        feedRewriteRulesAdapter = object : ColumnAdapter<FeedRewriteRules, String> {
            override fun decode(databaseValue: String) =
                FeedRewriteRules(
                    databaseValue
                )

            override fun encode(value: FeedRewriteRules) = value.rewriteRules
        },
        feedCrawlerAdapter = object : ColumnAdapter<FeedCrawler, Long> {
            override fun decode(databaseValue: Long) =
                FeedCrawler(databaseValue.toBoolean())

            override fun encode(value: FeedCrawler) = value.crawler.toLong()
        },
        feedUsernameAdapter = object : ColumnAdapter<FeedUsername, ByteArray> {
            override fun decode(databaseValue: ByteArray) =
                FeedUsername(aesEncryption.decryptData(databaseValue))

            override fun encode(value: FeedUsername) = aesEncryption.encryptData(value.username)
        },
        feedPasswordAdapter = object : ColumnAdapter<FeedPassword, ByteArray> {
            override fun decode(databaseValue: ByteArray) =
                FeedPassword(aesEncryption.decryptData(databaseValue))

            override fun encode(value: FeedPassword) = aesEncryption.encryptData(value.password)
        },
        feedUserAgentAdapter = object : ColumnAdapter<FeedUserAgent, String> {
            override fun decode(databaseValue: String) =
                FeedUserAgent(databaseValue)

            override fun encode(value: FeedUserAgent) = value.userAgent
        },
        feedAllowNotificationAdapter = object : ColumnAdapter<FeedAllowNotification, Long> {
            override fun decode(databaseValue: Long) =
                FeedAllowNotification(
                    databaseValue.toBoolean()
                )

            override fun encode(value: FeedAllowNotification) = value.notification.toLong()
        },
        feedAllowImagePreviewAdapter = object : ColumnAdapter<FeedAllowImagePreview, Long> {
            override fun decode(databaseValue: Long) =
                FeedAllowImagePreview(
                    databaseValue.toBoolean()
                )

            override fun encode(value: FeedAllowImagePreview) = value.allowImagePreview.toLong()
        },
        feedLastUpdateAtUnixAdapter = object : ColumnAdapter<FeedLastUpdateAtUnix, Long> {
            override fun decode(databaseValue: Long) =
                FeedLastUpdateAtUnix(databaseValue)

            override fun encode(value: FeedLastUpdateAtUnix) = value.lastUpdateAtUnix
        },
        feedNotificationCountAdapter = object : ColumnAdapter<FeedNotificationCount, Long> {
            override fun decode(databaseValue: Long) =
                FeedNotificationCount(databaseValue)

            override fun encode(value: FeedNotificationCount) = value.count
        }
    )

    private val categoryAdapter = Category.Adapter(
        serverIdAdapter = object : ColumnAdapter<ServerId, Long> {
            override fun decode(databaseValue: Long) =
                ServerId(databaseValue)

            override fun encode(value: ServerId) = value.id
        },
        categoryIdAdapter = object : ColumnAdapter<CategoryId, Long> {
            override fun decode(databaseValue: Long) =
                CategoryId(databaseValue)

            override fun encode(value: CategoryId) = value.id
        },
        categoryTitleAdapter = object : ColumnAdapter<CategoryTitle, String> {
            override fun decode(databaseValue: String) =
                CategoryTitle(databaseValue)

            override fun encode(value: CategoryTitle) = value.title
        },
        userIdAdapter = object : ColumnAdapter<UserId, Long> {
            override fun decode(databaseValue: Long) =
                UserId(databaseValue)

            override fun encode(value: UserId) = value.id
        }
    )

    private val userAdapter = User.Adapter(
        serverIdAdapter = object : ColumnAdapter<ServerId, Long> {
            override fun decode(databaseValue: Long) =
                ServerId(databaseValue)

            override fun encode(value: ServerId) = value.id
        },
        userIdAdapter = object : ColumnAdapter<UserId, Long> {
            override fun decode(databaseValue: Long) =
                UserId(databaseValue)

            override fun encode(value: UserId) = value.id
        },
        userNameAdapter = object : ColumnAdapter<UserName, String> {
            override fun decode(databaseValue: String) =
                UserName(
                    Base64.decode(
                        databaseValue,
                        Base64.DEFAULT
                    ).toString(Charsets.UTF_8)
                )

            override fun encode(value: UserName) = Base64.encodeToString(
                value.name.toByteArray(Charsets.UTF_8),
                Base64.DEFAULT
            )
        },
        userPasswordAdapter = object : ColumnAdapter<UserPassword, ByteArray> {
            override fun decode(databaseValue: ByteArray) =
                UserPassword(aesEncryption.decryptData(databaseValue))

            override fun encode(value: UserPassword) =
                aesEncryption.encryptData(value.password)
        },
        userSelectedAdapter = object : ColumnAdapter<UserSelected, Long> {
            override fun decode(databaseValue: Long) =
                UserSelected(databaseValue.toBoolean())

            override fun encode(value: UserSelected) = value.selected.toLong()
        },
        userFirstTimeRunAdapter = object : ColumnAdapter<UserFirstTimeRun, Long> {
            override fun decode(databaseValue: Long) =
                UserFirstTimeRun(
                    databaseValue.toBoolean()
                )

            override fun encode(value: UserFirstTimeRun) = value.firstTimeRun.toLong()
        }
    )

    private val meAdapter = Me.Adapter(
        serverIdAdapter = object : ColumnAdapter<ServerId, Long> {
            override fun decode(databaseValue: Long) =
                ServerId(databaseValue)

            override fun encode(value: ServerId) = value.id
        },
        userIdAdapter = object : ColumnAdapter<UserId, Long> {
            override fun decode(databaseValue: Long) =
                UserId(databaseValue)

            override fun encode(value: UserId) = value.id
        },
        meIsAdminAdapter = object : ColumnAdapter<MeIsAdmin, Long> {
            override fun decode(databaseValue: Long) =
                MeIsAdmin(databaseValue.toBoolean())

            override fun encode(value: MeIsAdmin) = value.isAdmin.toLong()
        },
        meLanguageAdapter = object : ColumnAdapter<MeLanguage, String> {
            override fun decode(databaseValue: String) =
                MeLanguage(databaseValue)

            override fun encode(value: MeLanguage) = value.language
        },
        meLastLoginAtAdapter = object : ColumnAdapter<MeLastLoginAt, String> {
            override fun decode(databaseValue: String) =
                MeLastLoginAt(databaseValue)

            override fun encode(value: MeLastLoginAt) = value.lastLoginAt
        },
        meThemeAdapter = object : ColumnAdapter<MeTheme, String> {
            override fun decode(databaseValue: String) =
                MeTheme(databaseValue)

            override fun encode(value: MeTheme) = value.theme
        },
        meTimeZoneAdapter = object : ColumnAdapter<MeTimeZone, String> {
            override fun decode(databaseValue: String) =
                MeTimeZone(databaseValue)

            override fun encode(value: MeTimeZone) = value.timeZone
        },
        meEntrySortingDirectionAdapter = object : ColumnAdapter<MeEntrySortingDirection, String> {
            override fun decode(databaseValue: String) =
                MeEntrySortingDirection(
                    databaseValue
                )

            override fun encode(value: MeEntrySortingDirection) = value.sortingDirection
        }
    )

    private val settingsAdapter = Settings.Adapter(
        serverIdAdapter = object : ColumnAdapter<ServerId, Long> {
            override fun decode(databaseValue: Long) =
                ServerId(databaseValue)

            override fun encode(value: ServerId) = value.id
        },
        userIdAdapter = object : ColumnAdapter<UserId, Long> {
            override fun decode(databaseValue: Long) =
                UserId(databaseValue)

            override fun encode(value: UserId) = value.id
        },
        settingsThemeAdapter = object : ColumnAdapter<SettingsTheme, Long> {
            override fun decode(databaseValue: Long) =
                SettingsTheme(databaseValue.toInt())

            override fun encode(value: SettingsTheme) = value.theme.toLong()
        },
        settingsAllowImagePreviewAdapter = object : ColumnAdapter<SettingsAllowImagePreview, Long> {
            override fun decode(databaseValue: Long) =
                SettingsAllowImagePreview(databaseValue.toBoolean())

            override fun encode(value: SettingsAllowImagePreview) = value.allowImagePreview.toLong()
        }
    )

    private val serverAdapter = Server.Adapter(
        serverIdAdapter = object : ColumnAdapter<ServerId, Long> {
            override fun decode(databaseValue: Long) =
                ServerId(databaseValue)

            override fun encode(value: ServerId) = value.id
        },
        serverUrlAdapter = object : ColumnAdapter<ServerUrl, String> {
            override fun decode(databaseValue: String) =
                ServerUrl(
                    Base64.decode(
                        databaseValue,
                        Base64.DEFAULT
                    ).toString(Charsets.UTF_8)
                )

            override fun encode(value: ServerUrl) = Base64.encodeToString(
                value.url.toByteArray(Charsets.UTF_8),
                Base64.DEFAULT
            )
        }
    )

    private val workAdapter = Work.Adapter(
        serverIdAdapter = object : ColumnAdapter<ServerId, Long> {
            override fun decode(databaseValue: Long) =
                ServerId(databaseValue)

            override fun encode(value: ServerId) = value.id
        },
        userIdAdapter = object : ColumnAdapter<UserId, Long> {
            override fun decode(databaseValue: Long) =
                UserId(databaseValue)

            override fun encode(value: UserId) = value.id
        },
        entryIdAdapter = object : ColumnAdapter<EntryId, Long> {
            override fun decode(databaseValue: Long) =
                EntryId(databaseValue)

            override fun encode(value: EntryId) = value.id
        },
        workTypeAdapter = object : ColumnAdapter<WorkType, Long> {
            override fun decode(databaseValue: Long) =
                WorkType(databaseValue)

            override fun encode(value: WorkType) = value.type
        }
    )

    private val database = Database(
        driver = sqlDriver,
        entryAdapter = entryAdapter,
        feedAdapter = feedAdapter,
        categoryAdapter = categoryAdapter,
        userAdapter = userAdapter,
        meAdapter = meAdapter,
        settingsAdapter = settingsAdapter,
        serverAdapter = serverAdapter,
        workAdapter = workAdapter
    )

    val entryQueries = database.entryQueries

    val feedQueries = database.feedQueries

    val categoryQueries = database.categoryQueries

    val userQueries = database.userQueries

    val meQueries = database.meQueries

    val settingsQueries = database.settingsQueries

    val serverQueries = database.serverQueries

    val workQueries = database.workQueries

    fun upsertAccount(
        serverId: ServerId,
        userName: UserName,
        userPassword: UserPassword,
        me: Me
    ): Account {
        database.transaction {
            userQueries.upsert(
                User(
                    serverId = serverId,
                    userId = me.userId,
                    userName = userName,
                    userPassword = userPassword,
                    userSelected = UserSelected.UNSELECTED,
                    userFirstTimeRun = UserFirstTimeRun.TRUE
                )
            )
            meQueries.upsert(me)
            settingsQueries.insert(
                Settings(
                    serverId = serverId,
                    userId = me.userId,
                    settingsTheme = SettingsTheme.AUTO,
                    settingsAllowImagePreview = SettingsAllowImagePreview.ON
                )
            )
        }
        return userQueries.selectUser(serverId = serverId, userId = me.userId)
    }
}
