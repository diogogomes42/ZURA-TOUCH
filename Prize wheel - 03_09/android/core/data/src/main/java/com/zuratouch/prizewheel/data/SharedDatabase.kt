package com.zuratouch.prizewheel.data

import android.content.Context
import android.util.Log
import java.io.File

/**
 * Customer and Operator apps must read/write the same SQLite file on kiosk devices.
 * [android.content.Context.getDatabasePath] is per-package; [sharedUserId] alone does not
 * merge Room databases — we open the canonical file under the customer app data dir.
 */
object SharedDatabase {
    private const val TAG = "SharedDatabase"

    /** Package that owns the on-disk database directory. */
    const val OWNER_PACKAGE = "com.zuratouch.customer"

    const val DATABASE_NAME = "zura_touch.db"

    fun databaseFile(context: Context): File {
        val file = openContext(context).getDatabasePath(DATABASE_NAME)
        file.parentFile?.mkdirs()
        return file
    }

    fun openContext(context: Context): Context {
        val app = context.applicationContext
        if (app.packageName == OWNER_PACKAGE) return app
        return try {
            app.createPackageContext(OWNER_PACKAGE, Context.CONTEXT_IGNORE_SECURITY)
        } catch (error: Exception) {
            Log.e(
                TAG,
                "Cannot open shared DB at $OWNER_PACKAGE — install both apps with the same " +
                    "signing key and android:sharedUserId. Using local database (changes will not sync).",
                error,
            )
            app
        }
    }
}
