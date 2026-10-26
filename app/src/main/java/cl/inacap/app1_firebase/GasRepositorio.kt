package cl.inacap.app1_firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

// Se comparten estados SIMULADOS entre Android conectados a la misma cuenta.
// No controla el ESP32 real ni reemplaza un sistema industrial de seguridad.
object GasRepositorio {
    fun ruta(): DatabaseReference? {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        return FirebaseDatabase.getInstance().getReference("usuarios")
            .child(uid).child("simulacion")
    }

    fun guardarAccion(
        accion: String, detalle: String, monitoreo: Boolean, fuga: Boolean,
        alTerminar: (Boolean, String) -> Unit
    ) {
        val ruta = ruta()
        if (ruta == null) {
            alTerminar(false, "Inicia sesión primero")
            return
        }
        val clave = ruta.child("eventos").push().key
        if (clave == null) {
            alTerminar(false, "No fue posible crear el evento")
            return
        }
        // Una única actualización guarda el estado Y su evento.
        val valores = hashMapOf<String, Any>(
            "estado/monitoreo" to monitoreo,
            "estado/fuga" to fuga,
            "estado/actualizado" to ServerValue.TIMESTAMP,
            "eventos/$clave/accion" to accion,
            "eventos/$clave/detalle" to detalle,
            "eventos/$clave/usuario" to (FirebaseAuth.getInstance().currentUser?.email?.substringBefore("@") ?: "Operador"),
            "eventos/$clave/fecha" to ServerValue.TIMESTAMP
        )
        ruta.updateChildren(valores).addOnCompleteListener { resultado ->
            if (resultado.isSuccessful) {
                alTerminar(true, "Evento guardado en Firebase")
            } else {
                alTerminar(false, resultado.exception?.localizedMessage ?: "Error al guardar. Revisa las reglas de Firebase")
            }
        }
    }
}
