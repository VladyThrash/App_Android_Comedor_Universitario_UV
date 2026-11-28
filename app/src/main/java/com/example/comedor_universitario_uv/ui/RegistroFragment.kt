package com.example.comedor_universitario_uv.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.comedor_universitario_uv.R
import com.example.comedor_universitario_uv.databinding.FragmentRegistroBinding

/** Pantalla de registro de usuario nuevo. */
class RegistroFragment : Fragment(R.layout.fragment_registro) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentRegistroBinding.bind(view)

        binding.btnCrearCuenta.setOnClickListener {
            // TODO: crear la cuenta con los datos del formulario
            findNavController().popBackStack()
        }

        binding.tvIrLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
