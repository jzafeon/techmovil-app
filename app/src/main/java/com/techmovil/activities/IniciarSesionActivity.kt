package com.techmovil.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.techmovil.R

class IniciarSesionActivity : AppCompatActivity() {

    private lateinit var editTextCorreo: EditText
    private lateinit var editTextContrasena: EditText
    private lateinit var iconoValidacionCorreo: ImageView
    private lateinit var iconoValidacionContrasena: ImageView
    private lateinit var iconoOjoContrasena: ImageView
    private lateinit var textViewMensajeCorreo: TextView
    private lateinit var textViewMensajeContrasena: TextView
    private lateinit var buttonIngresar: Button

    // DOMINIOS CORREGIDOS: Gmail solo .com, Outlook y Hotmail con .com y .es
    private val dominiosPermitidos = listOf(
        "gmail.com",                    // Solo gmail.com
        "outlook.com", "outlook.es",    // Outlook con .com y .es
        "hotmail.com", "hotmail.es",    // Hotmail con .com y .es
        "live.com",                     // Live.com
        "msn.com",                      // MSN.com
        "ucompensar.edu.co"             // Dominio institucional
    )
    private val limiteCorreo = 40
    private val limiteContrasena = 20
    private var isPasswordVisible = false
    private var isEmailComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_iniciar_sesion)

        // Configurar para que el teclado no reemplace la pantalla
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupValidations()

        // Redireccionamiento a RegistroActivity
        val textViewRegistrarse = findViewById<TextView>(R.id.textView_registrarse_iniciar_sesion)
        textViewRegistrarse.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initViews() {
        editTextCorreo = findViewById(R.id.editText_correo_iniciar_sesion)
        editTextContrasena = findViewById(R.id.editText_contrasena_iniciar_sesion)
        buttonIngresar = findViewById(R.id.button_ingresar_iniciar_sesion)

        iconoValidacionCorreo = findViewById(R.id.icono_validacion_correo)
        iconoValidacionContrasena = findViewById(R.id.icono_validacion_contrasena)
        iconoOjoContrasena = findViewById(R.id.icono_ojo_contrasena)
        textViewMensajeCorreo = findViewById(R.id.textView_mensaje_correo)
        textViewMensajeContrasena = findViewById(R.id.textView_mensaje_contrasena)

        // Mostrar asteriscos iniciales
        mostrarAsteriscosIniciales()

        // Configurar límites de caracteres y filtros
        configurarFiltros()

        // Configurar el clic del icono del ojo
        configurarOjoContrasena()

        // Configurar comportamiento del teclado
        configurarTeclado()
    }

    private fun configurarTeclado() {
        // Cuando el usuario presiona "Enter" en la contraseña, intentar iniciar sesión
        editTextContrasena.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (validarFormularioCompleto()) {
                    buttonIngresar.performClick()
                    return@setOnEditorActionListener true
                }
            }
            false
        }
    }

    private fun configurarOjoContrasena() {
        iconoOjoContrasena.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            editTextContrasena.transformationMethod = null
            iconoOjoContrasena.setImageResource(R.drawable.ojo_abierto)
        } else {
            editTextContrasena.transformationMethod = PasswordTransformationMethod()
            iconoOjoContrasena.setImageResource(R.drawable.ojo_cerrado)
        }

        editTextContrasena.setSelection(editTextContrasena.text.length)
    }

    private fun actualizarVisibilidadOjo() {
        val tieneTexto = editTextContrasena.text.isNotEmpty()
        iconoOjoContrasena.visibility = if (tieneTexto) ImageView.VISIBLE else ImageView.GONE
    }

    private fun configurarFiltros() {
        // FILTRO SIMPLIFICADO PARA CORREO
        editTextCorreo.filters = arrayOf(
            android.text.InputFilter.LengthFilter(limiteCorreo),
            android.text.InputFilter { source, start, end, dest, dstart, dend ->
                // Si el email está completo y el usuario intenta agregar al final, bloquear
                if (isEmailComplete && dstart >= dest.length) {
                    return@InputFilter ""
                }
                // Eliminar espacios
                if (source.contains(" ")) {
                    return@InputFilter ""
                }
                // Permitir edición normal
                null
            }
        )

        // Filtro para contraseña sin cambios
        editTextContrasena.filters = arrayOf(
            android.text.InputFilter.LengthFilter(limiteContrasena),
            android.text.InputFilter { source, _, _, _, _, _ ->
                if (source.isNotEmpty() && source.contains(" ")) "" else null
            }
        )
    }

    private fun mostrarAsteriscosIniciales() {
        iconoValidacionCorreo.apply {
            setImageResource(R.drawable.icono_asterisk)
            visibility = ImageView.VISIBLE
        }
        iconoValidacionContrasena.apply {
            setImageResource(R.drawable.icono_asterisk)
            visibility = ImageView.VISIBLE
        }
        iconoOjoContrasena.apply {
            setImageResource(R.drawable.ojo_cerrado)
            visibility = ImageView.GONE
        }
    }

    private fun setupValidations() {
        // Validación en tiempo real para el correo
        editTextCorreo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val textoLimpio = s.toString().replace(" ", "")

                // Verificar si el correo está completo y válido
                isEmailComplete = validarEmailEstricto(textoLimpio)

                // Si el texto cambió por eliminar espacios, actualizar el EditText
                if (s.toString() != textoLimpio) {
                    editTextCorreo.removeTextChangedListener(this)
                    editTextCorreo.setText(textoLimpio)
                    editTextCorreo.setSelection(textoLimpio.length)
                    editTextCorreo.addTextChangedListener(this)
                }
                validarCorreo(textoLimpio)
                actualizarBotonIngresar()
            }
        })

        // Validación en tiempo real para la contraseña
        editTextContrasena.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val textoLimpio = s.toString().replace(" ", "")
                if (s.toString() != textoLimpio) {
                    editTextContrasena.removeTextChangedListener(this)
                    editTextContrasena.setText(textoLimpio)
                    editTextContrasena.setSelection(textoLimpio.length)
                    editTextContrasena.addTextChangedListener(this)
                }
                validarContrasena(textoLimpio)
                actualizarVisibilidadOjo()
                actualizarBotonIngresar()
            }
        })

        // Configurar el botón de ingresar
        buttonIngresar.setOnClickListener {
            if (validarFormularioCompleto()) {
                val correo = editTextCorreo.text.toString().replace(" ", "")
                val contrasena = editTextContrasena.text.toString()
                // Ejemplo: iniciarSesion(correo, contrasena)
            }
        }

        actualizarBotonIngresar()
    }

    private fun validarCorreo(texto: String) {
        val error = when {
            texto.isEmpty() -> null
            texto.length > limiteCorreo -> "Límite de $limiteCorreo caracteres alcanzado"
            texto.count { it == '@' } != 1 -> "El email debe contener un solo símbolo @"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(texto).matches() -> "Formato de email inválido"
            !validarEmailEstricto(texto) -> "Dominio no permitido - Use: gmail.com, outlook.com, hotmail.com, ucompensar.edu.co"
            else -> null
        }

        if (error != null) {
            actualizarEstadoCorreo(false)
            textViewMensajeCorreo.text = error
            textViewMensajeCorreo.visibility = TextView.VISIBLE
        } else {
            actualizarEstadoCorreo(texto.isNotEmpty())
            textViewMensajeCorreo.visibility = TextView.GONE
        }
    }

    private fun validarContrasena(contrasena: String) {
        val error = when {
            contrasena.isEmpty() -> null
            contrasena.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            contrasena.length > limiteContrasena -> "Límite de $limiteContrasena caracteres alcanzado"
            !contrasena.matches(Regex(".*[A-Z].*")) -> "Debe contener al menos una letra mayúscula"
            !contrasena.matches(Regex(".*[a-z].*")) -> "Debe contener al menos una letra minúscula"
            !contrasena.matches(Regex(".*[0-9].*")) -> "Debe contener al menos un número"
            !contrasena.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")) -> "Debe contener al menos un carácter especial (!@#\$%^&* etc.)"
            else -> null
        }

        if (error != null) {
            actualizarEstadoContrasena(false)
            textViewMensajeContrasena.text = error
            textViewMensajeContrasena.visibility = TextView.VISIBLE
        } else {
            actualizarEstadoContrasena(contrasena.isNotEmpty())
            textViewMensajeContrasena.visibility = TextView.GONE
        }
    }

    private fun actualizarEstadoCorreo(esValido: Boolean) {
        val texto = editTextCorreo.text.toString()
        iconoValidacionCorreo.setImageResource(if (esValido) R.drawable.icono_check else R.drawable.icono_asterisk)
        iconoValidacionCorreo.visibility = if (texto.isEmpty() || esValido) ImageView.VISIBLE else ImageView.INVISIBLE
    }

    private fun actualizarEstadoContrasena(esValido: Boolean) {
        val texto = editTextContrasena.text.toString()
        iconoValidacionContrasena.setImageResource(if (esValido) R.drawable.icono_check else R.drawable.icono_asterisk)
        iconoValidacionContrasena.visibility = if (texto.isEmpty() || esValido) ImageView.VISIBLE else ImageView.INVISIBLE
    }

    private fun validarEmailEstricto(email: String): Boolean {
        return email.count { it == '@' } == 1 &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                email.substringAfterLast('@').lowercase() in dominiosPermitidos
    }

    private fun validarFormularioCompleto(): Boolean {
        val correoValido = validarEmailEstricto(editTextCorreo.text.toString().replace(" ", ""))
        val contrasenaValida = validarContrasenaSilenciosa(editTextContrasena.text.toString())
        return correoValido && contrasenaValida
    }

    private fun validarContrasenaSilenciosa(contrasena: String): Boolean {
        return contrasena.isNotEmpty() &&
                contrasena.length >= 8 &&
                contrasena.length <= limiteContrasena &&
                contrasena.matches(Regex(".*[A-Z].*")) &&
                contrasena.matches(Regex(".*[a-z].*")) &&
                contrasena.matches(Regex(".*[0-9].*")) &&
                contrasena.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*"))
    }

    private fun actualizarBotonIngresar() {
        val formularioValido = validarFormularioCompleto()
        buttonIngresar.isEnabled = formularioValido
        buttonIngresar.alpha = if (formularioValido) 1.0f else 0.5f
    }
}