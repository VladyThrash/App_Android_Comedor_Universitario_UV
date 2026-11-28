package com.example.comedor_universitario_uv.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.comedor_universitario_uv.R
import com.example.comedor_universitario_uv.databinding.FragmentMenuBinding

/** Menú del día y carta. La lista de platillos (rvPlatillos) se llena con los datos de la base. */
class MenuFragment : Fragment(R.layout.fragment_menu) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMenuBinding.bind(view)

        // TEMPORAL: permite probar la navegación al detalle mientras no hay datos.
        // Se reemplaza por el clic de cada elemento del RecyclerView.
        binding.tvTituloMenu.setOnClickListener {
            findNavController().navigate(R.id.detallePlatilloFragment)
        }
    }
}
