package com.bignerdranch.android.navonactivity

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity:AppCompatActivity() {
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}