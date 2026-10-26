package cl.inacap.app1_firebase

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Perfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        NombreUsuario.mostrar(this)
        findViewById<Button>(R.id.btnvolver).setOnClickListener { finish() }
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseDatabase.getInstance().getReference("usuarios").child(uid)
            .get().addOnSuccessListener { usuario ->
                findViewById<TextView>(R.id.lblrut).text = "Rut: ${usuario.child("rut").value ?: "-"}"
                findViewById<TextView>(R.id.lblnombre).text = "Nombre: ${usuario.child("nombre").value ?: "-"}"
                findViewById<TextView>(R.id.lblapellido).text = "Apellido: ${usuario.child("apellido").value ?: "-"}"
                findViewById<TextView>(R.id.lblcorreo).text = "Correo: ${usuario.child("correo").value ?: "-"}"
                findViewById<TextView>(R.id.lblcargo).text = "Cargo: ${usuario.child("cargo").value ?: "-"}"
            }
    }
}
