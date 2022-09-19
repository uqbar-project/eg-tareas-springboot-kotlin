package org.uqbar.tareas.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.uqbar.tareas.service.UsuariosService

@RestController
@CrossOrigin("*")
class UsuariosController {

    @Autowired
    lateinit var usuariosService: UsuariosService

    @GetMapping("/usuarios")
    @Operation(summary = "Devuelve todos los usuarios")
    fun usuarios() = usuariosService.allInstances()

}