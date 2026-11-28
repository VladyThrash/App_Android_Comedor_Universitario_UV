package com.example.comedor_universitario_uv.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.comedor_universitario_uv.R
import com.example.comedor_universitario_uv.databinding.FragmentPerfilBinding

/** Perfil del usuario y cierre de sesión. */
class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentPerfilBinding.bind(view)

        binding.btnCerrarSesion.setOnClickListener {
            // TODO: cerrar la sesión en la base de datos
            findNavController().navigate(
                R.id.loginFragment,
                null,
                navOptions { popUpTo(R.id.nav_graph) { inclusive = true } }
            )
        }
    }
}
