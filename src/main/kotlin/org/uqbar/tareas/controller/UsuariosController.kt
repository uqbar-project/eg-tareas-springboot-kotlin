package org.uqbar.tareas.controller

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.uqbar.tareas.domain.Usuario
import org.uqbar.tareas.service.UsuariosService

@RestController
@CrossOrigin("*")
class UsuariosController(
   val usuariosService: UsuariosService
) {

    @GetMapping("/usuarios")
    fun usuarios() = usuariosService.allInstances()

    @PostMapping("/usuarios")
    fun crear(@RequestBody usuario: Usuario) = usuariosService.crear(usuario)

    @DeleteMapping("/usuarios/{id}")
    fun eliminar(@PathVariable id: Int) = usuariosService.eliminar(id)
}