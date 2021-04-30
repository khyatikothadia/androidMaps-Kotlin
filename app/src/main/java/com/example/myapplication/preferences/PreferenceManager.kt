package com.example.myapplication.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    companion object {

        private lateinit var mPreferenceManager: PreferenceManager
        private lateinit var mSharedPreferences: SharedPreferences
        private lateinit var mEditor: SharedPreferences.Editor

        /**
         * getInstance method is used to initialize PreferenceManager singleton instance
         *
         * @param context context instance
         * @return Singleton PreferenceManager instance
         */
        fun getInstance(context: Context): PreferenceManager {
            mPreferenceManager = PreferenceManager(context)
            return mPreferenceManager
        }
    }

    init {
        mSharedPreferences =
            context.getSharedPreferences(PreferenceHelper.PREFERENCE_NAME, Context.MODE_PRIVATE)
        mEditor = mSharedPreferences.edit()
        mEditor.apply()
    }

    fun getAuthToken(): String? {
        return mSharedPreferences.getString(PreferenceHelper.AUTH_TOKEN, "")
    }

    fun setAuthToken(authToken: String) {
        mEditor.putString(PreferenceHelper.AUTH_TOKEN, authToken)
        mEditor.commit()
    }

    fun removePreferences() {
        val editor = mSharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun setPreferenceData(key: String, value: String?) {
        mEditor.putString(key, value)
        mEditor.commit()
    }

    fun getPreferenceData(key: String): String? {
        return mSharedPreferences.getString(key, "")
    }
}