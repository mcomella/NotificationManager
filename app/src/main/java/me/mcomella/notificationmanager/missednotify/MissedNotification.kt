package me.mcomella.notificationmanager.missednotify

data class MissedNotification(val pkgname: String,
                              val posttime: Long,
                              val title: String,
                              val contenttext: String)
