package org.uqbar.tareas.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.uqbar.tareas.domain.Usuario

@DisplayName("Dado un repositorio de usuarios")
class UsuariosRepositoryTest {

    lateinit var repository: UsuariosRepository

    @BeforeEach
    fun init() {
        repository = UsuariosRepository()
        repository.clearInit()
    }

    @Test
    fun `borrar por id funciona aunque sea otra instancia`() {
        val guardado = repository.create(Usuario("Alguien"))
        val otraInstanciaMismoId = Usuario("Otro nombre").apply { id = guardado.id }

        repository.delete(otraInstanciaMismoId)

        assertNull(repository.find(guardado.id!!))
        assertEquals(0, repository.allInstances().size)
    }
}
