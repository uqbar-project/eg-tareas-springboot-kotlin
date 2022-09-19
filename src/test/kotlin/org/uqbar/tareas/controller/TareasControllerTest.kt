package org.uqbar.tareas.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.uqbar.tareas.domain.Tarea
import org.uqbar.tareas.domain.Usuario
import org.uqbar.tareas.repository.TareasRepository
import org.uqbar.tareas.repository.UsuariosRepository
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dado un controller de tareas")
class TareasControllerTest(@Autowired val mockMvc: MockMvc) {

    @Autowired
    lateinit var tareasRepository: TareasRepository

    @Autowired
    lateinit var usuariosRepository: UsuariosRepository

    lateinit var usuario: Usuario
    lateinit var tarea: Tarea

    @BeforeEach
    fun init() {
        usuariosRepository.clear()
        tareasRepository.clear()
        usuario = Usuario("Juan Contardo")
        usuariosRepository.create(usuario)
        tarea = tareasRepository.create(buildTarea())
        tareasRepository.create(buildTarea().also {
            it.descripcion = "Implementar single sign on desde la extranet"
            it.fecha = LocalDate.of(2018, 9, 9)
            it.iteracion = "Iteracion 1"
            it.porcentajeCumplimiento = 76
        })
    }

    // region tareas
    @Test
    fun `se pueden obtener todas las tareas`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/tareas"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(2))
    }
    // end region

    // region tareas/search
    @Test
    fun `se pueden pedir las tareas que contengan cierta descripcion`() {
        val tareaBusqueda = buildTarea()
        println(ObjectMapper().writeValueAsString(tareaBusqueda))
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/tareas/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapper().writeValueAsString(tareaBusqueda))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$.[0].descripcion").value(tareaBusqueda.descripcion))
    }

    @Test
    fun `se pueden pedir tareas que contengan cierta descripcion y que no se encuentre ninguna`() {
        val tareaBusqueda = Tarea().apply {
            descripcion = "Esta tarea no existe"
        }
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get("/tareas/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapper().writeValueAsString(tareaBusqueda))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(0))
    }
    // end region

    // region tareas/{id}
    @Test
    fun `se puede obtener una tarea por su id`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/tareas/" + tarea.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(tarea.id))
            .andExpect(jsonPath("$.descripcion").value(tarea.descripcion))
    }

    @Test
    fun `si se pide una tarea con un id que no existe se produce un error`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/tareas/20000"))
            .andExpect(status().isNotFound)
    }
    // end region

    // region actualizar tarea
    @Test
    fun `actualizar una tarea a un valor valido actualiza correctamente`() {
        val tareaValida = buildTarea().apply {
            id = tarea.id
            porcentajeCumplimiento = 70
        }
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put("/tareas/" + tarea.id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapper().writeValueAsString(tareaValida))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.porcentajeCumplimiento").value("70"))
    }

    @Test
    fun `si se intenta actualizar una tarea con datos incorrectos, el sistema rechaza la operacion`() {
        val tareaInvalida = buildTarea().apply {
            id = tarea.id
            descripcion = ""
        }

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put("/tareas/" + tarea.id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapper().writeValueAsString(tareaInvalida))
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "Debe ingresar descripcion")
    }

    @Test
    fun `se puede desasignar omitiendo el asignatario, esto actualiza la tarea correctamente`() {
        val tareaSinAsignatario = """
            {
                "id": ${tarea.id},
                "descripcion":  "Resolver testeo unitario de tarea",
                "fecha": "21/05/2021",
                "iteracion": "Iteracion 1",
                "porcentajeCumplimiento": 40
            }
        """.trimIndent()

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put("/tareas/${tarea.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tareaSinAsignatario)
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.asignadoA").value(null))
            .andExpect(jsonPath("$.porcentajeCumplimiento").value("40"))
    }

    @Test
    fun `si se intenta asignar a un usuario inexistente, el sistema rechaza la operacion`() {
        val tareaSinAsignatario = """
            {
                "id": ${tarea.id},
                "descripcion":  "Resolver testeo unitario de tarea",
                "asignadoA": "Mengueche",
                "fecha": "21/05/2021",
                "iteracion": "Iteracion 1",
                "porcentajeCumplimiento": 40
            }
        """.trimIndent()

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put("/tareas/${tarea.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tareaSinAsignatario)
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "No se encontró el usuario <Mengueche>")
    }

    @Test
    fun `si se intenta actualizar una tarea con id diferente en el request y en el body, el sistema rechaza la operacion`() {
        val tareaJson = """
            {
                "id": 1,
                "descripcion":  "Resolver testeo unitario de tarea",
                "fecha": "21/05/2021",
                "iteracion": "Iteracion 1",
                "porcentajeCumplimiento": 20
            }
        """.trimIndent()

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put("/tareas/" + 2)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tareaJson)
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "Id en URL distinto del id que viene en el body")
    }
    // end region

    // region crear tarea
    @Test
    fun `crear una tarea a un valor valido actualiza correctamente`() {
        val descripcionNuevaTarea = "Implementar un servicio REST para crear una tarea"
        val mapper = ObjectMapper()
        val tareaValida = buildTarea().apply {
            descripcion = descripcionNuevaTarea
        }
        val nuevaTareaResponse = mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/tareas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(tareaValida))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andReturn().response.contentAsString

        val nuevaTareaObject = mapper.readValue(nuevaTareaResponse, Tarea::class.java)
        val nuevaTarea = tareasRepository.searchById(nuevaTareaObject.id!!)
        assertEquals(nuevaTarea!!.descripcion, descripcionNuevaTarea)
    }

    @Test
    fun `si se intenta crear una tarea con datos incorrectos, el sistema rechaza la operacion`() {
        val tareaInvalida = buildTarea().apply {
            descripcion = ""
        }

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/tareas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapper().writeValueAsString(tareaInvalida))
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "Debe ingresar descripcion")
    }

    @Test
    fun `si se intenta crear una tarea pasando un id, el sistema rechaza la operacion`() {
        val tareaInvalida = buildTarea().apply {
            id = 100
            descripcion = ""
        }

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/tareas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapper().writeValueAsString(tareaInvalida))
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "No debe pasar el identificador de la tarea")
    }

    @Test
    fun `si se intenta crear una tarea pasando un asignatario invalido, el sistema rechaza la operacion`() {
        val tareaSinAsignatario = """
            {
                "descripcion":  "Resolver testeo unitario de tarea",
                "asignadoA": "Mengueche",
                "fecha": "21/05/2021",
                "iteracion": "Iteracion 1",
                "porcentajeCumplimiento": 40
            }
        """.trimIndent()

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/tareas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tareaSinAsignatario)
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "No se encontró el usuario <Mengueche>")
    }
    // end region

    fun buildTarea(): Tarea {
        return Tarea().apply {
            descripcion = "Desarrollar componente de envio de mails"
            asignarA(usuario)
            fecha = LocalDate.now()
            iteracion = "Iteración 1"
            porcentajeCumplimiento = 0
        }
    }

}
