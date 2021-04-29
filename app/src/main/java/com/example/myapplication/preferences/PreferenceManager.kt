package com.example.myapplication.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    companion object {

        private var mPreferenceManager: PreferenceManager? = null

        /**
         * getInstance method is used to initialize SessionManager singleton
         * instance
         *
         * @param context context instance
         * @return Singleton session manager instance
         */
        fun getInstance(context: Context): PreferenceManager? {
            if (mPreferenceManager == null) {
                mPreferenceManager = PreferenceManager(context)
            }
            return mPreferenceManager
        }
    }

    private var mSharedPreferences: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor

    init {
        if (mSharedPreferences == null)
            mSharedPreferences =
                context.getSharedPreferences(PreferenceHelper.PREFERENCE_NAME, Context.MODE_PRIVATE)
        mEditor = mSharedPreferences!!.edit()
        mEditor.apply()
    }

    fun getAuthToken(): String? {
        return mSharedPreferences!!.getString(PreferenceHelper.AUTH_TOKEN, "")
    }

    fun setAuthToken(authToken: String) {
        mEditor.putString(PreferenceHelper.AUTH_TOKEN, authToken)
        mEditor.commit()
    }

    fun removePreferences() {
        val editor = mSharedPreferences!!.edit()
        editor.clear()
        editor.apply()
    }

    fun setPreferenceData(key: String?, value: String?) {
        mEditor.putString(key, value)
        mEditor.commit()
    }

    fun getPreferenceData(key: String?): String? {
        return mSharedPreferences!!.getString(key, "")
    }
}