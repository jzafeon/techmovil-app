package com.techmovil.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.techmovil.R
import com.techmovil.data.DatabaseHelper
import android.widget.Toast

class RegistroActivity : AppCompatActivity() {

    private lateinit var buttonContinuar: Button
    private lateinit var databaseHelper: DatabaseHelper // Declarar base de datos
    private val dominiosPermitidos = listOf(
        "gmail.com",
        "outlook.com", "outlook.es",
        "hotmail.com", "hotmail.es",
        "live.com",
        "msn.com",
        "ucompensar.edu.co"
    )
    private val limiteCaracteres = 35

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        databaseHelper = DatabaseHelper(this) // Inicializar base de datos


        buttonContinuar = findViewById(R.id.button_continuar_registro)
        actualizarBotonContinuar(false)
        mostrarAsteriscosIniciales()
        configurarCampos()

        findViewById<TextView>(R.id.textView_iniciar_sesion_registro).setOnClickListener {
            startActivity(Intent(this, BienvenidaActivity::class.java))
            finish()
        }

        buttonContinuar.setOnClickListener {
            if (validarTodosLosCampos()) {
                // Registrar usuario en base de datos
                if (registrarUsuarioEnBD()) {
                    val intent = Intent(this, CodigoVerificacionEmailActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun mostrarAsteriscosIniciales() {
        listOf(
            R.id.icono_validacion_nombres, R.id.icono_validacion_apellidos,
            R.id.icono_validacion_cedula, R.id.icono_validacion_email, R.id.icono_validacion_telefono
        ).forEach {
            findViewById<ImageView>(it).apply {
                setImageResource(R.drawable.icono_asterisk)
                visibility = View.VISIBLE
            }
        }
    }

    private fun configurarCampos() {
        configurarCampoTexto(R.id.editText_nombres_registro, R.id.icono_validacion_nombres, R.id.textView_mensaje_nombres, "nombres")
        configurarCampoTexto(R.id.editText_apellidos_registro, R.id.icono_validacion_apellidos, R.id.textView_mensaje_apellidos, "apellidos")
        configurarCampoCedula()
        configurarCampoEmail()
        configurarCampoTelefono()
    }

    private fun configurarCampoTexto(editTextId: Int, iconoId: Int, mensajeId: Int, tipo: String) {
        val editText = findViewById<EditText>(editTextId)
        val icono = findViewById<ImageView>(iconoId)
        val mensaje = findViewById<TextView>(mensajeId)
        var isEditing = false

        editText.filters = arrayOf(
            android.text.InputFilter.LengthFilter(limiteCaracteres),
            android.text.InputFilter { source, _, _, _, _, _ ->
                if (source.isNotEmpty() && !source.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*$"))) "" else null
            }
        )

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isNotEmpty() == true && s[0] == ' ') {
                    editText.post { editText.setText(s.toString().trimStart()) }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true

                val texto = s.toString()
                if (texto.isNotEmpty() && texto[0] == ' ') {
                    s?.replace(0, texto.length, texto.trimStart())
                    isEditing = false
                    return
                }

                if (texto.length >= limiteCaracteres) {
                    mensaje.text = "Límite de $limiteCaracteres caracteres alcanzado"
                    mensaje.visibility = View.VISIBLE
                }

                if (texto.isNotEmpty()) {
                    val palabras = texto.trim().split(" ").filter { it.isNotEmpty() }

                    if (palabras.size >= 3 && texto.endsWith(" ")) {
                        s?.delete(texto.length - 1, texto.length)
                        isEditing = false
                        return
                    }

                    val textoCapitalizado = capitalizarTexto(texto)
                    if (texto != textoCapitalizado) s?.replace(0, texto.length, textoCapitalizado)

                    val palabrasFinal = textoCapitalizado.trim().split(" ").filter { it.isNotEmpty() }
                    val tieneLetrasRepetidas = palabrasFinal.any { it.length >= 3 && it.matches(Regex("^(.)\\1+$")) }
                    val esValido = palabrasFinal.size in 1..3 && !tieneLetrasRepetidas && texto.length <= limiteCaracteres

                    when {
                        texto.length > limiteCaracteres -> actualizarEstado(icono, editText, false)
                        tieneLetrasRepetidas -> {
                            actualizarEstado(icono, editText, false)
                            mensaje.text = "$tipo inválidos - No se permiten letras repetidas"
                            mensaje.visibility = View.VISIBLE
                        }
                        palabrasFinal.size > 3 -> {
                            actualizarEstado(icono, editText, false)
                            mensaje.text = "Máximo 3 $tipo permitidos"
                            mensaje.visibility = View.VISIBLE
                        }
                        palabrasFinal.size < 1 -> {
                            actualizarEstado(icono, editText, false)
                            mensaje.text = "Mínimo 1 $tipo requerido"
                            mensaje.visibility = View.VISIBLE
                        }
                        else -> {
                            actualizarEstado(icono, editText, true)
                            if (texto.length < limiteCaracteres) mensaje.visibility = View.GONE
                        }
                    }
                } else {
                    actualizarEstado(icono, editText, false)
                    mensaje.visibility = View.GONE
                }
                isEditing = false
            }
        })
    }

    private fun configurarCampoCedula() {
        val editText = findViewById<EditText>(R.id.editText_cedula_registro)
        val icono = findViewById<ImageView>(R.id.icono_validacion_cedula)
        val mensaje = findViewById<TextView>(R.id.textView_mensaje_cedula)

        // LÍMITE DE 10 DÍGITOS PARA CEDULA
        editText.filters = arrayOf(android.text.InputFilter.LengthFilter(10))

        editText.addTextChangedListener(createTextWatcher(icono, editText, mensaje) { texto ->
            when {
                texto.isEmpty() -> null
                texto.length !in 7..10 -> "La cédula debe tener entre 7 y 10 dígitos"
                texto.matches(Regex("^(.)\\1*$")) -> "Cédula inválida - No se permiten números repetidos"
                else -> null
            }
        })
    }

    private fun configurarCampoEmail() {
        val editText = findViewById<EditText>(R.id.editText_email_registro)
        val icono = findViewById<ImageView>(R.id.icono_validacion_email)
        val mensaje = findViewById<TextView>(R.id.textView_mensaje_email)

        editText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                (v as EditText).getOffsetForPosition(event.x, event.y).takeIf { it != -1 }?.let {
                    v.post { v.setSelection(it) }
                }
            }
            false
        }

        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) v.post { (v as EditText).setSelection(v.text.length) }
        }


        var isEmailComplete = false

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val textoLimpio = s.toString().replace(" ", "")

                // Verificar si el email está completo
                isEmailComplete = validarEmailEstricto(textoLimpio)

                val error = when {
                    textoLimpio.isEmpty() -> null
                    textoLimpio.count { it == '@' } != 1 -> "El email debe contener un solo símbolo @"
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(textoLimpio).matches() -> "Formato de email inválido"
                    !validarEmailEstricto(textoLimpio) -> "Dominio no permitido - Use: gmail.com, outlook.com, hotmail.com, ucompensar.edu.co"
                    else -> null
                }

                if (error != null) {
                    actualizarEstado(icono, editText, false)
                    mensaje.text = error
                    mensaje.visibility = View.VISIBLE
                } else {
                    actualizarEstado(icono, editText, textoLimpio.isNotEmpty())
                    mensaje.visibility = View.GONE
                }
                actualizarBotonContinuar(validarTodosLosCampos())
            }
        })

        // LÍMITE DE 35 CARACTERES PARA EMAIL
        editText.filters = arrayOf(
            android.text.InputFilter { source, start, end, dest, dstart, dend ->
                // Eliminar espacios en blanco al final del email
                if (isEmailComplete && dstart >= dest.length) {
                    return@InputFilter ""
                }
                // Eliminar espacios en blanco
                if (source.contains(" ")) {
                    return@InputFilter ""
                }

                null
            }
        )
    }

    private fun configurarCampoTelefono() {
        val editText = findViewById<EditText>(R.id.editText_telefono_registro)
        val icono = findViewById<ImageView>(R.id.icono_validacion_telefono)
        val mensaje = findViewById<TextView>(R.id.textView_mensaje_telefono)

        // LÍMITE DE 13 DÍGITOS PARA TELÉFONO
        editText.filters = arrayOf(android.text.InputFilter.LengthFilter(13))

        editText.setText("+57")
        editText.setSelection(editText.text.length)

        editText.addTextChangedListener(createTextWatcher(icono, editText, mensaje) { texto ->
            val textoLimpio = texto.filter { it == '+' || it.isDigit() }
            when {
                textoLimpio.isEmpty() -> null
                !textoLimpio.startsWith("+57") -> {
                    editText.setText("+57")
                    editText.setSelection(editText.text.length)
                    null
                }
                textoLimpio.length > 3 && textoLimpio[3] != '3' -> "El número debe empezar con 3 después de +57"
                textoLimpio.length < 13 -> if (textoLimpio.length > 3 && textoLimpio[3] != '3') "Formato completo: +57 3XXXXXXXXX" else null
                !validarTelefono(textoLimpio) -> if (textoLimpio.length > 3 && textoLimpio[3] != '3') "Número inválido - Debe empezar con +573" else null
                else -> null
            }
        })
    }

    private fun createTextWatcher(icono: ImageView, editText: EditText, mensaje: TextView, validacion: (String) -> String?): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString()
                val error = validacion(texto)

                if (error != null) {
                    actualizarEstado(icono, editText, false)
                    mensaje.text = error
                    mensaje.visibility = View.VISIBLE
                } else {
                    actualizarEstado(icono, editText, texto.isNotEmpty())
                    mensaje.visibility = View.GONE
                }
            }
        }
    }

    private fun actualizarEstado(icono: ImageView, editText: EditText, esValido: Boolean) {
        val texto = editText.text.toString()
        icono.setImageResource(if (esValido) R.drawable.icono_check else R.drawable.icono_asterisk)
        icono.visibility = if (texto.isEmpty() || esValido) View.VISIBLE else View.INVISIBLE
        actualizarBotonContinuar(validarTodosLosCampos())
    }

    private fun capitalizarTexto(texto: String) = texto.split(" ").joinToString(" ") { palabra ->
        if (palabra.isNotEmpty()) palabra.substring(0, 1).uppercase() + palabra.substring(1).lowercase() else palabra
    }

    private fun validarEmailEstricto(email: String) = email.count { it == '@' } == 1 &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            email.substringAfterLast('@').lowercase() in dominiosPermitidos

    private fun validarTelefono(telefono: String) = telefono.startsWith("+57") &&
            telefono.substring(3).let { it.length == 10 && it.matches(Regex("^3[0-9]{9}\$")) }

    private fun validarTodosLosCampos() = validarCampo(R.id.editText_nombres_registro) { texto ->
        texto.trim().let { it.isNotEmpty() && it[0] != ' ' && it.length <= limiteCaracteres &&
                it.split(" ").filter { p -> p.isNotEmpty() }.let { palabras ->
                    palabras.size in 1..3 && palabras.none { p -> p.length >= 3 && p.matches(Regex("^(.)\\1+$")) }
                }
        }
    } && validarCampo(R.id.editText_apellidos_registro) { texto ->
        texto.trim().let { it.isNotEmpty() && it[0] != ' ' && it.length <= limiteCaracteres &&
                it.split(" ").filter { p -> p.isNotEmpty() }.let { palabras ->
                    palabras.size in 1..3 && palabras.none { p -> p.length >= 3 && p.matches(Regex("^(.)\\1+$")) }
                }
        }
    } && validarCampo(R.id.editText_cedula_registro) { it.isNotEmpty() && it.length in 7..10 &&
            it.matches(Regex("^[0-9]+\$")) && !it.matches(Regex("^(.)\\1*$"))
    } && validarCampo(R.id.editText_email_registro) { it.isNotEmpty() && validarEmailEstricto(it) } &&
            validarCampo(R.id.editText_telefono_registro) { it.isNotEmpty() && validarTelefono(it) }

    private fun validarCampo(editTextId: Int, validacion: (String) -> Boolean) =
        validacion(findViewById<EditText>(editTextId).text.toString())

    private fun actualizarBotonContinuar(habilitar: Boolean) {
        buttonContinuar.isEnabled = habilitar
        buttonContinuar.alpha = if (habilitar) 1.0f else 0.5f
    }

    // Registrar usuario en base de datos
    private fun registrarUsuarioEnBD(): Boolean {
        try {
            val nombres = findViewById<EditText>(R.id.editText_nombres_registro).text.toString().trim()
            val apellidos = findViewById<EditText>(R.id.editText_apellidos_registro).text.toString().trim()
            val cedula = findViewById<EditText>(R.id.editText_cedula_registro).text.toString().trim()
            val email = findViewById<EditText>(R.id.editText_email_registro).text.toString().trim()
            val telefono = findViewById<EditText>(R.id.editText_telefono_registro).text.toString().trim()

            if (databaseHelper.existeEmail(email)) {
                Toast.makeText(this, "El email ya está registrado", Toast.LENGTH_SHORT).show()
                return false
            }

            val idUsuario = "user_${System.currentTimeMillis()}"
            val contrasenaTemporal = "temp123"

            val resultado = databaseHelper.insertarUsuario(
                id = idUsuario,
                nombres = nombres,
                apellidos = apellidos,
                cedula = cedula,
                email = email,
                telefono = telefono,
                contrasena = contrasenaTemporal
            )

            if (resultado) {
                Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
            }

            return resultado

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            return false
        }
    }

}