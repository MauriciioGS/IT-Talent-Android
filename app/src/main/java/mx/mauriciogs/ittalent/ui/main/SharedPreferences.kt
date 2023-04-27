package mx.mauriciogs.ittalent.ui.main

import android.annotation.SuppressLint
import android.content.Context
import mx.mauriciogs.ittalent.R

class SharedPreferences private constructor(context: Context) {
    private val mPreferences: android.content.SharedPreferences
    private var mEditor: android.content.SharedPreferences.Editor? = null

    init {
        mPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    @SuppressLint("CommitPrefEdits")
    private fun doEdit() {
        if (mEditor == null) {
            mEditor = mPreferences.edit()
        }
    }

    private fun doApply() {
        if (mEditor != null) {
            mEditor!!.apply()
            mEditor = null
        }
    }

    fun getString(key: String?): String? {
        return mPreferences.getString(key, "")
    }

    fun putString(key: String?, value: String?) {
        doEdit()
        mEditor!!.putString(key, value)
        doApply()
    }

    fun putFcmToken(token: String?) {
        doEdit()
        mEditor!!.putString(FCM_TOKEN, token)
        doApply()
    }

    val fcmToken: String?
        get() = mPreferences.getString(FCM_TOKEN, "")

    fun clearAllPreferences() {
        val editor = mPreferences.edit()
        editor.clear().apply()
    }

    companion object {
        var instance: SharedPreferences? = null
        var FCM_TOKEN = "FCM_TOKEN"
        fun init(context: Context) {
            instance = SharedPreferences(context)
        }
    }
}
