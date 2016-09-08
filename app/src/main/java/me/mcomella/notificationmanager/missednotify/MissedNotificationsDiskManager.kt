package me.mcomella.notificationmanager.missednotify

import android.content.Context
import com.fasterxml.jackson.module.kotlin.*
import java.io.File

class MissedNotificationsDiskManager(context: Context) {
    private val SCHEMA = 1

    private val file = File(context.filesDir, "missedNotifications${SCHEMA}.json")

    // TODO: lock file or methods
    // TODO: prepend or append?
    fun prependNotificationToDisk(notification: MissedNotification) {
        val onDisk = readNotificationsFromDisk()
        jacksonObjectMapper().writeValue(file, listOf(notification) + onDisk)
    }

    fun readNotificationsFromDisk(): List<MissedNotification> {
        file.createNewFile()
        return if (file.length() == 0L) {
            listOf()
        } else {
            jacksonObjectMapper().readValue(file)
        }
    }
}