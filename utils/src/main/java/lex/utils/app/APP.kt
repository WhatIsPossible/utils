package lex.utils.app

import android.app.Application
import android.content.Context

object APP {

    lateinit var application: Application
        private set

    val context: Context
        get() = application.applicationContext

    fun init(app: Application) {
        this.application = app
    }

}