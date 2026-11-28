package com.example.comedor_universitario_uv.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.comedor_universitario_uv.R
import com.example.comedor_universitario_uv.databinding.FragmentComprobanteBinding

/** Comprobante del pedido con código QR. */
class ComprobanteFragment : Fragment(R.layout.fragment_comprobante) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentComprobanteBinding.bind(view)

        // TODO: generar el QR (ZXing) y mostrarlo en binding.ivQr

        binding.btnVolverMenu.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }
    }
}
