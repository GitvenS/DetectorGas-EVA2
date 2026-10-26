package cl.inacap.app1_firebase

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// Lectura sencilla de la misma ruta "usuarios" que usa Registro.kt.
object NombreUsuario {
    fun mostrar(pantalla: AppCompatActivity) {
        val texto = pantalla.findViewById<TextView>(R.id.lblusuario)
        texto.text = "Usuario"
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseDatabase.getInstance().getReference("usuarios").child(uid).child("nombre")
            .get().addOnSuccessListener { dato ->
                val nombre = dato.getValue(String::class.java)
                if (!nombre.isNullOrEmpty()) texto.text = nombre
            }
    }
}
