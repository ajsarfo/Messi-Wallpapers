package com.sarftec.messiwallpapers.view.handler

import android.app.UiModeManager
import android.content.Context.UI_MODE_SERVICE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class NightModeHandler(activity: AppCompatActivity) {

    private val uiManager = activity.getSystemService(UI_MODE_SERVICE) as UiModeManager

    fun getMode() : Mode {
       return when(uiManager.nightMode) {
           UiModeManager.MODE_NIGHT_YES -> Mode.NIGHT
           else -> Mode.DAY
       }
    }

   fun changeMode(mode: Mode) {
        val value = when(mode) {
            Mode.DAY -> UiModeManager.MODE_NIGHT_NO
            Mode.NIGHT -> UiModeManager.MODE_NIGHT_YES
        }
        uiManager.nightMode = value
        AppCompatDelegate.setDefaultNightMode(value)
    }

    enum class Mode {
        DAY, NIGHT
    }
}