package lex.utils.app

import android.app.Application
import android.content.Context

object APP {

    private lateinit var app: Application

    val context: Context
        get() = app.applicationContext

    fun init(app: Application) {
        this.app = APP.app
    }

}