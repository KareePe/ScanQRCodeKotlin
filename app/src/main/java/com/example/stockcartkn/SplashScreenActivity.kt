package com.example.stockcartkn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stockcartkn.MainActivity

class SplashScreenActivity : AppCompatActivity() {

    private var preferenceHelper: PreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        preferenceHelper = PreferenceHelper(this)

        if(preferenceHelper!!.getIsLogin()){
            val intent = Intent(this@SplashScreenActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this.finish()
        }else{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}
