package com.example.comedor_universitario_uv.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.comedor_universitario_uv.R
import com.example.comedor_universitario_uv.databinding.FragmentCarritoBinding

/** Carrito con método de pago y confirmación del pedido. */
class CarritoFragment : Fragment(R.layout.fragment_carrito) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentCarritoBinding.bind(view)

        binding.btnConfirmarPedido.setOnClickListener {
            // TODO: crear el pedido en la base de datos y pasar su folio al comprobante
            findNavController().navigate(R.id.comprobanteFragment)
        }
    }
}
