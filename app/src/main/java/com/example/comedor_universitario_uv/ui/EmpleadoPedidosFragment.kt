package com.example.comedor_universitario_uv.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.comedor_universitario_uv.R
import com.example.comedor_universitario_uv.databinding.FragmentEmpleadoPedidosBinding

/** Lista de pedidos en cola para el personal del comedor (rol EMPLEADO o ADMIN). */
class EmpleadoPedidosFragment : Fragment(R.layout.fragment_empleado_pedidos) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentEmpleadoPedidosBinding.bind(view)

        binding.btnEscanearQr.setOnClickListener {
            // TODO: abrir el escáner de QR (ZXing) y navegar con el folio leído
            findNavController().navigate(R.id.validarPedidoFragment)
        }
    }
}
