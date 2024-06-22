package org.uqbar.tareas.controller

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.uqbar.tareas.service.UsuariosService

@RestController
@CrossOrigin("*")
class UsuariosController(
   val usuariosService: UsuariosService
) {

    @GetMapping("/usuarios")
    fun usuarios() = usuariosService.allInstances()

}