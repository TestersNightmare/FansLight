package com.dex7er.flashlight.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 直接跳转到主界面
        val intent = Intent(this, MainActivity::class.java)

        // 如果是从快捷方式或其他外部 Intent 启动的，保留原本的 extras
        if (getIntent().extras != null) {
            intent.putExtras(getIntent().extras!!)
        }

        startActivity(intent)
        finish()
    }
}
