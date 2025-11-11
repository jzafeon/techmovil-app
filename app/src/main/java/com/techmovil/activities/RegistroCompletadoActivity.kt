package com.techmovil.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.techmovil.R

class RegistroCompletadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_completado)

        // redireccionamiento
        findViewById<android.widget.Button>(R.id.button_continuar_registro_completado).setOnClickListener {
            val intent = Intent(this, BienvenidaActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}