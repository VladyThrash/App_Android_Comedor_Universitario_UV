package com.example.comedor_universitario_uv.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.comedor_universitario_uv.R
import com.example.comedor_universitario_uv.databinding.FragmentLoginBinding

/** Pantalla de inicio de sesión. La autenticación real la conecta quien trabaja la base de datos. */
class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentLoginBinding.bind(view)

        binding.btnIniciarSesion.setOnClickListener {
            // TODO: validar credenciales (Firebase Authentication) antes de navegar
            findNavController().navigate(
                R.id.menuFragment,
                null,
                navOptions { popUpTo(R.id.loginFragment) { inclusive = true } }
            )
        }

        binding.tvRegistrate.setOnClickListener {
            findNavController().navigate(R.id.registroFragment)
        }
    }
}
