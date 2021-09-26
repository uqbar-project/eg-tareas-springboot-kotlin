package org.uqbar.tareas.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.uqbar.commons.model.Entity
import org.uqbar.tareas.errors.BusinessException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Tarea : Entity() {
    companion object {
        const val TAREA_COMPLETA = 100
        const val DATE_PATTERN = "dd/MM/yyyy"
        val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
    }

    var descripcion: String = ""
    var iteracion: String = ""
    var porcentajeCumplimiento: Int = 0

    @JsonIgnore
    var asignatario: Usuario? = null

    @JsonIgnore
    var fecha = LocalDate.now()

    fun validar(): Unit {
        if (descripcion.isEmpty()) {
            throw BusinessException("Debe ingresar descripcion")
        }
        if (fecha === null) {
            throw BusinessException("Debe ingresar fecha")
        }
    }

    fun estaCumplida() = porcentajeCumplimiento == TAREA_COMPLETA

    fun estaPendiente() = !this.estaCumplida()

    override fun toString() = this.descripcion

    @JsonProperty("asignadoA")
    fun getAsignadoA(): String = asignatario?.nombre.orEmpty()

    @JsonProperty("asignadoA")
    fun setAsignatario(nombreAsignatario: String) {
        asignatario = Usuario(nombreAsignatario)
    }

    @JsonProperty("fecha")
    fun getFechaAsString() = formatter.format(this.fecha)

    @JsonProperty("fecha")
    fun asignarFecha(fecha: String) {
        this.fecha = LocalDate.parse(fecha, formatter)
    }

    fun asignarA(usuario: Usuario) {
        this.asignatario = usuario
        usuario.asignarTarea(this)
    }

    fun actualizar(otraTarea: Tarea) {
        descripcion = otraTarea.descripcion
        iteracion = otraTarea.iteracion
        porcentajeCumplimiento = otraTarea.porcentajeCumplimiento
        asignatario = otraTarea.asignatario
        fecha = otraTarea.fecha
    }
}