package me.mcomella.notificationmanager

import android.content.Context
import com.fasterxml.jackson.module.kotlin.*
import java.io.File

class DiskManager(context: Context) {
    private val SCHEMA = 1 // to iterate on format without causing crashes.

    val file = File(context.filesDir, "apps${SCHEMA}.json")

    fun saveAppsToDisk(apps: List<BlockedAppInfo>) {
        jacksonObjectMapper().writeValue(file, apps)
    }

    fun readAppsFromDisk(): List<BlockedAppInfo> {
        file.createNewFile()
        return if (file.length() == 0L) {
            listOf()
        } else {
            jacksonObjectMapper().readValue(file)
        }
    }
}
