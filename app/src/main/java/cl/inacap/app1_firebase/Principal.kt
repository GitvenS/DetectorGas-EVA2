package cl.inacap.app1_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class Principal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        val txtusu = findViewById<EditText>(R.id.txtusu)
        val txtcon = findViewById<EditText>(R.id.txtcon)
        val btnini = findViewById<Button>(R.id.btnini)
        val btncre = findViewById<Button>(R.id.btncre)

        btncre.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }
        btnini.setOnClickListener {
            val correo = txtusu.text.toString().trim()
            val contrasena = txtcon.text.toString()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa el correo y la contraseña", Toast.LENGTH_SHORT).show()
            } else if (FirebaseApp.getApps(this).isEmpty()) {
                Toast.makeText(this, "Primero configura Firebase (consulta LEEME)", Toast.LENGTH_LONG).show()
            } else {
                btnini.isEnabled = false
                FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener(this) { resultado ->
                        btnini.isEnabled = true
                        if (resultado.isSuccessful) {
                            val intento = Intent(this, Menu::class.java)
                            intento.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intento)
                            finish()
                        } else {
                            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}
