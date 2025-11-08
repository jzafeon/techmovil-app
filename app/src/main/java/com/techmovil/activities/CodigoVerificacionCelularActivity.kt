package com.techmovil.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.techmovil.R

class CodigoVerificacionCelularActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_codigo_verificacion_celular)

        // Redireccionamiento a CelularVerificadoActivity
        val buttonVerificarCelular = findViewById<android.widget.Button>(R.id.button_verificar_celular)
        buttonVerificarCelular.setOnClickListener {
            val intent = Intent(this, CelularVerificadoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}