package org.fossify.thankyou.contentproviders

import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri
import org.fossify.thankyou.dbhelpers.MyContentProviderDbHelper
import org.fossify.thankyou.extensions.config

class MyContentProvider : ContentProvider() {
    lateinit var dbHelper: MyContentProviderDbHelper

    override fun insert(uri: Uri, contentValues: ContentValues?) = null

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?) =
        dbHelper.getSharedTheme()

    override fun onCreate(): Boolean {
        dbHelper = MyContentProviderDbHelper.newInstance(context!!)
        return true
    }

    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        context?.config?.shouldUseSharedTheme = true
        return dbHelper.updateTheme(contentValues!!)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun getType(uri: Uri) = ""
}
