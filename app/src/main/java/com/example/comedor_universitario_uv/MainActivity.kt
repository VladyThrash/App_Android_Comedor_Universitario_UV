package com.example.comedor_universitario_uv

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Actividad única de la app: aloja el NavHost y la barra de navegación inferior.
 * La barra solo se muestra en los destinos principales.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHost.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setupWithNavController(navController)

        // TEMPORAL: muestra la pestaña del personal para poder probarla.
        // Debe mostrarse solo si el rol del usuario es EMPLEADO o ADMIN.
        bottomNav.menu.findItem(R.id.empleadoFragment).isVisible = true

        val destinosConBarra = setOf(
            R.id.menuFragment,
            R.id.carritoFragment,
            R.id.pedidosFragment,
            R.id.perfilFragment,
            R.id.empleadoFragment
        )
        navController.addOnDestinationChangedListener { _, destino, _ ->
            bottomNav.visibility = if (destino.id in destinosConBarra) View.VISIBLE else View.GONE
        }
    }
}
