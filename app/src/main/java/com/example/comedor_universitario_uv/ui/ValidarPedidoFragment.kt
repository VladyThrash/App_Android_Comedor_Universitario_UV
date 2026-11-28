package com.example.comedor_universitario_uv.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.comedor_universitario_uv.R
import com.example.comedor_universitario_uv.databinding.FragmentValidarPedidoBinding

/** Resultado de escanear el QR de un pedido: datos del pedido y acción de entrega. */
class ValidarPedidoFragment : Fragment(R.layout.fragment_validar_pedido) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentValidarPedidoBinding.bind(view)

        // TODO: buscar el pedido por folio en la base de datos y llenar la pantalla.
        // Si no existe o ya está ENTREGADO o CANCELADO, mostrar cardResultado en rojo
        // con R.string.validar_no_valido y ocultar btnMarcarEntregado.

        binding.btnMarcarEntregado.setOnClickListener {
            // TODO: cambiar el estado del pedido a ENTREGADO
            findNavController().popBackStack()
        }

        binding.btnEscanearOtro.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnVolverLista.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
