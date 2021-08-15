package me.vlasoff.kotlinopencv

import android.content.Context

class PassportUtils(
    private val context: Context
) {

    private val prefs =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun save(key: String, data: Boolean) {
        prefs.edit().apply {
            putBoolean(key, data)
            apply()
        }
    }

    fun fetch(key: String) = prefs.getString(key, null)
}