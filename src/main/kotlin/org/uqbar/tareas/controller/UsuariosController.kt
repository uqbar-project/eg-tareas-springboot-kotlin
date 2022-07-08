package org.uqbar.tareas.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.uqbar.tareas.repository.UsuariosRepository

@RestController
//@CrossOrigin TODO: Ver si es necesario
class UsuariosController {

    @Autowired
    lateinit var usuariosRepository: UsuariosRepository

    @GetMapping("/usuarios")
    @Operation(summary = "Devuelve todos los usuarios")
    fun usuarios() = usuariosRepository.allInstances()

}