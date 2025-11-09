package com.techmovil.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.techmovil.R

class CodigoVerificacionEmailActivity : AppCompatActivity() {

    private lateinit var editTextCodigo: EditText
    private lateinit var iconoValidacionCodigo: ImageView
    private lateinit var buttonVerificarEmail: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_codigo_verificacion_email)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupValidations()

        // Redireccionamiento a IniciarSesionActivity
        val textViewIniciarSesion = findViewById<TextView>(R.id.textView_enlace_iniciarsesion_verificacion_email)
        textViewIniciarSesion.setOnClickListener {
            val intent = Intent(this, IniciarSesionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initViews() {
        editTextCodigo = findViewById(R.id.editText_entrada_codigo_verificacion_email)
        iconoValidacionCodigo = findViewById(R.id.icono_validacion_codigo)
        buttonVerificarEmail = findViewById(R.id.button_verificar_verificacion_email)

        // Mostrar asterisco inicial
        mostrarAsteriscoInicial()

        // Configurar filtros para solo números y máximo 6 dígitos
        configurarFiltros()
    }

    private fun mostrarAsteriscoInicial() {
        iconoValidacionCodigo.apply {
            setImageResource(R.drawable.icono_asterisk)
            visibility = ImageView.VISIBLE
        }
    }

    private fun configurarFiltros() {
        // Filtro para solo números, máximo 6 dígitos y sin espacios
        editTextCodigo.filters = arrayOf(
            android.text.InputFilter.LengthFilter(6),
            android.text.InputFilter { source, start, end, dest, dstart, dend ->
                // Si está vacío, permitir (para poder borrar)
                if (source.isEmpty()) return@InputFilter null

                // Verificar que solo sean dígitos
                if (!source.toString().matches(Regex("^[0-9]*$"))) {
                    return@InputFilter ""
                }

                // Si es el primer carácter, verificar que sea dígito del 0-9
                if (dest.isEmpty() && dstart == 0) {
                    if (!source.toString().matches(Regex("^[0-9]$"))) {
                        return@InputFilter ""
                    }
                }

                // Permitir si pasa todas las validaciones
                null
            }
        )
    }

    private fun setupValidations() {
        // Validación en tiempo real para el código
        editTextCodigo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validarCodigo(s.toString())
            }
        })

        // Configurar el botón de verificar email - REDIRECCIONA A EmailVerificadoActivity
        buttonVerificarEmail.setOnClickListener {
            if (validarCodigoCompleto()) {
                val intent = Intent(this, EmailVerificadoActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun validarCodigo(codigo: String) {
        val esValido = codigo.length == 6 && codigo.matches(Regex("^[0-9]{6}$"))

        if (codigo.isEmpty()) {
            // Mostrar asterisco cuando está vacío
            iconoValidacionCodigo.setImageResource(R.drawable.icono_asterisk)
            iconoValidacionCodigo.visibility = ImageView.VISIBLE
        } else if (esValido) {
            // Mostrar check cuando son 6 dígitos válidos
            iconoValidacionCodigo.setImageResource(R.drawable.icono_check)
            iconoValidacionCodigo.visibility = ImageView.VISIBLE
        } else {
            // Mostrar asterisco cuando no es válido
            iconoValidacionCodigo.setImageResource(R.drawable.icono_asterisk)
            iconoValidacionCodigo.visibility = ImageView.VISIBLE
        }
    }

    private fun validarCodigoCompleto(): Boolean {
        val codigo = editTextCodigo.text.toString()
        return codigo.length == 6 && codigo.matches(Regex("^[0-9]{6}$"))
    }
}