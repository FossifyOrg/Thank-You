package org.fossify.thankyou.helpers

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_INCLUDE_STOPPED_PACKAGES
import android.content.pm.PackageManager.SIGNATURE_MATCH
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.fossify.commons.R
import org.fossify.commons.helpers.FONT_TYPE_SYSTEM_DEFAULT
import org.fossify.commons.helpers.MyContentProvider.ACTION_GLOBAL_CONFIG_UPDATED
import org.fossify.commons.helpers.MyContentProvider.COL_ACCENT_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_APP_ICON_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_BACKGROUND_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_FONT_NAME
import org.fossify.commons.helpers.MyContentProvider.COL_FONT_TYPE
import org.fossify.commons.helpers.MyContentProvider.COL_ID
import org.fossify.commons.helpers.MyContentProvider.COL_LAST_UPDATED_TS
import org.fossify.commons.helpers.MyContentProvider.COL_PRIMARY_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_SHOW_CHECKMARKS_ON_SWITCHES
import org.fossify.commons.helpers.MyContentProvider.COL_TEXT_COLOR
import org.fossify.commons.helpers.MyContentProvider.COL_THEME_TYPE
import org.fossify.commons.helpers.MyContentProvider.GLOBAL_THEME_DISABLED

class MyContentProviderHelper private constructor(
    private val context: Context
) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    private val db = writableDatabase

    companion object {
        private const val DB_NAME = "Preferences.db"
        private const val DB_VERSION = 2
        private const val TABLE_NAME = "settings"
        private const val PREF_ID = 1

        fun newInstance(context: Context) = MyContentProviderHelper(context)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_THEME_TYPE INTEGER DEFAULT 0,
                $COL_TEXT_COLOR INTEGER DEFAULT 0,
                $COL_BACKGROUND_COLOR INTEGER DEFAULT 0,
                $COL_PRIMARY_COLOR INTEGER DEFAULT 0,
                $COL_ACCENT_COLOR INTEGER DEFAULT 0,
                $COL_APP_ICON_COLOR INTEGER DEFAULT 0,
                $COL_SHOW_CHECKMARKS_ON_SWITCHES INTEGER DEFAULT 0,
                $COL_LAST_UPDATED_TS INTEGER DEFAULT 0,
                $COL_FONT_TYPE INTEGER DEFAULT 0,
                $COL_FONT_NAME TEXT DEFAULT ''
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COL_FONT_TYPE INTEGER DEFAULT 0")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COL_FONT_NAME TEXT DEFAULT ''")
        }
    }

    private fun initializePreferences() {
        with(context.resources) {
            db.insert(
                TABLE_NAME,
                null,
                ContentValues().apply {
                    put(COL_THEME_TYPE, GLOBAL_THEME_DISABLED)
                    put(COL_TEXT_COLOR, getColor(R.color.theme_dark_text_color))
                    put(COL_BACKGROUND_COLOR, getColor(R.color.theme_dark_background_color))
                    put(COL_PRIMARY_COLOR, getColor(R.color.color_primary))
                    put(COL_ACCENT_COLOR, getColor(R.color.color_primary))
                    put(COL_APP_ICON_COLOR, getColor(R.color.color_primary))
                    put(COL_SHOW_CHECKMARKS_ON_SWITCHES, false)
                    put(COL_LAST_UPDATED_TS, 0)
                    put(COL_FONT_TYPE, FONT_TYPE_SYSTEM_DEFAULT)
                    put(COL_FONT_NAME, "")
                }
            )
        }
    }

    fun updatePreferences(values: ContentValues): Int {
        if (!isPreferenceInitialized()) {
            initializePreferences()
        }

        val selection = "$COL_ID = ?"
        val selectionArgs = arrayOf(PREF_ID.toString())
        val rowsChanged = db.update(TABLE_NAME, values, selection, selectionArgs)
        if (rowsChanged > 0) {
            broadcastChanges()
        }

        return rowsChanged
    }

    private fun isPreferenceInitialized(): Boolean {
        val cols = arrayOf(COL_ID)
        val selection = "$COL_ID = ?"
        val selectionArgs = arrayOf(PREF_ID.toString())
        var cursor: Cursor? = null
        try {
            cursor = db.query(
                TABLE_NAME, cols, selection, selectionArgs, null, null, null
            )

            return cursor.moveToFirst()
        } finally {
            cursor?.close()
        }
    }

    fun getGlobalConfigCursor(): Cursor? {
        val columns = arrayOf(
            COL_THEME_TYPE,
            COL_TEXT_COLOR,
            COL_BACKGROUND_COLOR,
            COL_PRIMARY_COLOR,
            COL_ACCENT_COLOR,
            COL_APP_ICON_COLOR,
            COL_SHOW_CHECKMARKS_ON_SWITCHES,
            COL_LAST_UPDATED_TS,
            COL_FONT_TYPE,
            COL_FONT_NAME,
        )

        val selection = "$COL_ID = ?"
        val selectionArgs = arrayOf(PREF_ID.toString())
        return db.query(
            TABLE_NAME, columns, selection, selectionArgs, null, null, null
        )
    }

    private fun broadcastChanges() {
        val intent = Intent(ACTION_GLOBAL_CONFIG_UPDATED).apply {
            addFlags(FLAG_INCLUDE_STOPPED_PACKAGES)
        }

        val packageName = context.packageName
        val packageManager = context.packageManager
        val packages = packageManager.queryBroadcastReceivers(intent, 0)
            .map { it.activityInfo.applicationInfo.packageName }
            .filter {
                packageManager.checkSignatures(packageName, it) == SIGNATURE_MATCH
            }

        for (`package` in packages) {
            intent.setPackage(`package`)
            context.sendBroadcast(intent)
        }
    }
}
