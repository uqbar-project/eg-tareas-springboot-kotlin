package org.uqbar.tareas.repository

import org.springframework.stereotype.Component
import org.uqbar.tareas.domain.Usuario
import java.util.concurrent.atomic.AtomicInteger

@Component
class UsuariosRepository {
    private val usuarios = mutableListOf<Usuario>()
    private val lock = Any()

    companion object {
        private val ultimoId = AtomicInteger(ID_INICIAL_REPOSITORY)
    }

    fun allInstances() = synchronized(lock) {
        usuarios.sortedBy { it.nombre }
    }

    fun find(id: Int) = synchronized(lock) {
        usuarios.find { it.id == id }
    }

    fun create(usuario: Usuario): Usuario = synchronized(lock) {
        usuario.id = ultimoId.getAndIncrement()
        usuarios.add(usuario)
        usuario
    }

    fun delete(usuario: Usuario) = synchronized(lock) {
        usuarios.remove(usuario)
        usuario
    }

    fun getAsignatario(nombre: String) = synchronized(lock) {
        usuarios.find { it.nombre.equals(nombre, ignoreCase = true) }
    }

    fun clear() = synchronized(lock) {
        usuarios.clear()
    }

    fun clearInit() = synchronized(lock) {
        usuarios.clear()
        ultimoId.set(ID_INICIAL_REPOSITORY)
    }
}
