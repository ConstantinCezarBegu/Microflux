package com.constantin.microflux.data

class InvalidExtractErrorException(message: String = "Cannot extract a error or success containing data.") :
    Exception(message)

sealed class Result<out T> {

    companion object {
        fun success() = Success.Empty

        fun <T> success(data: T) =
            Success(data)

        fun inProgress() = InProgress

        fun complete() = Complete

        fun <T> userValidation(validUsers: T, invalidUsers: T) =
            UserValidation(
                validUsers,
                invalidUsers
            )

        fun <T> notificationInformation(notifications: T) =
            NotificationInformation(
                notifications
            )
    }

    data class Success<out T>(val data: T) : Result<T>() {
        override fun toString() = "Success: $data"

        companion object {
            val Empty = Success(Unit)
        }
    }

    object InProgress : Result<Nothing>() {
        override fun toString() = "InProgress: No Return"
    }

    object Complete : Result<Nothing>() {
        override fun toString() = "Complete: No Return"
    }

    data class UserValidation<out T>(val validUsers: T, val invalidUsers: T) : Result<T>() {
        override fun toString() = "Valid users: $validUsers\tInvalid users: $invalidUsers"
    }

    data class NotificationInformation<out T>(val notifications: T) : Result<T>() {
        override fun toString() = "Notifications: $notifications"
    }

    sealed class Error : Result<Nothing>() {

        sealed class NetworkError : Error() {

            object ServerUrlError : NetworkError() {
                override fun toString() = "ServerUrlError: No Return"
            }

            object ConnectivityError : NetworkError() {
                override fun toString() = "ConnectivityError: No Return"
            }

            object AuthorizationError : NetworkError() {
                override fun toString() = "AuthorizationError: No Return"
            }

            object RedirectResponseError : NetworkError() {
                override fun toString() = "RedirectResponseError: No Return"
            }

            object ServerResponseError : NetworkError() {
                override fun toString() = "ServerResponseError: No Return"
            }

            object ResponseError : NetworkError() {
                override fun toString() = "ResponseError: No Return"
            }

            object IOError : NetworkError() {
                override fun toString() = "IOError: No Return"
            }
        }

        sealed class DatabaseError : Error() {

            object InsertionError : DatabaseError() {
                override fun toString() = "InsertionError: No Return"
            }

            object NoMemoryError : DatabaseError() {
                override fun toString() = "NoMemoryError: No Return"
            }

            object NoUserError : DatabaseError() {
                override fun toString() = "NoUseError: No Return"
            }
        }

    }

    fun extractError(): Result<Unit> = when (this) {
        is Error -> this
        else -> throw InvalidExtractErrorException()
    }

    fun isAccountError() =
        this is Error.NetworkError.AuthorizationError
                || this is Error.NetworkError.ServerUrlError

}