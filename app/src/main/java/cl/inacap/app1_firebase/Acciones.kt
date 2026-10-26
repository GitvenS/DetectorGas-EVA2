package cl.inacap.app1_firebase

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Acciones : AppCompatActivity() {
    private var referencia: DatabaseReference? = null
    private var observador: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acciones)
        NombreUsuario.mostrar(this)
        findViewById<Button>(R.id.btnvolver).setOnClickListener { finish() }
    }
    override fun onStart() {
        super.onStart()
        referencia = GasRepositorio.ruta()?.child("eventos")
        observador = object : ValueEventListener {
            override fun onDataChange(datos: DataSnapshot) {
                val contenedor = findViewById<LinearLayout>(R.id.contenedorEventos)
                contenedor.removeAllViews()
                val eventos = datos.children.sortedByDescending {
                    (it.child("fecha").value as? Number)?.toLong() ?: 0L
                }
                if (eventos.isEmpty()) {
                    agregarTexto(contenedor, "Todavía no hay eventos. Prueba Manipular Circuito.")
                    return
                }
                for (evento in eventos.take(40)) {
                    val fecha = (evento.child("fecha").value as? Number)?.toLong() ?: 0L
                    val fechaTexto = if (fecha > 0) SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale("es", "CL")).format(Date(fecha)) else "Sin fecha"
                    val accion = evento.child("accion").getValue(String::class.java) ?: "Acción"
                    val detalle = evento.child("detalle").getValue(String::class.java) ?: ""
                    val usuario = evento.child("usuario").getValue(String::class.java) ?: "Operador"
                    agregarTexto(contenedor, "$fechaTexto\nUsuario: $usuario\nAcción: $accion\n$detalle")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                val contenedor = findViewById<LinearLayout>(R.id.contenedorEventos)
                contenedor.removeAllViews()
                agregarTexto(contenedor, "Error de acceso: ${error.message}")
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
    private fun agregarTexto(contenedor: LinearLayout, contenido: String) {
        val texto = TextView(this)
        val margen = (12 * resources.displayMetrics.density).toInt()
        texto.text = contenido
        texto.textSize = 15f
        texto.setTextColor(android.graphics.Color.rgb(28, 28, 28))
        texto.setBackgroundResource(R.drawable.tarjeta)
        texto.setPadding(margen, margen, margen, margen)
        val parametros = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        parametros.bottomMargin = margen
        contenedor.addView(texto, parametros)
    }
}
