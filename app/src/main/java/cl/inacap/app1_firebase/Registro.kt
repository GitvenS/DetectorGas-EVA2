package cl.inacap.app1_firebase

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val rut = findViewById<EditText>(R.id.txtRutRegistro)
        val nombre = findViewById<EditText>(R.id.txtNombreRegistro)
        val apellido = findViewById<EditText>(R.id.txtApellidoRegistro)
        val correo = findViewById<EditText>(R.id.txtCorreoRegistro)
        val clave = findViewById<EditText>(R.id.txtClaveRegistro)
        val repetir = findViewById<EditText>(R.id.txtConfirmacionRegistro)
        val btnregistrar = findViewById<Button>(R.id.btnregistrar)

        findViewById<Button>(R.id.btnvolver).setOnClickListener { finish() }
        btnregistrar.setOnClickListener {
            val r = rut.text.toString().trim()
            val n = nombre.text.toString().trim()
            val a = apellido.text.toString().trim()
            val e = correo.text.toString().trim().lowercase()
            val c = clave.text.toString()
            val c2 = repetir.text.toString()

            if (r.isEmpty() || n.isEmpty() || a.isEmpty() || e.isEmpty() || c.isEmpty() || c2.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
            } else if (c != c2) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else if (c.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            } else if (FirebaseApp.getApps(this).isEmpty()) {
                Toast.makeText(this, "Primero configura Firebase (consulta LEEME)", Toast.LENGTH_LONG).show()
            } else {
                btnregistrar.isEnabled = false
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(e, c)
                    .addOnCompleteListener(this) { resultado ->
                        if (!resultado.isSuccessful) {
                            btnregistrar.isEnabled = true
                            Toast.makeText(this, "No se pudo crear la cuenta: ${resultado.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                        } else {
                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (uid == null) {
                                btnregistrar.isEnabled = true
                                Toast.makeText(this, "No se pudo recuperar el usuario", Toast.LENGTH_LONG).show()
                            } else {
                                // Solo datos de perfil: la clave no se almacena en Realtime Database.
                                val datos = mapOf(
                                    "rut" to r,
                                    "nombre" to n,
                                    "apellido" to a,
                                    "correo" to e,
                                    "cargo" to "Operador"
                                )
                                FirebaseDatabase.getInstance().getReference("usuarios").child(uid)
                                    .setValue(datos).addOnCompleteListener(this) { guardado ->
                                        btnregistrar.isEnabled = true
                                        if (guardado.isSuccessful) {
                                            val intento = Intent(this, Menu::class.java)
                                            intento.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intento)
                                            finish()
                                        } else {
                                            Toast.makeText(this, "Cuenta creada, pero no se guardó el perfil. Revisa las reglas de Firebase.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                            }
                        }
                    }
            }
        }
    }
}
