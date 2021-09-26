package org.uqbar.tareas.domain

import org.uqbar.commons.model.Entity

class Usuario(val nombre: String) : Entity() {

    val tareasAsignadas: MutableList<Tarea> = mutableListOf()

    fun asignarTarea(tarea: Tarea) {
        tareasAsignadas.add(tarea)
    }
}