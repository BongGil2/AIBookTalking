package com.friendsmon.fmlibrary.tools

import android.content.Context

class FMSharedPreferences(context: Context) {
    private val mContext: Context = context

    /**
     * SharedPreferences 에 String 값을 저장
     */
    fun saveData(db: String, key: String, value: String) {
        val sp = mContext.getSharedPreferences(db, Context.MODE_PRIVATE)
        val edit = sp.edit()

        edit.putString(key, value)
        edit.apply()
    }

    /**
     * SharedPreferences 에 Boolean 값을 저장
     */
    fun saveData(db: String, key: String, value: Boolean) {
        val sp = mContext.getSharedPreferences(db, Context.MODE_PRIVATE)
        val edit = sp.edit()

        edit.putBoolean(key, value)
        edit.apply()
    }

    /**
     * SharedPreferences 에 Int 값을 저장
     */
    fun saveData(db: String, key: String, value: Int) {
        val sp = mContext.getSharedPreferences(db, Context.MODE_PRIVATE)
        val edit = sp.edit()

        edit.putInt(key, value)
        edit.apply()
    }

    /**
     * SharedPreferences에서 String 값을 가져온다.
     */
    fun getData(db: String, key: String, default: String): String {
        val sp = mContext.getSharedPreferences(db, Context.MODE_PRIVATE)

        return sp.getString(key, default) ?: default
    }

    /**
     * SharedPreferences에서 Boolean 값을 가져온다.
     */
    fun getData(db: String, key: String, default: Boolean): Boolean {
        val sp = mContext.getSharedPreferences(db, Context.MODE_PRIVATE)

        return sp.getBoolean(key, default)
    }

    /**
     * SharedPreferences에서 Int 값을 가져온다.
     */
    fun getData(db: String, key: String, default: Int): Int {
        val sp = mContext.getSharedPreferences(db, Context.MODE_PRIVATE)

        return sp.getInt(key, default)
    }

    /**
     * SharedPreferences에 모든 데이터를 삭제한다.
     */
    fun reset(db: String) {
        val sp = mContext.getSharedPreferences(db, Context.MODE_PRIVATE)
        val edit = sp.edit()

        edit.clear()
        edit.apply()
    }
}