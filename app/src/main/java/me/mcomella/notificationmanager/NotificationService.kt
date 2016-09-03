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

    /*
    private val diskManager: DiskManager
        get() = DiskManager(this) // TODO: fragile. underlying filesDir is null at init time but not when accessed later.
    private val appsAndState: Map<String, Boolean>
        get() = diskManager.readUserContextsFromDisk() // TODO: getting from disk each time inefficient. send refresh signals.
        */

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        startForeground(FOREGROUND_ID, foregroundNotification)
        return super.onStartCommand(intent, flags, startId) // TODO: sticky?
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        Log.d(TAG, "onNotificationPosted: " + sbn)
        /*
        if (!appsAndState.getOrElse(sbn.packageName, {true})) {
            Log.d(TAG, "Cancelling notification for package: " + sbn.packageName)
            cancelNotification(sbn.key)
        }
        */
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
