package com.ahuja.sons.globals

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SessionManagement(_context: Context) {

    var _context: Context
    init {
        this._context = _context
    }

    private val NAME = "ProductionApp"
    private val MODE = Context.MODE_PRIVATE

    // todo Shared Preferences
    var pref: SharedPreferences = _context.getSharedPreferences(NAME, MODE)

    //todo  Editor for Shared preferences
    lateinit var editor: SharedPreferences.Editor

    //todo Shared preference String value store..
    fun setSharedPrefernce( key: String, value: String) {
        editor = pref.edit()
        editor.putString(key, value)
        editor.commit()
    }

    private fun getDataFromSharedPreferences(Key: String): String? {
        return try {
            val returnString: String? = pref.getString(Key, null)
            returnString
        } catch (e: java.lang.Exception) {
            ""
        }
    }

    //todo Shared preference int value store..
    fun setIntSharedPrefernce(key: String, value: Int) {
        editor = pref.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    private fun getIntDataFromSharedPreferences(Key: String): Int {
        val returnInt: Int = pref.getInt(Key, 0)
        return returnInt
    }

    fun ClearSession(mContext: Context) {
        val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit()
        editor.clear()
        editor.commit()
    }

    fun setId( id: String?) {
        if (id != null) {
            setSharedPrefernce(Global.LOGIN_ID, id)
        }
    }

    fun getId(): String? {
        return getDataFromSharedPreferences(Global.LOGIN_ID)
    }

    fun saveStringPreferences(mContext: Context, key: String, value: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getPreferencesString(mContext: Context, key: String): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        return sharedPreferences.getString(key, "")
    }

    fun saveBooleanPreferences(mContext: Context, key: String, value: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getPreferencesBoolean(mContext: Context, key: String): Boolean? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        return sharedPreferences.getBoolean(key, false)
    }

    fun saveIntPreferences(mContext: Context, key: String, value: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getPreferencesInt(mContext: Context, key: String): Int? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        return sharedPreferences.getInt(key, 1)
    }


    companion object {
        /*fun saveStringPreferences(mContext: Context, key: String, value: String) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getPreferencesString(mContext: Context, key: String): String? {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            return sharedPreferences.getString(key, "")
        }

        fun saveIntPreferences(mContext: Context, key: String, value: Int) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            val editor = sharedPreferences.edit()
            editor.putInt(key, value)
            editor.apply()
        }

        fun getIntPreferences(mContext: Context, key: String): Int {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            return sharedPreferences.getInt(key, 0)
        }

        fun ClearSession(mContext: Context) {
            val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit()
            editor.clear()
            editor.commit()
        }*/
    }


}
