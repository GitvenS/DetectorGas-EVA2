package cl.inacap.app1_firebase

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class Estadisticas : AppCompatActivity() {
    private var referencia: DatabaseReference? = null
    private var observador: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)
        NombreUsuario.mostrar(this)
        findViewById<Button>(R.id.btnvolver).setOnClickListener { finish() }
    }
    override fun onStart() {
        super.onStart()
        referencia = GasRepositorio.ruta()?.child("eventos")
        observador = object : ValueEventListener {
            override fun onDataChange(datos: DataSnapshot) {
                var activaciones = 0
                var desactivaciones = 0
                var fugas = 0
                var normales = 0
                for (registro in datos.children) {
                    when (registro.child("accion").getValue(String::class.java)) {
                        "Monitoreo activado" -> activaciones++
                        "Monitoreo desactivado" -> desactivaciones++
                        "Fuga de gas detectada" -> fugas++
                        "Condición normal restaurada" -> normales++
                    }
                }
                findViewById<TextView>(R.id.lblActivaciones).text = "Monitoreos activados: $activaciones"
                findViewById<TextView>(R.id.lblDesactivaciones).text = "Monitoreos desactivados: $desactivaciones"
                findViewById<TextView>(R.id.lblFugas).text = "Fugas simuladas detectadas: $fugas"
                findViewById<TextView>(R.id.lblCierres).text = "Cierres virtuales de compuerta: $fugas"
                findViewById<TextView>(R.id.lblNormales).text = "Retornos a estado normal: $normales"
                findViewById<TextView>(R.id.lblTotal).text = "Total de acciones registradas: ${datos.childrenCount}"
            }
            override fun onCancelled(error: DatabaseError) {
                findViewById<TextView>(R.id.lblTotal).text = "No se pudo consultar Firebase: ${error.message}"
            }
        }
        observador?.let { referencia?.addValueEventListener(it) }
    }
    override fun onStop() {
        observador?.let { referencia?.removeEventListener(it) }
        observador = null
        referencia = null
        super.onStop()
    }
}
