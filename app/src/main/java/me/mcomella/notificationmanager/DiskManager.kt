package me.mcomella.notificationmanager

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class DiskManager(context: Context) {
    private val KEY_NAME = "name"
    private val KEY_APPS = "apps"

    val file = File(context.filesDir, "userContexts.json")

    fun saveUserContextsToDisk(userContexts: List<UserContext>) {
        val json = userContextsToJSON(userContexts)
        file.writeText(json.toString())
    }

    private fun userContextsToJSON(userContexts: List<UserContext>): JSONArray {
        val json = JSONArray()
        for ((name, apps) in userContexts) {
            val jsonApps = listToJSONArray(apps)

            val userContextObj = JSONObject()
            userContextObj.put(KEY_NAME, name)
            userContextObj.put(KEY_APPS, jsonApps)

            json.put(userContextObj)
        }
        return json
    }

    private fun listToJSONArray(list: List<String>): JSONArray {
        val json = JSONArray()
        list.forEach { json.put(it) }
        return json
    }

    fun readUserContextsFromDisk(): List<UserContext> {
        file.createNewFile() // only if does not already exist.
        val onDisk = file.readText()
        return if (onDisk.isEmpty()) {
            getDefaultUserContexts()
        } else {
            val json = JSONArray(onDisk)
            jsonToUserContexts(json)
        }
    }

    private fun jsonToUserContexts(json: JSONArray): List<UserContext> {
        val userContexts = mutableListOf<UserContext>()
        for (i in 0..json.length() - 1) {
            val userContextJSON = json.getJSONObject(i)
            val name = userContextJSON.getString(KEY_NAME)
            val apps = jsonArrayToList(userContextJSON.getJSONArray(KEY_APPS))
            val userContext = UserContext(name = name, apps = apps)
            userContexts.add(userContext)
        }
        return userContexts
    }

    // TODO: Will be more than String.
    private fun jsonArrayToList(json: JSONArray): List<String> {
        val out = mutableListOf<String>()
        for (i in 0..json.length() - 1) {
            out.add(json.getString(i))
        }
        return out
    }

    private fun getDefaultUserContexts() = listOf(
            UserContext("Always on", listOf<String>()))
}