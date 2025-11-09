package com.techmovil.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.techmovil.R

class CodigoVerificacionCelularActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_codigo_verificacion_celular)

        val editTextCodigo = findViewById<EditText>(R.id.editText_entrada_codigo_verificacion_celular)
        val botonVerificar = findViewById<Button>(R.id.button_verificar_celular)

        editTextCodigo.filters = arrayOf(
            InputFilter.LengthFilter(6),
            InputFilter { source, _, _, _, _, _ ->
                if (source.isEmpty()) null else if (source.toString().matches(Regex("^[0-9]*$"))) null else ""
            }
        )

        botonVerificar.setOnClickListener {
            val codigo = editTextCodigo.text.toString()
            if (codigo.matches(Regex("^[0-9]{6}$"))) {
                startActivity(Intent(this, CelularVerificadoActivity::class.java))
                finish()
            }
        }
    }
}
