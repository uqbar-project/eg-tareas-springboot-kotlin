package org.uqbar.tareas.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.uqbar.tareas.repository.TareasRepository
import org.uqbar.tareas.repository.UsuariosRepository

@SpringBootTest(properties = ["dev-endpoints.enabled=true"])
@AutoConfigureMockMvc
@DisplayName("Dado un controller de comandos dev")
class DevCommandsControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var tareasRepository: TareasRepository

    @Autowired
    lateinit var usuariosRepository: UsuariosRepository

    /**
     * Aunque se haya borrado un usuario, resetear solo las tareas
     * recupera los datos de ejemplo completos.
     */
    @Test
    fun `resetear tareas restaura los datos de ejemplo`() {
        val juan = usuariosRepository.getAsignatario("Juan Contardo")!!
        mockMvc
            .perform(MockMvcRequestBuilders.delete("/usuarios/${juan.id}"))
            .andExpect(status().isOk)

        mockMvc
            .perform(MockMvcRequestBuilders.post("/reset/tareas"))
            .andExpect(status().isOk)

        assertThat(tareasRepository.allInstances()).hasSize(4)
        assertThat(usuariosRepository.getAsignatario("Juan Contardo")).isNotNull()
    }

    @Test
    fun `resetear todo restaura usuarios y tareas`() {
        mockMvc
            .perform(MockMvcRequestBuilders.post("/reset/all"))
            .andExpect(status().isOk)

        assertThat(usuariosRepository.allInstances()).hasSize(5)
        assertThat(tareasRepository.allInstances()).hasSize(4)
    }
}
