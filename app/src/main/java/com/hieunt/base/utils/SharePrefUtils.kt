package com.hieunt.base.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharePrefUtils @Inject constructor(@ApplicationContext context: Context) {
    private val pre: SharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pre.edit()

    var isRated
        get() = pre.getBoolean("rated", false)
        set(value) {
            editor.putBoolean("rated", value)
            editor.apply()
        }

    var isPassPermission
        get() = pre.getBoolean("pass_permission", false)
        set(value) {
            editor.putBoolean("pass_permission", value)
            editor.apply()
        }

    var countExitApp
        get() = pre.getInt("count_exit_app", 1)
        set(value) {
            editor.putInt("count_exit_app", value)
            editor.apply()
        }

    var isFirstSelectLanguage
        get() = pre.getBoolean("isFirstSelectLanguage", true)
        set(value) {
            editor.putBoolean("isFirstSelectLanguage", value)
            editor.apply()
        }

    var countOpenApp
        get() = pre.getInt("countOpenApp", 0)
        set(value) {
            editor.putInt("countOpenApp", value)
            editor.apply()
        }

    var countOpenHome
        get() = pre.getInt("countOpenHome", 1)
        set(value) {
            editor.putInt("countOpenHome", value)
            editor.apply()
        }


    var countOpenAppTestFlow
        get() = pre.getInt("countOpenAppTestFlow", 0)
        set(value) {
            editor.putInt("countOpenAppTestFlow", value)
            editor.apply()
        }
}