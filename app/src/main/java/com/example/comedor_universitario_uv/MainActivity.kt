package com.example.comedor_universitario_uv

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

/**
 * Actividad principal que actúa como contenedor base tras la autenticación en el Comedor UV.
 *
 * Centraliza la barra superior institucional, el menú lateral adaptado dinámicamente al rol
 * del usuario activo (comensal, becado, empleado o administrador), los indicadores globales
 * de carrito y notificaciones, y el intercambio de pantallas mediante [Fragment].
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_SESION_COMEDOR = "sesion_comedor"
        private const val KEY_NOMBRE = "nombre"
        private const val KEY_APELLIDOS = "apellidos"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROL = "rol"

        private const val ROL_ADMIN = "ADMIN"
        private const val ROL_EMPLEADO = "EMPLEADO"
        private const val ROL_BECADO = "BECADO"
        private const val ROL_ESTUDIANTE_DEFAULT = "ESTUDIANTE"
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var containerNotificaciones: FrameLayout
    private lateinit var tvNotifBadge: TextView
    private lateinit var containerCarrito: FrameLayout
    private lateinit var tvCarritoBadge: TextView
    private lateinit var tvToolbarAvatar: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vincularVistas()
        configurarPerfilYPermisosPorRol()
        configurarEventosBarraSuperior()
        configurarNavegacionLateral()
        configurarRetrocesoDrawer()
    }

    /**
     * Reemplaza la vista activa dentro del contenedor principal de la aplicación.
     *
     * @param fragment Instancia del [Fragment] que representa la sección a mostrar.
     */
    fun cargarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    /**
     * Actualiza el contador visual de platillos agregados al carrito en la barra superior.
     *
     * @param cantidadTotal Número total de artículos acumulados en el carrito actual.
     */
    fun actualizarBadgeCarrito(cantidadTotal: Int) {
        if (cantidadTotal > 0) {
            tvCarritoBadge.visibility = View.VISIBLE
            tvCarritoBadge.text = cantidadTotal.toString()
        } else {
            tvCarritoBadge.visibility = View.GONE
        }
    }

    /**
     * Actualiza el indicador numérico de notificaciones sin leer en la barra superior.
     *
     * @param sinLeer Cantidad de notificaciones pendientes por revisar.
     */
    fun actualizarBadgeNotificaciones(sinLeer: Int) {
        if (sinLeer > 0) {
            tvNotifBadge.visibility = View.VISIBLE
            tvNotifBadge.text = sinLeer.toString()
        } else {
            tvNotifBadge.visibility = View.GONE
        }
    }

    private fun vincularVistas() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        containerNotificaciones = findViewById(R.id.containerNotificaciones)
        tvNotifBadge = findViewById(R.id.tvNotifBadge)
        containerCarrito = findViewById(R.id.containerCarrito)
        tvCarritoBadge = findViewById(R.id.tvCarritoBadge)
        tvToolbarAvatar = findViewById(R.id.tvToolbarAvatar)
    }

    private fun configurarPerfilYPermisosPorRol() {
        val prefs = getSharedPreferences(PREFS_SESION_COMEDOR, Context.MODE_PRIVATE)
        val nombre = prefs.getString(KEY_NOMBRE, "Usuario") ?: "Usuario"
        val apellidos = prefs.getString(KEY_APELLIDOS, "UV") ?: "UV"
        val email = prefs.getString(KEY_EMAIL, "usuario@uv.mx") ?: "usuario@uv.mx"
        val rol = (prefs.getString(KEY_ROL, ROL_ESTUDIANTE_DEFAULT) ?: ROL_ESTUDIANTE_DEFAULT).uppercase()

        val iniciales = generarIniciales(nombre, apellidos)
        val nombreCompleto = "$nombre $apellidos".trim()

        tvToolbarAvatar.text = iniciales

        val headerView = navigationView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.tvNavIniciales).text = iniciales
        headerView.findViewById<TextView>(R.id.tvNavNombreUsuario).text = nombreCompleto
        headerView.findViewById<TextView>(R.id.tvNavEmailUsuario).text = email
        headerView.findViewById<TextView>(R.id.tvNavRolUsuario).text = rol

        // El personal operativo (ADMIN y EMPLEADO) gestiona cocina/caja y no realiza pedidos como comensal
        val esRolComensal = rol != ROL_ADMIN && rol != ROL_EMPLEADO
        containerNotificaciones.visibility = if (esRolComensal) View.VISIBLE else View.GONE
        containerCarrito.visibility = if (esRolComensal) View.VISIBLE else View.GONE

        val menuLateral = navigationView.menu
        menuLateral.setGroupVisible(R.id.group_comensal, esRolComensal)
        // Los becados comparten el catálogo general pero requieren acceso exclusivo a su calendario de subsidio
        menuLateral.setGroupVisible(R.id.group_becado, rol == ROL_BECADO)
        menuLateral.setGroupVisible(R.id.group_empleado, rol == ROL_EMPLEADO)
        menuLateral.setGroupVisible(R.id.group_admin, rol == ROL_ADMIN)
    }

    private fun configurarEventosBarraSuperior() {
        findViewById<ImageButton>(R.id.btnAbrirSidebar).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        findViewById<ImageButton>(R.id.btnNotificaciones).setOnClickListener {
            Toast.makeText(this, "Abrir notificaciones", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageButton>(R.id.btnCarrito).setOnClickListener {
            Toast.makeText(this, "Abrir carrito", Toast.LENGTH_SHORT).show()
        }

        tvToolbarAvatar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun configurarNavegacionLateral() {
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_menu_dia -> notificarSeccionPendiente("Menú del Día")
                R.id.nav_menu_carta -> notificarSeccionPendiente("A la Carta")
                R.id.nav_historial -> notificarSeccionPendiente("Historial de Pedidos")
                R.id.nav_calendario_beca -> notificarSeccionPendiente("Mi Calendario de Beca")
                R.id.nav_emp_pedidos -> notificarSeccionPendiente("Cocina: Pedidos Activos")
                R.id.nav_emp_disponibilidad -> notificarSeccionPendiente("Cocina: Disponibilidad")
                R.id.nav_emp_pos -> notificarSeccionPendiente("Caja: Punto de Venta")
                R.id.nav_admin_dashboard -> notificarSeccionPendiente("Dashboard Admin")
                R.id.nav_admin_menu_semanal -> notificarSeccionPendiente("Programar Menú Semanal")
                R.id.nav_admin_platillos -> notificarSeccionPendiente("Platillos y Menú")
                R.id.nav_admin_recetas -> notificarSeccionPendiente("Recetas")
                R.id.nav_admin_usuarios -> notificarSeccionPendiente("Usuarios")
                R.id.nav_admin_becados -> notificarSeccionPendiente("Becados Autorizados")
                R.id.nav_admin_inventario -> notificarSeccionPendiente("Inventario")
                R.id.nav_admin_calificaciones -> notificarSeccionPendiente("Calificaciones")
                R.id.nav_perfil -> notificarSeccionPendiente("Mi Perfil")
                R.id.nav_logout -> cerrarSesion()
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun configurarRetrocesoDrawer() {
        // Intercepta el botón atrás del sistema para cerrar el menú lateral antes de salir de la app
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun cerrarSesion() {
        // Limpia los datos locales de sesión para evitar acceso sin credenciales válidas
        getSharedPreferences(PREFS_SESION_COMEDOR, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
    }

    private fun generarIniciales(nombre: String, apellidos: String): String {
        val inicialNombre = nombre.trim().firstOrNull()?.uppercaseChar() ?: 'U'
        val inicialApellido = apellidos.trim().firstOrNull()?.uppercaseChar() ?: 'V'
        return "$inicialNombre$inicialApellido"
    }

    private fun notificarSeccionPendiente(nombreSeccion: String) {
        Toast.makeText(this, "Sección: $nombreSeccion", Toast.LENGTH_SHORT).show()
    }
}