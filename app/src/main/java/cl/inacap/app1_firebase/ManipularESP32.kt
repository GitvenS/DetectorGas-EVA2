package cl.inacap.app1_firebase

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ManipularESP32 : AppCompatActivity() {
    private var monitoreo = false
    private var fuga = false
    private var cargado = false
    private var guardando = false
    private var referencia: DatabaseReference? = null
    private var observador: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manipular_esp32)
        NombreUsuario.mostrar(this)

        findViewById<Button>(R.id.btnvolver).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnactivar).setOnClickListener {
            registrar("Monitoreo activado", "Lectura simulada del sensor GPIO 33 habilitada", true, false)
        }
        findViewById<Button>(R.id.btndesactivar).setOnClickListener {
            if (fuga) {
                Toast.makeText(this, "Primero restablece el estado normal", Toast.LENGTH_SHORT).show()
            } else {
                registrar("Monitoreo desactivado", "Simulación pausada (sin lecturas)", false, false)
            }
        }
        findViewById<Button>(R.id.btnfuga).setOnClickListener {
            registrar("Fuga de gas detectada", "Sensor LOW (simulado), LED rojo y compuerta a 0°", true, true)
        }
        findViewById<Button>(R.id.btnnormal).setOnClickListener {
            registrar("Condición normal restaurada", "Sensor HIGH (simulado), LED verde y compuerta a 90°", true, false)
        }
        mostrar()
    }

    override fun onStart() {
        super.onStart()
        referencia = GasRepositorio.ruta()?.child("estado")
        observador = object : ValueEventListener {
            override fun onDataChange(dato: DataSnapshot) {
                monitoreo = dato.child("monitoreo").getValue(Boolean::class.java) ?: false
                fuga = dato.child("fuga").getValue(Boolean::class.java) ?: false
                cargado = true
                mostrar()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManipularESP32, "Sin acceso al estado: ${error.message}", Toast.LENGTH_LONG).show()
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

    private fun registrar(accion: String, detalle: String, activo: Boolean, hayFuga: Boolean) {
        if (!cargado || guardando) return
        guardando = true
        mostrar()
        GasRepositorio.guardarAccion(accion, detalle, activo, hayFuga) { bien, mensaje ->
            guardando = false
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            mostrar()
        }
    }

    private fun mostrar() {
        val estado = findViewById<TextView>(R.id.lblEstadoGas)
        val sensor = findViewById<TextView>(R.id.lblSensorGas)
        val servo = findViewById<TextView>(R.id.lblCompuertaGas)
        val verde = findViewById<TextView>(R.id.lblLedVerde)
        val rojo = findViewById<TextView>(R.id.lblLedRojo)
        when {
            !cargado -> {
                estado.text = "Cargando estado desde Firebase..."
                sensor.text = "Esperando datos"
                servo.text = "Compuerta: sin lectura"
                verde.text = "LED verde: sin lectura"
                rojo.text = "LED rojo: sin lectura"
                estado.setTextColor(Color.DKGRAY)
            }
            !monitoreo -> {
                estado.text = "MONITOREO DESACTIVADO"
                sensor.text = "Sensor GPIO 33: sin lectura (simulación pausada)"
                servo.text = "Compuerta virtual: estado no controlado"
                verde.text = "LED verde: apagado"
                rojo.text = "LED rojo: apagado"
                estado.setTextColor(Color.DKGRAY)
            }
            fuga -> {
                estado.text = "ALERTA: FUGA DETECTADA"
                sensor.text = "Sensor GPIO 33: LOW (fuga simulada)"
                servo.text = "Compuerta virtual: CERRADA (0°)"
                verde.text = "LED verde: apagado"
                rojo.text = "LED rojo: encendido"
                estado.setTextColor(Color.RED)
            }
            else -> {
                estado.text = "ESTADO NORMAL: SIN FUGA"
                sensor.text = "Sensor GPIO 33: HIGH (normal simulado)"
                servo.text = "Compuerta virtual: ABIERTA (90°)"
                verde.text = "LED verde: encendido"
                rojo.text = "LED rojo: apagado"
                estado.setTextColor(Color.rgb(18, 97, 53))
            }
        }
        findViewById<Button>(R.id.btnactivar).isEnabled = cargado && !guardando && !monitoreo
        findViewById<Button>(R.id.btndesactivar).isEnabled = cargado && !guardando && monitoreo && !fuga
        findViewById<Button>(R.id.btnfuga).isEnabled = cargado && !guardando && monitoreo && !fuga
        findViewById<Button>(R.id.btnnormal).isEnabled = cargado && !guardando && monitoreo && fuga
    }
}
