package org.uqbar.tareas.bootstrap

import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import org.uqbar.tareas.domain.Usuario
import org.uqbar.tareas.repository.TareasRepository
import org.uqbar.tareas.repository.UsuariosRepository
import java.time.LocalDate

@Service
class TareasBootstrap(
    val tareasRepository: TareasRepository,
    val usuariosRepository: UsuariosRepository
) : InitializingBean {

    private lateinit var juan: Usuario
    private lateinit var rodrigo: Usuario

    fun crearTareas() {
        tareasRepository.clearInit()
        val thisYear = LocalDate.now().year
        tareasRepository.apply {
            create("Algo2: migrar ejemplo de Decorator a Kotlin", juan, LocalDate.now(), "Iteración 1", 0)
            create(
                "Algo3: Preparar TP de React", null, LocalDate.of(thisYear, 9, 9), "Iteración 1",
                76
            )
            create(
                "PHM: Planificar cursada", rodrigo, LocalDate.of(thisYear, 6, 30),
                "Iteración 1", 22
            )
            create(
                "Armar una página de la wiki que explique colecciones en Kotlin", null, LocalDate.of(thisYear, 8, 10), "Iteración 2",
                90
            )
        }
    }

    fun crearUsuarios() {
        usuariosRepository.clearInit()
        juan = Usuario ("Juan Contardo")
        rodrigo = Usuario ("Rodrigo Grisolia")

        usuariosRepository.apply {
            create(Usuario("Fernando Dodino"))
            create(rodrigo)
            create(Usuario("Jorge Luis Lescano"))
            create(juan)
            create(Usuario("Nahuel Palumbo"))
        }
    }

    override fun afterPropertiesSet() {
        this.crearUsuarios()
        this.crearTareas()
    }

}