package org.uqbar.tareas.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.uqbar.tareas.errors.BusinessException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Tarea : Entity() {
    companion object {
        const val TAREA_COMPLETA = 100
        private const val DATE_PATTERN = "dd/MM/yyyy"
        private val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
    }

    var descripcion = ""
    var iteracion = ""
    var porcentajeCumplimiento = 0

    @JsonIgnore
    var asignatario: Usuario? = null

    @JsonIgnore
    var fecha: LocalDate = LocalDate.now()

    fun validar() {
        if (descripcion.isEmpty()) {
            throw BusinessException("Debe ingresar descripcion")
        }
    }

    fun estaCumplida() = porcentajeCumplimiento == TAREA_COMPLETA

    override fun toString() = this.descripcion

    @JsonProperty("asignadoA")
    fun getAsignadoA(): String? = asignatario?.nombre

    @JsonProperty("asignadoA")
    fun setAsignatario(nombreAsignatario: String?) {
        asignatario = if (nombreAsignatario != null) Usuario(nombreAsignatario) else null
    }

    @JsonProperty("fecha")
    fun getFechaAsString(): String = formatter.format(this.fecha)

    @JsonProperty("fecha")
    fun asignarFecha(fecha: String?) {
        if (fecha === null)
            throw BusinessException("Debe ingresar una fecha")
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
