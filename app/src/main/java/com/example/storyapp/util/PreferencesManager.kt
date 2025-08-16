package com.example.storyapp.util

import android.content.Context

object PreferencesManager {

    private const val PREF_NAME = "story_app_pref"
    private const val KEY_TOKEN = "user_token"

    fun saveToken(context: Context, token: String){
        val shared = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
        shared.edit().putString(KEY_TOKEN,token).apply()
    }

    fun getToken(context: Context) : String? {
        val shared = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
        return shared.getString(KEY_TOKEN,null)
    }

    fun clearToken(context: Context) {
        val shared = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        shared.edit().remove(KEY_TOKEN).apply()
    }
}