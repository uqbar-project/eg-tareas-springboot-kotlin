package org.uqbar.tareas.repository

import org.apache.commons.collections15.Predicate
import org.springframework.stereotype.Component
import org.uqbar.commons.model.CollectionBasedRepo
import org.uqbar.tareas.domain.Tarea
import org.uqbar.tareas.domain.Usuario
import java.time.LocalDate
import java.util.*

@Component
class TareasRepository : CollectionBasedRepo<Tarea>() {

    fun crearTarea(unaDescripcion: String, responsable: Usuario?, date: LocalDate?, unaIteracion: String, cumplimiento: Int): Unit {
        val tarea = Tarea().apply {
            if (responsable !== null) {
                asignarA(responsable)
            }
            descripcion = unaDescripcion
            fecha = date ?: LocalDate.now()
            iteracion = unaIteracion
            porcentajeCumplimiento = cumplimiento
        }
        this.create(tarea)
    }

    fun crearTarea(tarea: Tarea): Tarea {
        this.create(tarea)
        return tarea
    }

    override fun validateCreate(tarea: Tarea) {
        tarea.validar()
        super.validateCreate(tarea)
    }

    override fun createExample() = Tarea()

    override fun getEntityType() = Tarea::class.java

    override fun searchById(id: Int) = allInstances().find { it.id == id }
    override fun getCriterio(example: Tarea): Predicate<Tarea> =
        Predicate<Tarea> {
                tarea -> tarea.descripcion.uppercase().contains(example.descripcion.uppercase())
        }

    fun clear() {
        this.objects.clear()
    }

}