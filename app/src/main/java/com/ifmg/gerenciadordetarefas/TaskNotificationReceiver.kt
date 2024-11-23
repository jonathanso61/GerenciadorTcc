package com.ifmg.gerenciadordetarefas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.PendingIntent
import android.util.Log
import androidx.core.app.NotificationCompat

    class TaskNotificationReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                val taskId = intent.getLongExtra("taskId", -1)

                if (taskId != -1L) {
                    val taskDBHelper = TaskDBHelper(context)
                    val task = taskDBHelper.getTask(taskId)

                    if (task != null) {
                        val notificationIntent = Intent(context, MainActivity::class.java)
                        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

                        val pendingIntent = PendingIntent.getActivity(
                            context,
                            task.id.toInt(),
                            notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        val notificationBuilder = NotificationCompat.Builder(context, "default")
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle("Tarefa Agendada")
                            .setContentText("Você agendou uma tarefa para agora: ${task.description}")
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)

                        val notificationManager =
                            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(task.id.toInt(), notificationBuilder.build())
                    }
                }
            }
        }
    //constroi e exibi notificações na barra de notificação
    private fun showNotification(context: Context, task: Task) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(
            context,
            task.id.toInt(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(context, "default")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Tarefa Agendada")
            .setContentText("Você agendou uma tarefa para agora: ${task.description}")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(task.id.toInt(), notificationBuilder.build())
    }

}