package com.example.comedor_universitario_uv.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.comedor_universitario_uv.R

/** Pantalla de presentación con el logotipo; después de unos segundos pasa al login. */
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val handler = Handler(Looper.getMainLooper())

    private val irAlLogin = Runnable {
        // TODO: si ya hay una sesión iniciada, ir directo a menuFragment
        findNavController().navigate(
            R.id.loginFragment,
            null,
            navOptions { popUpTo(R.id.splashFragment) { inclusive = true } }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handler.postDelayed(irAlLogin, DURACION_MS)
    }

    override fun onDestroyView() {
        handler.removeCallbacks(irAlLogin)
        super.onDestroyView()
    }

    private companion object {
        const val DURACION_MS = 2000L
    }
}
