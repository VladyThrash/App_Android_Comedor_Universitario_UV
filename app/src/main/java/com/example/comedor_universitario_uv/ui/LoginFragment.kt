package com.example.comedor_universitario_uv.ui

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.comedor_universitario_uv.R
import com.example.comedor_universitario_uv.databinding.FragmentLoginBinding

/** Inicio de sesión con validación de campos vacíos, formato de correo y credenciales incorrectas. */
class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentLoginBinding.bind(view)

        // Limpia los errores en cuanto el usuario vuelve a escribir
        binding.etCorreo.doOnTextChanged { _, _, _, _ ->
            binding.tilCorreo.error = null
            binding.tvError.visibility = View.GONE
        }
        binding.etContrasena.doOnTextChanged { _, _, _, _ ->
            binding.tilContrasena.error = null
            binding.tvError.visibility = View.GONE
        }

        binding.btnIniciarSesion.setOnClickListener {
            val correo = binding.etCorreo.text?.toString()?.trim().orEmpty()
            val contrasena = binding.etContrasena.text?.toString().orEmpty()

            if (!datosValidos(binding, correo, contrasena)) return@setOnClickListener

            if (credencialesCorrectas(correo, contrasena)) {
                findNavController().navigate(
                    R.id.menuFragment,
                    null,
                    navOptions { popUpTo(R.id.loginFragment) { inclusive = true } }
                )
            } else {
                binding.tvError.setText(R.string.login_error_credenciales)
                binding.tvError.visibility = View.VISIBLE
            }
        }

        binding.tvRegistrate.setOnClickListener {
            findNavController().navigate(R.id.registroFragment)
        }
    }

    /** Valida campos vacíos y formato del correo; muestra el error en el campo correspondiente. */
    private fun datosValidos(binding: FragmentLoginBinding, correo: String, contrasena: String): Boolean {
        var valido = true

        if (correo.isEmpty()) {
            binding.tilCorreo.error = getString(R.string.login_error_correo_vacio)
            valido = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.tilCorreo.error = getString(R.string.login_error_correo_invalido)
            valido = false
        }

        if (contrasena.isEmpty()) {
            binding.tilContrasena.error = getString(R.string.login_error_contrasena_vacia)
            valido = false
        }

        return valido
    }

    // TEMPORAL: validación local de demostración. Reemplazar por Firebase Authentication
    // (signInWithEmailAndPassword) y eliminar estas constantes.
    private fun credencialesCorrectas(correo: String, contrasena: String): Boolean =
        correo == DEMO_CORREO && contrasena == DEMO_CONTRASENA

    private companion object {
        const val DEMO_CORREO = "demo@uv.mx"
        const val DEMO_CONTRASENA = "123456"
    }
}
