package com.constantin.microflux.database.util

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteOutOfMemoryException
import com.constantin.microflux.data.Result
import com.constantin.microflux.database.NoUserException

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