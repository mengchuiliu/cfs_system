package com.xxjr.cfs_system.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

//启动页logo显示
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if ((intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish()
            return
        }
        startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
        finish()
    }
}
