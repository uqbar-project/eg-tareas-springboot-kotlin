package org.uqbar.tareas.bootstrap

import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.uqbar.tareas.domain.Usuario
import org.uqbar.tareas.repository.TareasRepository
import org.uqbar.tareas.repository.UsuariosRepository
import java.time.LocalDate

@Service
class TareasBootstrap : InitializingBean {

    @Autowired
    lateinit var tareasRepository: TareasRepository

    @Autowired
    lateinit var usuariosRepository: UsuariosRepository

    lateinit var juan: Usuario
    lateinit var rodrigo: Usuario

    fun crearTareas() {
        tareasRepository.apply {
            create("Desarrollar componente de envio de mails", juan, LocalDate.now(), "Iteración 1", 0)
            create(
                "Implementar single sign on desde la extranet", null, LocalDate.of(2018, 9, 9), "Iteración 1",
                76
            )
            create(
                "Cancelar pedidos que esten pendientes desde hace 2 meses", rodrigo, LocalDate.of(2018, 6, 30),
                "Iteración 1", 22
            )
            create(
                "Mostrar info del pedido cuando esta finalizado", null, LocalDate.of(2018, 8, 10), "Iteración 2",
                90
            )
        }
    }

    fun crearUsuarios() {
        juan = Usuario ("Juan Contardo")
        rodrigo = Usuario ("Rodrigo Grisolia")

        usuariosRepository.apply {
            create(Usuario("Fernando Dodino"))
            create(rodrigo)
            create(Usuario("Dario Grinberg"))
            create(juan)
            create(Usuario("Nahuel Palumbo"))
        }
    }

    override fun afterPropertiesSet() {
        this.crearUsuarios()
        this.crearTareas()
    }

}