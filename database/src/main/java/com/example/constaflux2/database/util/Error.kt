package com.example.constaflux2.database.util

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteOutOfMemoryException
import com.example.constaflux2.data.Result
import com.example.constaflux2.database.NoUserException

inline fun error(
    block: () -> Unit
) = try {
    block()
    Result.success()
} catch (e: NoUserException) {
    Result.Error.DatabaseError.NoUserError
} catch (e: SQLiteConstraintException) {
    Result.Error.DatabaseError.InsertionError
} catch (e: SQLiteOutOfMemoryException) {
    Result.Error.DatabaseError.NoMemoryError
}