package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity

object NotificationHelper {
    const val CHANNEL_REPAIRS_ID = "ajusta_repairs_channel"
    const val CHANNEL_REPAIRS_NAME = "Progresso de Reparos"

    const val CHANNEL_TIPS_ID = "ajusta_tips_channel"
    const val CHANNEL_TIPS_NAME = "Dicas & Tutoriais de Costura"

    const val ACTION_REPAIR_REMINDER = "com.example.ajusta.ACTION_REPAIR_REMINDER"
    const val ACTION_SEWING_TIP = "com.example.ajusta.ACTION_SEWING_TIP"

    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_MESSAGE = "extra_message"
    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val repairChannel = NotificationChannel(
                CHANNEL_REPAIRS_ID,
                CHANNEL_REPAIRS_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Lembretes sobre peças em andamento no ateliê e prazos de retirada."
                enableVibration(true)
            }

            val tipsChannel = NotificationChannel(
                CHANNEL_TIPS_ID,
                CHANNEL_TIPS_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Novas técnicas de alfaiataria manual e segredos de costura caseira."
            }

            notificationManager.createNotificationChannel(repairChannel)
            notificationManager.createNotificationChannel(tipsChannel)
        }
    }

    fun showRepairNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = 1001
    ) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_REPAIRS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted yet
        }
    }

    fun showSewingTipNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = 1002
    ) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_TIPS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted yet
        }
    }

    fun scheduleReminder(
        context: Context,
        title: String,
        message: String,
        delaySeconds: Long,
        isTip: Boolean = false,
        requestCode: Int = 2001
    ) {
        createNotificationChannels(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val action = if (isTip) ACTION_SEWING_TIP else ACTION_REPAIR_REMINDER
        val intent = Intent(context, RepairNotificationReceiver::class.java).apply {
            this.action = action
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_NOTIFICATION_ID, requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val triggerAtMillis = System.currentTimeMillis() + (delaySeconds * 1000)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // Fallback for devices restricting exact alarms
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, requestCode: Int = 2001) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, RepairNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
