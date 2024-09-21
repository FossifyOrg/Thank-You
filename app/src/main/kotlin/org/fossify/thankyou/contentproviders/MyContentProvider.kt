package org.fossify.thankyou.contentproviders

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import org.fossify.thankyou.helpers.MyContentProviderHelper

class MyContentProvider : ContentProvider() {
    private lateinit var dbHelper: MyContentProviderHelper

    override fun insert(uri: Uri, contentValues: ContentValues?) = null

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        return dbHelper.getGlobalConfigCursor()
    }

    override fun onCreate(): Boolean {
        dbHelper = MyContentProviderHelper.newInstance(context!!)
        return true
    }

    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return dbHelper.updatePreferences(contentValues!!)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun getType(uri: Uri) = ""
}
