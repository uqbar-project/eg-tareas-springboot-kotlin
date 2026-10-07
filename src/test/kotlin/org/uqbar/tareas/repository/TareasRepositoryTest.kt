package org.uqbar.tareas.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.uqbar.tareas.domain.Tarea
import java.time.LocalDate

@DisplayName("Dado un repositorio de tareas")
class TareasRepositoryTest {

    lateinit var repository: TareasRepository

    @BeforeEach
    fun init() {
        repository = TareasRepository()
        repository.clearInit()
    }

    @Test
    fun `borrar por id funciona aunque sea otra instancia`() {
        val guardada = repository.create(Tarea().apply {
            descripcion = "Tarea a borrar"
            fecha = LocalDate.now()
            iteracion = "Iteración 1"
        })
        val otraInstanciaMismoId = Tarea().apply { id = guardada.id }

        repository.delete(otraInstanciaMismoId)

        assertNull(repository.searchById(guardada.id!!))
        assertEquals(0, repository.allInstances().size)
    }
}
