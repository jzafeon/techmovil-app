package com.techmovil.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.techmovil.R

class CelularVerificadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_celular_verificado)

        // redireccionamiento a la pantalla de crear contraseña
        val intent = Intent(this, CrearContrasenaRegistroActivity::class.java)
        startActivity(intent)
        finish()
    }
}