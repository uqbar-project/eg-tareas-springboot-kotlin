package org.uqbar.tareas.domain

import jakarta.validation.constraints.NotBlank

class Usuario(@field:NotBlank(message = "Debe ingresar nombre") var nombre: String = "") : Entity() {

    val tareasAsignadas: MutableList<Tarea> = mutableListOf()

    fun asignarTarea(tarea: Tarea) {
        if (!tareasAsignadas.contains(tarea)) {
            tareasAsignadas.add(tarea)
        }
    }

    fun quitarTarea(tarea: Tarea) {
        tareasAsignadas.remove(tarea)
    }
}