package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RepairNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val title = intent.getStringExtra(NotificationHelper.EXTRA_TITLE)
            ?: "Ajusta: Atualização do seu Ateliê"
        val message = intent.getStringExtra(NotificationHelper.EXTRA_MESSAGE)
            ?: "Verifique o status do seu reparo ou nova dica de costura."
        val notificationId = intent.getIntExtra(NotificationHelper.EXTRA_NOTIFICATION_ID, 1001)

        when (action) {
            NotificationHelper.ACTION_REPAIR_REMINDER -> {
                NotificationHelper.showRepairNotification(
                    context = context,
                    title = title,
                    message = message,
                    notificationId = notificationId
                )
            }
            NotificationHelper.ACTION_SEWING_TIP -> {
                NotificationHelper.showSewingTipNotification(
                    context = context,
                    title = title,
                    message = message,
                    notificationId = notificationId
                )
            }
        }
    }
}
