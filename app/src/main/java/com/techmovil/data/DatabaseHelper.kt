package com.techmovil.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Clase para manejar la base de datos SQLite
class DatabaseHelper (context: Context): SQLiteOpenHelper(context, "db.techmovil", null, 1) {
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
                telefono TEXT NOT NULL,
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

    // Método para insertar un nuevo usuario en la base de datos
    fun insertarUsuario(
        id: String,
        nombres: String,
        apellidos: String,
        cedula: String,
        email: String,
        telefono: String,
        contrasena: String
    ): Boolean {
        val db = writableDatabase
        val values = android.content.ContentValues().apply {
            put("id", id)
            put("nombres", nombres)
            put("apellidos", apellidos)
            put("cedula", cedula)
            put("email", email)
            put("telefono", telefono)
            put("contrasena", contrasena)
            put("fecha_registro", System.currentTimeMillis())
        }
        val resultado = db.insert("usuarios", null, values)
        return resultado != -1L
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