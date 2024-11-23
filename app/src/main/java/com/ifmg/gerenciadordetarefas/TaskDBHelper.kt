package com.ifmg.gerenciadordetarefas

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "TaskDB"
        private const val TABLE_NAME = "tasks"
        private const val KEY_ID = "id"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DUE_DATE = "due_date"
        private const val KEY_DUE_TIME = "due_time"
        private const val KEY_PRIORITY = "priority"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_DESCRIPTION TEXT,
                $KEY_DUE_DATE TEXT,
                $KEY_DUE_TIME TEXT,
                $KEY_PRIORITY TEXT
            )
        """.trimIndent()

        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTask(task: Task) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_DESCRIPTION, task.description)
        values.put(KEY_DUE_DATE, task.dueDate)
        values.put(KEY_DUE_TIME, task.dueTime)
        values.put(KEY_PRIORITY, task.priority)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val idIndex = it.getColumnIndex(KEY_ID)
                    val descriptionIndex = it.getColumnIndex(KEY_DESCRIPTION)
                    val dueDateIndex = it.getColumnIndex(KEY_DUE_DATE)
                    val dueTimeIndex = it.getColumnIndex(KEY_DUE_TIME)
                    val priorityIndex = it.getColumnIndex(KEY_PRIORITY)

                    val task = Task(
                        it.getLong(idIndex),
                        if (descriptionIndex >= 0) it.getString(descriptionIndex) else "",
                        if (dueDateIndex >= 0) it.getString(dueDateIndex) else "",
                        if (dueTimeIndex >= 0) it.getString(dueTimeIndex) else "",
                        if (priorityIndex >= 0) it.getString(priorityIndex) else ""
                    )
                    taskList.add(task)
                } while (it.moveToNext())
            }
        }

        return taskList
    }

    fun getTask(taskId: Long): Task? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(KEY_ID, KEY_DESCRIPTION, KEY_DUE_DATE, KEY_DUE_TIME, KEY_PRIORITY),
            "$KEY_ID=?",
            arrayOf(taskId.toString()),
            null,
            null,
            null
        )

        return cursor?.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(KEY_ID)
                val descriptionIndex = it.getColumnIndex(KEY_DESCRIPTION)
                val dueDateIndex = it.getColumnIndex(KEY_DUE_DATE)
                val dueTimeIndex = it.getColumnIndex(KEY_DUE_TIME)
                val priorityIndex = it.getColumnIndex(KEY_PRIORITY)

                Task(
                    it.getLong(idIndex),
                    if (descriptionIndex >= 0) it.getString(descriptionIndex) else "",
                    if (dueDateIndex >= 0) it.getString(dueDateIndex) else "",
                    if (dueTimeIndex >= 0) it.getString(dueTimeIndex) else "",
                    if (priorityIndex >= 0) it.getString(priorityIndex) else ""
                )
            } else {
                null
            }
        }
    }

    fun updateTask(task: Task): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_DESCRIPTION, task.description)
        values.put(KEY_DUE_DATE, task.dueDate)
        values.put(KEY_DUE_TIME, task.dueTime)
        values.put(KEY_PRIORITY, task.priority)

        return db.update(TABLE_NAME, values, "$KEY_ID=?", arrayOf(task.id.toString()))
    }

    fun deleteTask(taskId: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$KEY_ID=?", arrayOf(taskId.toString()))
    }
}
