package com.example.comedor_universitario_uv.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.comedor_universitario_uv.R
import com.example.comedor_universitario_uv.databinding.FragmentDetallePlatilloBinding

/** Detalle de un platillo con notas y cantidad. */
class DetallePlatilloFragment : Fragment(R.layout.fragment_detalle_platillo) {

    private var cantidad = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentDetallePlatilloBinding.bind(view)

        binding.btnMenosDetalle.setOnClickListener {
            if (cantidad > 1) {
                cantidad--
                binding.tvCantidadDetalle.text = cantidad.toString()
            }
        }

        binding.btnMasDetalle.setOnClickListener {
            if (cantidad < 10) {
                cantidad++
                binding.tvCantidadDetalle.text = cantidad.toString()
            }
        }

        binding.btnAgregarCarrito.setOnClickListener {
            // TODO: guardar el platillo, la cantidad y las notas en el carrito
            findNavController().navigate(R.id.carritoFragment)
        }
    }
}
