package com.ifmg.gerenciadordetarefas

import java.text.SimpleDateFormat
import java.util.Locale

data class Task(
    var id: Long = 0,
    var description: String = "",
    var dueDate: String = "",
    var dueTime: String = "",
    var priority: String = ""
) {
    fun isValid(): Boolean {
        return description.isNotBlank() && dueDate.isNotBlank() && dueTime.isNotBlank() && priority.isNotBlank()
    }

    fun getDateTimeMillis(): Long {
        val dateTimeString = "$dueDate $dueTime"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = dateFormat.parse(dateTimeString)
        return date?.time ?: 0
    }

}
