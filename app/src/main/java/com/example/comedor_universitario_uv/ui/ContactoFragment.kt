package com.example.comedor_universitario_uv.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.comedor_universitario_uv.R
import com.example.comedor_universitario_uv.databinding.FragmentContactoBinding

/** Formulario que abre el cliente de correo y sistema de calificación de la app con RatingBar. */
class ContactoFragment : Fragment(R.layout.fragment_contacto) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentContactoBinding.bind(view)
        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        // Muestra la última calificación enviada desde este dispositivo
        binding.ratingApp.rating = prefs.getInt(CLAVE_CALIFICACION, 0).toFloat()

        binding.etMensajeContacto.doOnTextChanged { _, _, _, _ ->
            binding.tilMensajeContacto.error = null
        }

        binding.btnEnviarCorreo.setOnClickListener {
            val mensaje = binding.etMensajeContacto.text?.toString()?.trim().orEmpty()
            if (mensaje.isEmpty()) {
                binding.tilMensajeContacto.error = getString(R.string.contacto_error_mensaje)
                return@setOnClickListener
            }

            val asunto = when (binding.chipGroupTipoMensaje.checkedChipId) {
                R.id.chipProblema -> getString(R.string.contacto_asunto_problema)
                R.id.chipSugerencia -> getString(R.string.contacto_asunto_sugerencia)
                else -> getString(R.string.contacto_asunto_comentario)
            }
            enviarCorreo(asunto, mensaje)
        }

        binding.btnEnviarCalificacion.setOnClickListener {
            val estrellas = binding.ratingApp.rating.toInt()
            if (estrellas == 0) {
                Toast.makeText(requireContext(), R.string.contacto_calificacion_cero, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit { putInt(CLAVE_CALIFICACION, estrellas) }
            // TODO: guardar también la calificación en la base de datos del proyecto
            binding.tvRatingTexto.text = getString(R.string.contacto_calificacion_gracias, estrellas)
        }
    }

    /** Abre el cliente de correo con destinatario, asunto y mensaje ya escritos. */
    private fun enviarCorreo(asunto: String, mensaje: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(CORREO_SOPORTE))
            putExtra(Intent.EXTRA_SUBJECT, asunto)
            putExtra(Intent.EXTRA_TEXT, mensaje)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.contacto_sin_correo, Toast.LENGTH_LONG).show()
        }
    }

    private companion object {
        // TODO: cambiar por el correo real del equipo
        const val CORREO_SOPORTE = "soporte@ejemplo.com"
        const val PREFS = "comedoruv_prefs"
        const val CLAVE_CALIFICACION = "calificacion_app"
    }
}
