package com.eliranrp.score421

import android.app.Application
import com.eliranrp.score421.data.PlayerNamesStore

class Score421Application : Application() {
    lateinit var namesStore: PlayerNamesStore
        private set

    override fun onCreate() {
        super.onCreate()
        namesStore = PlayerNamesStore(this)
    }
}
