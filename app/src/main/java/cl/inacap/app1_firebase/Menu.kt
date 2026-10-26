package cl.inacap.app1_firebase

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, Principal::class.java))
            finish()
            return
        }
        NombreUsuario.mostrar(this)

        findViewById<Button>(R.id.btnmanipular).setOnClickListener {
            startActivity(Intent(this, ManipularESP32::class.java))
        }
        findViewById<Button>(R.id.btnacciones).setOnClickListener {
            startActivity(Intent(this, Acciones::class.java))
        }
        findViewById<Button>(R.id.btnestadisticas).setOnClickListener {
            startActivity(Intent(this, Estadisticas::class.java))
        }
        findViewById<Button>(R.id.btnperfil).setOnClickListener {
            startActivity(Intent(this, Perfil::class.java))
        }
        findViewById<Button>(R.id.btncerrar).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intento = Intent(this, Principal::class.java)
            intento.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intento)
            finish()
        }
    }
}
