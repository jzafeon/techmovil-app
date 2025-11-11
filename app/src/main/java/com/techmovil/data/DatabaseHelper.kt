package com.techmovil.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Clase para la base de datos
class DatabaseHelper(context: Context): SQLiteOpenHelper(context, "db.techmovil", null, 1) {
    companion object{
        private const val DATABASE_NAME = "db.techmovil"
        private const val DATABASE_VERSION = 1
    }

    // Método para crear la base de datos
    override fun onCreate(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE usuarios ( 
                id TEXT PRIMARY KEY,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL, 
                cedula TEXT UNIQUE NOT NULL,
                email TEXT UNIQUE NOT NULL,
                telefono TEXT UNIQUE NOT NULL,
                contrasena TEXT NOT NULL,
                fecha_registro INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(sql)
    }

    // Método para actualizar la base de datos
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    // Método para validar campos únicos
    fun validarCamposUnicos(cedula: String, email: String, telefono: String): Map<String, Boolean> {
        val db = readableDatabase
        val camposUnicos = mapOf(
            "cedula" to cedula,
            "email" to email,
            "telefono" to telefono
        )

        val resultados = mutableMapOf<String, Boolean>()

        camposUnicos.forEach { (campo, valor) ->
            val cursor = db.query(
                "usuarios",
                arrayOf(campo),
                "$campo = ?",
                arrayOf(valor),
                null, null, null
            )
            resultados[campo] = cursor.count > 0
            cursor.close()
        }

        return resultados
    }

    // Método para obtener mensajes de error de campos duplicados
    fun obtenerMensajesErrorDuplicados(cedula: String, email: String, telefono: String): List<String> {
        val errores = mutableListOf<String>()
        val camposDuplicados = validarCamposUnicos(cedula, email, telefono)

        if (camposDuplicados["cedula"] == true) {
            errores.add("La cédula ya está registrada")
        }
        if (camposDuplicados["email"] == true) {
            errores.add("El email ya está registrado")
        }
        if (camposDuplicados["telefono"] == true) {
            errores.add("El teléfono ya está registrado")
        }

        return errores
    }

        // Método para insertar un nuevo usuario
    fun insertarUsuario(
        id: String,
        nombres: String,
        apellidos: String,
        cedula: String,
        email: String,
        telefono: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", id)
            put("nombres", nombres)
            put("apellidos", apellidos)
            put("cedula", cedula)
            put("email", email)
            put("telefono", telefono)
            put("contrasena", "") // CONTRASEÑA VACÍA INICIAL
            put("fecha_registro", System.currentTimeMillis())
        }
        val resultado = db.insert("usuarios", null, values)
        return resultado != -1L
    }

    // Método para crear una nueva contrasena

    fun crearContrasena(contrasena: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("contrasena", contrasena)
        }// CONTRASEÑA VACÍA INICIAL
        val resultado = db.update("usuarios", values, "contrasena = ?", arrayOf(""))
        return resultado > 0
    }

    // Método para verificar si un email ya existe en la base de datos
    fun existeEmail(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            "usuarios",
            arrayOf("email"),
            "email = ?",
            arrayOf(email),
            null, null, null
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    // Método para verificar si una cédula ya existe en la base de datos
    fun existeCedula(cedula: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            "usuarios",
            arrayOf("cedula"),
            "cedula = ?",
            arrayOf(cedula),
            null, null, null
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    // Método para verificar si un teléfono ya existe en la base de datos
    fun existeTelefono(telefono: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            "usuarios",
            arrayOf("telefono"),
            "telefono = ?",
            arrayOf(telefono),
            null, null, null
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }
}