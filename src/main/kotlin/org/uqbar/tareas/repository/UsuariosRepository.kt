package org.uqbar.tareas.repository

import org.apache.commons.collections15.Predicate
import org.springframework.stereotype.Component
import org.uqbar.commons.model.CollectionBasedRepo
import org.uqbar.tareas.domain.Usuario
import org.uqbar.tareas.errors.NotFoundException

@Component
class UsuariosRepository : CollectionBasedRepo<Usuario>() {

    override fun createExample() = Usuario("")

    override fun getEntityType() = Usuario::class.java

    override fun getCriterio(example: Usuario): Predicate<Usuario>  =
        Predicate<Usuario> {
            usuario -> usuario.nombre.uppercase().contains(example.nombre.uppercase())
        }

    fun getAsignatario(nombreAsignatario: String): Usuario {
        val usuariosByExample = searchByExample(Usuario(nombreAsignatario))
        if (usuariosByExample.isEmpty()) {
            throw NotFoundException("No se encontró el usuario <$nombreAsignatario>")
        }
        return usuariosByExample.first()
    }

    fun clear() {
        this.objects.clear()
    }
}