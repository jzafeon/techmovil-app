package com.techmovil.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.techmovil.MainActivity
import com.techmovil.R
import kotlinx.coroutines.Delay

class SplashActivity : AppCompatActivity() {
    private val splashDuration: Long = 3000L // duracion en milisegundos



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            //logica de redireccionamiento
            val intent = Intent(this, BienvenidaActivity::class.java)
            startActivity(intent)
            finish()
        }, splashDuration)
    }
}