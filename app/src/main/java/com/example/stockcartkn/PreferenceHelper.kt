package com.example.stockcartkn

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper (private val context : Context) {

    private val INTRO = "intro"
    private val USERNAME = "username"
    private val FIRSTNAME = "first_name"
    private val VINNO = "vinno"
    private val LCSTOCKID = "stocklocation_id"
    private val LCSTOCK = "locationstock"
    private val PARKINGLOT = "parkinglot"
    private val USER = "lastupdateuserid"
    private val CARID = "carid"
    private val KAKUDAI = "kakudai"

    private val app_prefs: SharedPreferences

    init {
        app_prefs = context.getSharedPreferences(
            "shared",
            Context.MODE_PRIVATE
        )
    }

    fun putIsLogin(loginorout: Boolean){
        val edit = app_prefs.edit()
        edit.putBoolean(INTRO, loginorout)
        edit.commit()
    }
    fun getIsLogin(): Boolean{
        return app_prefs.getBoolean(INTRO,false)
    }

    fun putName(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(USERNAME, loginorout)
        edit.commit()
    }
    fun getNames(): String? {
        return app_prefs.getString(USERNAME, "")
    }

    fun putHobby(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(FIRSTNAME, loginorout)
        edit.commit()
    }
    fun getHobbys(): String? {
        return app_prefs.getString(FIRSTNAME, "")
    }

    fun putIsCheckVinno(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(VINNO, loginorout)
        edit.commit()
    }
    fun getCheckVinno(): String? {
        return app_prefs.getString(VINNO, "")
    }

    fun putLcStockID(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(LCSTOCKID, loginorout)
        edit.commit()
    }
    fun getlcstockID(): String? {
        return app_prefs.getString(LCSTOCKID, "")
    }

    fun putLcStockDes(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(LCSTOCK, loginorout)
        edit.commit()
    }
    fun getlcstockdes(): String? {
        return app_prefs.getString(LCSTOCK, "")
    }

    fun putParklot(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(PARKINGLOT, loginorout)
        edit.commit()
    }
    fun getParkinglot(): String? {
        return app_prefs.getString(PARKINGLOT, "")
    }

    fun putUser(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(USER, loginorout)
        edit.commit()
    }
    fun getUser(): String? {
        return app_prefs.getString(USER, "")
    }

    fun putCarid(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(CARID, loginorout)
        edit.commit()
    }
    fun getCarid(): String? {
        return app_prefs.getString(CARID, "")
    }

    fun putkakudai(loginorout: String) {
        val edit = app_prefs.edit()
        edit.putString(KAKUDAI, loginorout)
        edit.commit()
    }
    fun getkakudai(): String? {
        return app_prefs.getString(KAKUDAI, "")
    }

}
