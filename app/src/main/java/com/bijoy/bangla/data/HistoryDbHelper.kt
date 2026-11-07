package com.bijoy.bangla.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class HistoryItem(
    val id: Long,
    val content: String,
    val packageName: String,
    val contactName: String?,
    val layoutUsed: String,
    val timestamp: Long,
    val isClipboard: Boolean
)

class HistoryDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bijoy_history.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "typing_history"
        private const val COL_ID = "id"
        private const val COL_CONTENT = "content"
        private const val COL_PACKAGE = "package_name"
        private const val COL_CONTACT = "contact_name"
        private const val COL_LAYOUT = "layout_used"
        private const val COL_TIMESTAMP = "timestamp"
        private const val COL_IS_CLIPBOARD = "is_clipboard"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createSql = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CONTENT TEXT NOT NULL,
                $COL_PACKAGE TEXT NOT NULL,
                $COL_CONTACT TEXT,
                $COL_LAYOUT TEXT NOT NULL,
                $COL_TIMESTAMP INTEGER NOT NULL,
                $COL_IS_CLIPBOARD INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createSql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertRecord(
        content: String,
        packageName: String,
        contactName: String?,
        layoutUsed: String,
        isClipboard: Boolean = false
    ): Long {
        if (content.isBlank()) return -1
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_CONTENT, content.trim())
            put(COL_PACKAGE, packageName)
            put(COL_CONTACT, contactName)
            put(COL_LAYOUT, layoutUsed)
            put(COL_TIMESTAMP, System.currentTimeMillis())
            put(COL_IS_CLIPBOARD, if (isClipboard) 1 else 0)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllRecords(query: String = ""): List<HistoryItem> {
        val list = mutableListOf<HistoryItem>()
        val db = readableDatabase
        val selection = if (query.isBlank()) null else "$COL_CONTENT LIKE ? OR $COL_CONTACT LIKE ?"
        val selectionArgs = if (query.isBlank()) null else arrayOf("%$query%", "%$query%")

        val cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "$COL_TIMESTAMP DESC",
            "200"
        )

        cursor.use {
            val idIndex = it.getColumnIndexOrThrow(COL_ID)
            val contentIndex = it.getColumnIndexOrThrow(COL_CONTENT)
            val pkgIndex = it.getColumnIndexOrThrow(COL_PACKAGE)
            val contactIndex = it.getColumnIndexOrThrow(COL_CONTACT)
            val layoutIndex = it.getColumnIndexOrThrow(COL_LAYOUT)
            val timeIndex = it.getColumnIndexOrThrow(COL_TIMESTAMP)
            val clipIndex = it.getColumnIndexOrThrow(COL_IS_CLIPBOARD)

            while (it.moveToNext()) {
                list.add(
                    HistoryItem(
                        id = it.getLong(idIndex),
                        content = it.getString(contentIndex),
                        packageName = it.getString(pkgIndex),
                        contactName = it.getString(contactIndex),
                        layoutUsed = it.getString(layoutIndex),
                        timestamp = it.getLong(timeIndex),
                        isClipboard = it.getInt(clipIndex) == 1
                    )
                )
            }
        }
        return list
    }

    fun getRecentRecords(limit: Int = 5): List<HistoryItem> {
        val list = mutableListOf<HistoryItem>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$COL_TIMESTAMP DESC",
            limit.toString()
        )
        cursor.use {
            val idIndex = it.getColumnIndexOrThrow(COL_ID)
            val contentIndex = it.getColumnIndexOrThrow(COL_CONTENT)
            val pkgIndex = it.getColumnIndexOrThrow(COL_PACKAGE)
            val contactIndex = it.getColumnIndexOrThrow(COL_CONTACT)
            val layoutIndex = it.getColumnIndexOrThrow(COL_LAYOUT)
            val timeIndex = it.getColumnIndexOrThrow(COL_TIMESTAMP)
            val clipIndex = it.getColumnIndexOrThrow(COL_IS_CLIPBOARD)
            while (it.moveToNext()) {
                list.add(
                    HistoryItem(
                        id = it.getLong(idIndex),
                        content = it.getString(contentIndex),
                        packageName = it.getString(pkgIndex),
                        contactName = it.getString(contactIndex),
                        layoutUsed = it.getString(layoutIndex),
                        timestamp = it.getLong(timeIndex),
                        isClipboard = it.getInt(clipIndex) == 1
                    )
                )
            }
        }
        return list
    }

    fun deleteRecord(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun clearAllHistory(): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, null, null)
    }
}
