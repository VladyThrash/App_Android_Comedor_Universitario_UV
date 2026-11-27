package com.example.comedor_universitario_uv

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import mx.uv.comedor.databinding.ActivityMainBinding
import java.util.Locale

/**
 * Actividad anfitriona principal (arquitectura Single-Activity) de Comedor Universitario UV.
 *
 * Centraliza la navegación entre los fragmentos de todos los roles del sistema (Público,
 * Comensal, Becado, Empleado y Administrador), adapta dinámicamente los controles de sesión
 * en la barra superior y el menú lateral, y provee lectura de pantalla por voz (TTS).
 */
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var textToSpeech: TextToSpeech? = null
    private var ttsListo: Boolean = false
    private var velocidadVoz: Float = 1.0f
    private var rolActual: String = ROL_PUBLICO

    /**
     * Clasificación visual para las notificaciones emergentes globales de la aplicación.
     */
    enum class TipoMensaje {
        EXITO,
        ERROR,
        AVISO,
        INFO
    }

    companion object {
        const val ROL_PUBLICO = "PUBLICO"
        const val ROL_ESTUDIANTE = "ESTUDIANTE"
        const val ROL_DOCENTE = "DOCENTE"
        const val ROL_BECADO = "BECADO"
        const val ROL_EMPLEADO = "EMPLEADO"
        const val ROL_ADMIN = "ADMIN"

        private const val LIMITE_BADGE_NUMERICO = 9
        private const val MAX_CARACTERES_LECTURA_VOZ = 3800
        private const val ID_UTTERANCE_ACCESIBILIDAD = "UV_A11Y_UTTERANCE"
    }

    /**
     * Inicializa el enlace de vistas, el sintetizador de voz y la navegación por roles.
     *
     * @param savedInstanceState Estado previo de la actividad en caso de recreación.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textToSpeech = TextToSpeech(this, this)
        configurarNavegacionGlobal()
        configurarAccionesToolbarYFlotantes()
        configurarManejoBotonAtras()
        aplicarConfiguracionPorRol(rolActual)
    }

    /**
     * Libera los recursos nativos del motor [TextToSpeech] al destruir la actividad.
     */
    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }

    /**
     * Callback de inicialización del motor [TextToSpeech] configurado en español de México.
     *
     * @param status Código de estado devuelto por el servicio TTS del sistema.
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val resultado = textToSpeech?.setLanguage(Locale("es", "MX"))
            ttsListo = resultado != TextToSpeech.LANG_MISSING_DATA &&
                resultado != TextToSpeech.LANG_NOT_SUPPORTED
            textToSpeech?.setSpeechRate(velocidadVoz)
        } else {
            ttsListo = false
        }
    }

    /**
     * Delega la navegación ascendente de la barra superior al [NavController] y [AppBarConfiguration].
     *
     * @return `true` si la navegación fue manejada por el grafo; `false` en caso contrario.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Adapta los grupos visibles del menú lateral, los accesos de la barra superior y la
     * cabecera del perfil de acuerdo con el rol de sesión activo en el comedor.
     *
     * @param rol Identificador del rol del usuario (ej. [ROL_ESTUDIANTE], [ROL_BECADO], [ROL_ADMIN]).
     * @param nombreCompleto Nombre completo del usuario autenticado para la cabecera lateral.
     * @param email Correo institucional o identificador mostrado bajo el nombre.
     */
    fun aplicarConfiguracionPorRol(
        rol: String,
        nombreCompleto: String = "Invitado UV",
        email: String = "Comedor Universitario"
    ) {
        rolActual = rol
        val menu: Menu = binding.navViewMain.menu

        val esComensal = rol == ROL_ESTUDIANTE || rol == ROL_DOCENTE || rol == ROL_BECADO
        val esBecado = rol == ROL_BECADO
        val esEmpleado = rol == ROL_EMPLEADO
        val esAdmin = rol == ROL_ADMIN
        val estaAutenticado = rol != ROL_PUBLICO

        menu.setGroupVisible(R.id.grupo_nav_publico, !estaAutenticado)

        // El catálogo público permanece accesible sin sesión, pero historial y perfil exigen cuenta de comensal
        menu.setGroupVisible(R.id.grupo_nav_comensal, esComensal || !estaAutenticado)
        menu.findItem(R.id.nav_pedido_historial)?.isVisible = esComensal
        menu.findItem(R.id.nav_perfil)?.isVisible = esComensal
        menu.findItem(R.id.nav_becado_panel)?.isVisible = esBecado

        // Regla de negocio: el administrador también puede supervisar cocina, POS y disponibilidad
        menu.setGroupVisible(R.id.grupo_nav_empleado, esEmpleado || esAdmin)
        menu.setGroupVisible(R.id.grupo_nav_admin, esAdmin)
        menu.findItem(R.id.nav_cerrar_sesion)?.isVisible = estaAutenticado

        // El carrito solo aplica a pedidos en línea de comensales; empleados cobran mediante el POS
        binding.btnToolbarCarrito.visibility = if (esComensal) View.VISIBLE else View.GONE
        binding.btnToolbarNotificaciones.visibility = if (estaAutenticado) View.VISIBLE else View.GONE

        actualizarCabeceraDrawer(nombreCompleto, email, rol)
    }

    /**
     * Actualiza el indicador numérico del carrito en la barra superior.
     *
     * @param totalArticulos Cantidad acumulada de platillos en el carrito activo.
     */
    fun actualizarBadgeCarrito(totalArticulos: Int) {
        if (totalArticulos > 0) {
            binding.tvCarritoBadge.visibility = View.VISIBLE
            // Se trunca a "9+" para conservar la geometría circular del badge en pantallas estrechas
            binding.tvCarritoBadge.text = if (totalArticulos > LIMITE_BADGE_NUMERICO) {
                "9+"
            } else {
                totalArticulos.toString()
            }
        } else {
            binding.tvCarritoBadge.visibility = View.GONE
        }
    }

    /**
     * Actualiza el indicador numérico de notificaciones pendientes por leer en la barra superior.
     *
     * @param totalNoLeidas Cantidad de notificaciones sin leer recibidas del servidor.
     */
    fun actualizarBadgeNotificaciones(totalNoLeidas: Int) {
        if (totalNoLeidas > 0) {
            binding.tvNotifBadge.visibility = View.VISIBLE
            binding.tvNotifBadge.text = if (totalNoLeidas > LIMITE_BADGE_NUMERICO) {
                "9+"
            } else {
                totalNoLeidas.toString()
            }
        } else {
            binding.tvNotifBadge.visibility = View.GONE
        }
    }

    /**
     * Extrae los textos visibles de la pantalla activa y los reproduce mediante síntesis de voz
     * para asistencia de accesibilidad.
     *
     * @return `true` si la lectura comenzó correctamente; `false` si el motor TTS no está listo
     * o la vista actual no contiene texto legible.
     */
    fun leerPantallaActualEnVozAlta(): Boolean {
        if (!ttsListo) {
            mostrarMensaje(
                "El sintetizador de voz no está disponible en este dispositivo.",
                TipoMensaje.AVISO
            )
            return false
        }

        detenerLecturaVoz()
        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragmentMain)
        val vistaFragment = navHost?.childFragmentManager?.primaryNavigationFragment?.view

        if (vistaFragment == null) {
            return hablarTexto("No hay contenido disponible para leer en esta pantalla.")
        }

        // LinkedHashSet conserva el orden de lectura superior a inferior evitando etiquetas duplicadas
        val textosUnicos = LinkedHashSet<String>()
        recolectarTextosVisibles(vistaFragment, textosUnicos)

        // Android TTS limita las cadenas de entrada a 4000 caracteres por emisión
        val guionLectura = textosUnicos.joinToString(separator = ". ")
            .take(MAX_CARACTERES_LECTURA_VOZ)

        return if (guionLectura.isBlank()) {
            hablarTexto("No hay contenido disponible para leer en esta pantalla.")
        } else {
            hablarTexto(guionLectura)
        }
    }

    /**
     * Envía una cadena de texto específica a la cola inmediata del sintetizador de voz.
     *
     * @param texto Mensaje que será pronunciado en voz alta.
     * @return `true` si el mensaje fue aceptado por el motor TTS; `false` si está inactivo o vacío.
     */
    fun hablarTexto(texto: String): Boolean {
        if (!ttsListo || texto.isBlank()) return false
        textToSpeech?.setSpeechRate(velocidadVoz)
        val resultado = textToSpeech?.speak(
            texto,
            TextToSpeech.QUEUE_FLUSH,
            null,
            ID_UTTERANCE_ACCESIBILIDAD
        )
        return resultado == TextToSpeech.SUCCESS
    }

    /**
     * Interrumpe de inmediato cualquier lectura por voz en curso.
     */
    fun detenerLecturaVoz() {
        if (textToSpeech?.isSpeaking == true) {
            textToSpeech?.stop()
        }
    }

    /**
     * Muestra un aviso temporal institucional en la parte inferior de la pantalla.
     *
     * @param texto Contenido del mensaje a mostrar al usuario.
     * @param tipo Categoría semántica que determina el color de fondo del aviso.
     */
    fun mostrarMensaje(texto: String, tipo: TipoMensaje = TipoMensaje.INFO) {
        val colorFondo = when (tipo) {
            TipoMensaje.EXITO -> getColor(R.color.uv_verde_dark)
            TipoMensaje.ERROR -> getColor(R.color.uv_rojo_dark)
            TipoMensaje.AVISO -> getColor(R.color.uv_amarillo_texto)
            TipoMensaje.INFO -> getColor(R.color.uv_azul)
        }

        Snackbar.make(binding.root, texto, Snackbar.LENGTH_LONG)
            .setBackgroundTint(colorFondo)
            .setTextColor(getColor(R.color.uv_blanco))
            .show()
    }

    private fun configurarNavegacionGlobal() {
        setSupportActionBar(binding.toolbarMain)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragmentMain) as NavHostFragment
        navController = navHostFragment.navController

        // Destinos raíz donde se muestra el ícono de menú lateral en lugar de flecha de retroceso
        val destinosNivelSuperior = setOf(
            R.id.nav_menu,
            R.id.nav_pedido_historial,
            R.id.nav_becado_panel,
            R.id.nav_perfil,
            R.id.nav_empleado_dashboard,
            R.id.nav_empleado_disponibilidad,
            R.id.nav_empleado_pos,
            R.id.nav_admin_dashboard,
            R.id.nav_admin_menu,
            R.id.nav_admin_menu_semanal,
            R.id.nav_admin_recetas_lista,
            R.id.nav_admin_inventario,
            R.id.nav_admin_usuarios,
            R.id.nav_admin_becados,
            R.id.nav_admin_calificaciones
        )

        appBarConfiguration = AppBarConfiguration(destinosNivelSuperior, binding.drawerLayoutMain)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navViewMain.setupWithNavController(navController)

        binding.navViewMain.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_cerrar_sesion) {
                binding.drawerLayoutMain.closeDrawer(GravityCompat.START)
                confirmarCierreSesion()
                true
            } else {
                val manejado = NavigationUI.onNavDestinationSelected(menuItem, navController)
                if (manejado) {
                    binding.drawerLayoutMain.closeDrawer(GravityCompat.START)
                }
                manejado
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            actualizarVisibilidadInterfazPorDestino(destination.id)
        }
    }

    private fun actualizarVisibilidadInterfazPorDestino(destinationId: Int) {
        val esPantallaAutenticacion = destinationId == R.id.nav_login ||
            destinationId == R.id.nav_registro

        // En login y registro se bloquea el deslizamiento del Drawer para evitar navegación sin sesión
        if (esPantallaAutenticacion) {
            supportActionBar?.hide()
            binding.drawerLayoutMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            supportActionBar?.show()
            binding.drawerLayoutMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    private fun actualizarCabeceraDrawer(nombreCompleto: String, email: String, rol: String) {
        val headerView = binding.navViewMain.getHeaderView(0) ?: return
        val tvIniciales = headerView.findViewById<TextView>(R.id.tvNavHeaderIniciales)
        val tvNombre = headerView.findViewById<TextView>(R.id.tvNavHeaderNombre)
        val tvEmail = headerView.findViewById<TextView>(R.id.tvNavHeaderEmail)
        val tvRol = headerView.findViewById<TextView>(R.id.tvNavHeaderRolBadge)

        val partes = nombreCompleto.trim().split("\\s+".toRegex())
        val iniciales = when {
            partes.size >= 2 -> "${partes[0].first()}${partes[1].first()}".uppercase()
            partes.isNotEmpty() && partes[0].isNotEmpty() -> partes[0].take(2).uppercase()
            else -> "UV"
        }

        tvIniciales?.text = iniciales
        tvNombre?.text = nombreCompleto
        tvEmail?.text = email
        tvRol?.text = rol
    }

    private fun configurarManejoBotonAtras() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayoutMain.closeDrawer(GravityCompat.START)
                } else if (!navController.popBackStack()) {
                    finish()
                }
            }
        })
    }

    private fun configurarAccionesToolbarYFlotantes() {
        binding.btnToolbarNotificaciones.setOnClickListener {
            mostrarMensaje("Consultando notificaciones recientes...", TipoMensaje.INFO)
        }

        binding.btnToolbarCarrito.setOnClickListener {
            mostrarMensaje("Abriendo carrito de pedido...", TipoMensaje.INFO)
        }

        binding.fabAccesibilidadVoz.setOnClickListener {
            if (textToSpeech?.isSpeaking == true) {
                detenerLecturaVoz()
            } else {
                leerPantallaActualEnVozAlta()
            }
        }

        binding.fabAccesibilidadVoz.setOnLongClickListener {
            mostrarDialogoConfiguracionVoz()
            true
        }
    }

    private fun recolectarTextosVisibles(view: View, destino: MutableSet<String>) {
        if (view.visibility != View.VISIBLE) return

        if (view is TextView) {
            val texto = view.text?.toString()?.trim()?.replace("\\s+".toRegex(), " ") ?: ""
            if (texto.length > 1) {
                destino.add(texto)
            }
        } else if (view is ViewGroup) {
            for (child in view.children) {
                recolectarTextosVisibles(child, destino)
            }
        }
    }

    private fun mostrarDialogoConfiguracionVoz() {
        val opciones = arrayOf("Lenta (0.75x)", "Normal (1.0x)", "Rápida (1.25x)", "Muy rápida (1.5x)")
        val valores = floatArrayOf(0.75f, 1.0f, 1.25f, 1.5f)
        val seleccionActual = valores.indexOfFirst { it == velocidadVoz }.coerceAtLeast(1)

        MaterialAlertDialogBuilder(this)
            .setTitle("Asistencia por voz — Velocidad")
            .setSingleChoiceItems(opciones, seleccionActual) { dialog, which ->
                velocidadVoz = valores[which]
                textToSpeech?.setSpeechRate(velocidadVoz)
                mostrarMensaje("Velocidad de voz ajustada a ${opciones[which]}", TipoMensaje.INFO)
                dialog.dismiss()
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun confirmarCierreSesion() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas salir de tu cuenta en Comedor UV?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Salir") { _, _ ->
                aplicarConfiguracionPorRol(ROL_PUBLICO)
                navController.navigate(R.id.nav_login)
                mostrarMensaje("Sesión cerrada correctamente", TipoMensaje.INFO)
            }
            .show()
    }
}