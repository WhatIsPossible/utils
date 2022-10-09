package lex.utils

import android.app.Application
import lex.utils.app.APP

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        APP.init(this)
    }
}