package com.techmovil.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.techmovil.R
import android.widget.EditText
import android.widget.Button
import android.widget.TextView

class CodigoVerificacionEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_codigo_verificacion_email)

        val editTextCodigo = findViewById<EditText>(R.id.editText_entrada_codigo_verificacion_email)
        val botonVerificar = findViewById<Button>(R.id.button_verificar_verificacion_email)

        editTextCodigo.filters = arrayOf(
            InputFilter.LengthFilter(6),
            InputFilter { s, _, _, _, _, _ ->
                if (s.isEmpty() || s.toString().matches(Regex("^[0-9]*$"))) null else ""
            }
        )

        botonVerificar.setOnClickListener {
            if (editTextCodigo.text.toString().matches(Regex("^[0-9]{6}$"))) {
                startActivity(Intent(this, EmailVerificadoActivity::class.java))
                finish()
            }
        }
    }
}
