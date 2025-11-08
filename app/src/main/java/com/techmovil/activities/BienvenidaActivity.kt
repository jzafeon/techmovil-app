package com.techmovil.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.techmovil.R

class BienvenidaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        // Botón registro
        val buttonCrearCuenta = findViewById<Button>(R.id.button_crear_cuenta_bienvenida)
        buttonCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Botón iniciar sesión
        val buttonIniciarSesion = findViewById<Button>(R.id.button_Iniciar_sesion_bienvenida)
        buttonIniciarSesion.setOnClickListener {
            val intent = Intent(this, IniciarSesionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}