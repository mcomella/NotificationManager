package me.mcomella.notificationmanager.ext

import java.lang.ref.WeakReference

fun <T> WeakReference<T>.use(fn: (T) -> Unit) {
    val value = this.get()
    if (value != null) fn(value)
}
