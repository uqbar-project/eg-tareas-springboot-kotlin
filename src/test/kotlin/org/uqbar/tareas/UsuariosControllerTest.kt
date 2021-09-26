package org.uqbar.tareas

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.uqbar.tareas.domain.Usuario
import org.uqbar.tareas.repository.UsuariosRepository

@AutoConfigureJsonTesters
@WebMvcTest
@ComponentScan(basePackages=arrayOf("org.uqbar.tareas"))
@DisplayName("Dado un controller de usuarios")
class UsuariosControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var usuariosRepository: UsuariosRepository

    @BeforeEach
    fun init() {
        usuariosRepository.apply {
            clear()
            create(Usuario("Fernando Dodino"))
            create(Usuario("Rodrigo Grisolia"))
            create(Usuario("Dario Grinberg"))
            create(Usuario("Juan Contardo"))
            create(Usuario("Nahuel Palumbo"))
        }
    }

    @Test
    fun `se pueden obtener todes les usuaries`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/usuarios"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(5))
    }

}
