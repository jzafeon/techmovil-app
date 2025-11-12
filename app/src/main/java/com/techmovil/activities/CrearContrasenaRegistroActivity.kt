package com.techmovil.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.techmovil.R
import com.techmovil.data.DatabaseHelper
// Crear contraseña
class CrearContrasenaRegistroActivity : AppCompatActivity() {

    private lateinit var editTextCrearContrasena: EditText
    private lateinit var editTextConfirmarContrasena: EditText
    private lateinit var iconoOjoCrearContrasena: ImageView
    private lateinit var iconoOjoConfirmarContrasena: ImageView
    private lateinit var iconoValidacionCrearContrasena: ImageView
    private lateinit var iconoValidacionConfirmarContrasena: ImageView
    private lateinit var buttonFinalizarRegistro: Button
    private lateinit var databaseHelper: DatabaseHelper

    private val limiteContrasena = 15
    private var isPasswordVisibleCrear = false
    private var isPasswordVisibleConfirmar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_contrasena_registro)

        databaseHelper = DatabaseHelper(this)

        initViews()
        setupValidations()
        // Botón finalizar registro
        buttonFinalizarRegistro.setOnClickListener {
            if (validarFormularioCompleto()) {
                val contrasena = editTextCrearContrasena.text.toString()
                databaseHelper.crearContrasena(contrasena)
                startActivity(Intent(this, RegistroCompletadoActivity::class.java))
                finish()
            }
        }
    }

    private fun initViews() {
        editTextCrearContrasena = findViewById(R.id.editText_entrada_crear_contrasena_registro)
        editTextConfirmarContrasena = findViewById(R.id.editText_confirmar_contrasena)
        buttonFinalizarRegistro = findViewById(R.id.button_finalizar_registro_crear_contrasena_registro)

        iconoOjoCrearContrasena = findViewById(R.id.icono_ojo_contrasena_registro)
        iconoOjoConfirmarContrasena = findViewById(R.id.icono_ojo_confirmar_contrasena)
        iconoValidacionCrearContrasena = findViewById(R.id.icono_validacion_contrasena_registro)
        iconoValidacionConfirmarContrasena = findViewById(R.id.icono_validacion_confirmar_contrasena)

        configurarFiltros()
        configurarOjosContrasena()
    }

    private fun configurarFiltros() {
        val filtroSinEspacios = android.text.InputFilter { source, _, _, _, _, _ ->
            if (source.isNotEmpty() && source.contains(" ")) "" else null
        }

        val filtros = arrayOf(android.text.InputFilter.LengthFilter(limiteContrasena), filtroSinEspacios)
        editTextCrearContrasena.filters = filtros
        editTextConfirmarContrasena.filters = filtros
    }
    // Configurar ojos de contraseña
    private fun configurarOjosContrasena() {
        iconoOjoCrearContrasena.setOnClickListener { togglePasswordVisibility(editTextCrearContrasena, iconoOjoCrearContrasena, ::isPasswordVisibleCrear) }
        iconoOjoConfirmarContrasena.setOnClickListener { togglePasswordVisibility(editTextConfirmarContrasena, iconoOjoConfirmarContrasena, ::isPasswordVisibleConfirmar) }
        actualizarVisibilidadOjos()
    }
    // Ocultar/mostrar contraseña
    private fun togglePasswordVisibility(editText: EditText, icono: ImageView, visibilityFlag: () -> Boolean) {
        val newVisibility = !visibilityFlag()
        if (newVisibility) {
            editText.transformationMethod = null
            icono.setImageResource(R.drawable.ojo_abierto)
        } else {
            editText.transformationMethod = PasswordTransformationMethod()
            icono.setImageResource(R.drawable.ojo_cerrado)
        }
        editText.setSelection(editText.text.length)

        when (visibilityFlag) {
            ::isPasswordVisibleCrear -> isPasswordVisibleCrear = newVisibility
            ::isPasswordVisibleConfirmar -> isPasswordVisibleConfirmar = newVisibility
        }
    }

    private fun actualizarVisibilidadOjos() {
        iconoOjoCrearContrasena.visibility = if (editTextCrearContrasena.text.isNotEmpty()) ImageView.VISIBLE else ImageView.GONE
        iconoOjoConfirmarContrasena.visibility = if (editTextConfirmarContrasena.text.isNotEmpty()) ImageView.VISIBLE else ImageView.GONE
    }

    private fun setupValidations() {
        setupTextWatcher(editTextCrearContrasena) { text ->
            validarCrearContrasena(text)
            actualizarVisibilidadOjos()
            actualizarBotonFinalizar()
        }

        setupTextWatcher(editTextConfirmarContrasena) { text ->
            validarConfirmarContrasena(text)
            actualizarVisibilidadOjos()
            actualizarBotonFinalizar()
        }
        actualizarBotonFinalizar()
    }

    private fun setupTextWatcher(editText: EditText, onTextChanged: (String) -> Unit) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val textoLimpio = s.toString().replace(" ", "")
                if (s.toString() != textoLimpio) {
                    editText.removeTextChangedListener(this)
                    editText.setText(textoLimpio)
                    editText.setSelection(textoLimpio.length)
                    editText.addTextChangedListener(this)
                }
                onTextChanged(textoLimpio)
            }
        })
    }
    // Validar contraseña
    private fun validarCrearContrasena(contrasena: String) {
        val esValido = contrasena.isNotEmpty() &&
                contrasena.length in 8..limiteContrasena &&
                contrasena.matches(Regex(".*[A-Z].*")) &&
                contrasena.matches(Regex(".*[a-z].*")) &&
                contrasena.matches(Regex(".*[0-9].*")) &&
                contrasena.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*+-"))

        actualizarEstadoContrasena(iconoValidacionCrearContrasena, editTextCrearContrasena.text.toString(), esValido)
    }
    // Validar confirmación de contraseña
    private fun validarConfirmarContrasena(confirmacion: String) {
        val esValido = confirmacion.isNotEmpty() && confirmacion == editTextCrearContrasena.text.toString()
        actualizarEstadoContrasena(iconoValidacionConfirmarContrasena, editTextConfirmarContrasena.text.toString(), esValido)
    }
    // Actualizar estado de la contraseña
    private fun actualizarEstadoContrasena(icono: ImageView, texto: String, esValido: Boolean) {
        icono.setImageResource(if (esValido) R.drawable.icono_check else R.drawable.icono_asterisk)
        icono.visibility = if (texto.isEmpty() || esValido) ImageView.VISIBLE else ImageView.INVISIBLE
    }
    // Validar formulario completo
    private fun validarFormularioCompleto(): Boolean {
        return validarContrasenaSilenciosa(editTextCrearContrasena.text.toString()) &&
                editTextConfirmarContrasena.text.toString() == editTextCrearContrasena.text.toString() &&
                editTextConfirmarContrasena.text.toString().isNotEmpty()
    }
    // Validar contraseña
    private fun validarContrasenaSilenciosa(contrasena: String): Boolean {
        return contrasena.isNotEmpty() &&
                contrasena.length >= 8 &&
                contrasena.length <= limiteContrasena &&
                contrasena.matches(Regex(".*[A-Z].*")) &&
                contrasena.matches(Regex(".*[a-z].*")) &&
                contrasena.matches(Regex(".*[0-9].*")) &&
                contrasena.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*+-"))
    }
    // Botón finalizar registro
    private fun actualizarBotonFinalizar() {
        val formularioValido = validarFormularioCompleto()
        buttonFinalizarRegistro.isEnabled = formularioValido
        buttonFinalizarRegistro.alpha = if (formularioValido) 1.0f else 0.5f
    }
}