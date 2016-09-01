package me.mcomella.notificationmanager

import android.content.Context
import org.json.JSONObject
import java.io.File

class DiskManager(context: Context) {
    val file = File(context.filesDir, "applicationMap.json")

    fun saveApplicationsToDisk(applicationMap: Map<String, Boolean>) {
        val json = applicationMapToJSON(applicationMap)
        file.writeText(json.toString())
    }

    private fun applicationMapToJSON(applicationList: Map<String, Boolean>): JSONObject {
        val json = JSONObject()
        for ((pkgName, isChecked) in applicationList) {
            json.put(pkgName, isChecked)
        }
        return json
    }

    fun readApplicationsFromDisk(): MutableMap<String, Boolean> {
        file.createNewFile()
        val onDisk = file.readText()
        return if (onDisk.isEmpty()) {
            mutableMapOf()
        } else {
            val json = JSONObject(onDisk)
            jsonToApplicationMap(json)
        }
    }

    private fun jsonToApplicationMap(json: JSONObject): MutableMap<String, Boolean> {
        val applicationMap: MutableMap<String, Boolean> = mutableMapOf()
        for (key in json.keys()) {
            val isChecked = json.getBoolean(key)
            applicationMap.put(key, isChecked)
        }
        return applicationMap
    }
}