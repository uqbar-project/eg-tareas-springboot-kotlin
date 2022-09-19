package org.uqbar.tareas.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.uqbar.tareas.repository.UsuariosRepository

@Service
class UsuariosService {

   @Autowired
   lateinit var usuariosRepository: UsuariosRepository

   fun allInstances() = usuariosRepository.allInstances()

}