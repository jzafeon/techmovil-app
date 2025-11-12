package com.techmovil.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.techmovil.R

class EmailVerificadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_email_verificado)

        // redireccionamiento a la pantalla de inicio de sesión
        val buttonContinuar = findViewById<android.widget.Button>(R.id.button_continuar_email_verificado)
        buttonContinuar.setOnClickListener {
            val intent = Intent(this, CodigoVerificacionCelularActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}