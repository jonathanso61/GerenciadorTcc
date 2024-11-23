package com.ifmg.gerenciadordetarefas

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {

    private lateinit var btnSaveTask: Button
    private lateinit var editTextDescription: EditText
    private lateinit var editTextDueDate: EditText
    private lateinit var editTextDueTime: EditText
    private lateinit var radioButtonGreen: RadioButton
    private lateinit var radioButtonRed: RadioButton
    private lateinit var radioButtonYellow: RadioButton

    private val calendar: Calendar = Calendar.getInstance()
    private var editingTask: Task? = null // Nova variável para armazenar a tarefa sendo editada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtaskactivity)

        initViews()

        // Crie o canal de notificação
        createNotificationChannel()

        btnSaveTask.setOnClickListener {
            val task = createTaskFromInput()

            if (task.isValid()) {
                if (editingTask != null) {
                    // Se está editando, atualize a tarefa existente
                    task.id = editingTask!!.id // Mantenha o mesmo ID
                    TaskDBHelper(this).updateTask(task)
                    scheduleNotification(task)
                    showSnackbar("Tarefa atualizada com sucesso")
                } else {
                    // Se não está editando, adicione uma nova tarefa
                    TaskDBHelper(this).addTask(task)
                    scheduleNotification(task)
                    showSnackbar("Tarefa adicionada com sucesso")
                }

                finish()
            } else {
                showSnackbar("Preencha todos os campos")
            }
        }

        editTextDueDate.setOnClickListener { showDatePickerDialog() }
        editTextDueTime.setOnClickListener { showTimePickerDialog() }

        // Verifique se há uma tarefa para edição
        val taskId = intent.getLongExtra("taskId", -1)
        if (taskId != -1L) {
            // Se houver um ID válido, carregue os detalhes da tarefa
            loadTaskDetails(taskId)
        }
    }

    private fun initViews() {
        btnSaveTask = findViewById(R.id.btnSaveTask)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextDueDate = findViewById(R.id.editTextDueDate)
        editTextDueTime = findViewById(R.id.editTextDueTime)
        radioButtonGreen = findViewById(R.id.radioButtonGreen)
        radioButtonRed = findViewById(R.id.radioButtonRed)
        radioButtonYellow = findViewById(R.id.radioButtonYellow)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default"
            val channelName = "Default Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun scheduleNotification(task: Task) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val notificationIntent = Intent(this, TaskNotificationReceiver::class.java)
        notificationIntent.putExtra("taskId", task.id)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            task.id.toInt(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            // Configurar a data e hora da tarefa
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val dueDate = task.dueDate
            val dueTime = task.dueTime

            val parsedDueDate = dateFormat.parse(dueDate)
            val parsedDueTime = timeFormat.parse(dueTime)

            if (parsedDueDate != null && parsedDueTime != null) {
                time = parsedDueDate
                set(Calendar.HOUR_OF_DAY, parsedDueTime.hours)
                set(Calendar.MINUTE, parsedDueTime.minutes)
                set(Calendar.SECOND, 0)
            }
        }

        val dateTime = calendar.timeInMillis

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager != null) {
                try {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            dateTime,
                            pendingIntent
                        )
                    } else {

                        showToast("O aplicativo não tem permissão para agendar alarmes exatos.")
                    }
                } catch (e: SecurityException) {

                    showToast("O aplicativo não tem permissão para agendar alarmes exatos.")
                }
            }
        } else if (alarmManager != null) {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                dateTime,
                pendingIntent
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun createTaskFromInput(): Task {
        return Task(
            description = editTextDescription.text.toString(),
            dueDate = editTextDueDate.text.toString(),
            dueTime = editTextDueTime.text.toString(),
            priority = getSelectedPriority()
        )
    }

    private fun getSelectedPriority(): String {
        return when {
            radioButtonGreen.isChecked -> "Green"
            radioButtonRed.isChecked -> "Red"
            radioButtonYellow.isChecked -> "Yellow"
            else -> ""
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDueDate()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateDueTime()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun updateDueDate() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        editTextDueDate.setText(dateFormat.format(calendar.time))
    }

    private fun updateDueTime() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        editTextDueTime.setText(timeFormat.format(calendar.time))
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(btnSaveTask, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun loadTaskDetails(taskId: Long) {
        val task = TaskDBHelper(this).getTask(taskId)

        if (task != null) {

            editingTask = task

            editTextDescription.setText(task.description)
            editTextDueDate.setText(task.dueDate)
            editTextDueTime.setText(task.dueTime)

            when (task.priority) {
                "Green" -> radioButtonGreen.isChecked = true
                "Red" -> radioButtonRed.isChecked = true
                "Yellow" -> radioButtonYellow.isChecked = true
            }

            btnSaveTask.text = "Atualizar Tarefa"
        }
    }
}
