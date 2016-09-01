package me.mcomella.notificationmanager

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationService : NotificationListenerService() {
    private val TAG = "lol"
    private val FOREGROUND_ID = 4321

    private val foregroundNotification: Notification
        get() = Notification.Builder(this).
                setContentTitle("Notification Manager").
                setContentText("Listening and blocking specified notifications..."). // TODO: doesn't appear
                build()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        startForeground(FOREGROUND_ID, foregroundNotification)
        return super.onStartCommand(intent, flags, startId) // TODO: sticky?
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        Log.d(TAG, "onNotificationPosted: " + sbn)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind")
        return super.onBind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "listenerConnected")
    }
}
