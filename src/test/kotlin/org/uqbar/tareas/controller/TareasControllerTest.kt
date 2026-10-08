package org.uqbar.tareas.controller

import tools.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
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
class TareasControllerTest(@param:Autowired val mockMvc: MockMvc) {

    companion object {
        const val ID_INEXISTENTE = 99999
    }

    @Autowired
    lateinit var tareasRepository: TareasRepository

    @Autowired
    lateinit var usuariosRepository: UsuariosRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

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

    // region GET /tareas
    @Test
    fun `se pueden obtener todas las tareas`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/tareas"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(2))
    }
    // endregion

    // region GET /tareas/search
    @Test
    fun `se pueden pedir las tareas que contengan cierta descripcion`() {
        val tareaBusqueda = buildTarea()
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/tareas/search?descripcion=${tareaBusqueda.descripcion}")
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
                    .get("/tareas/search?descripcion=${tareaBusqueda.descripcion}")
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.length()").value(0))
    }
    // endregion

    // region GET /tareas/{id}
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
            .perform(MockMvcRequestBuilders.get("/tareas/$ID_INEXISTENTE"))
            .andExpect(status().isNotFound)
    }

    /**
     * Un id no numérico no llega al service: Spring lo rechaza con 400.
     */
    @Test
    fun `si se pide una tarea con un id no numerico se produce un error de cliente`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("/tareas/abc"))
            .andExpect(status().isBadRequest)
    }
    // endregion

    // region [actualizar] PUT /tareas/{id}
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
                    .content(objectMapper.writeValueAsString(tareaValida))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.porcentajeCumplimiento").value("70"))
    }

    /**
     * Con @Valid el 400 lo genera Spring (MethodArgumentNotValidException)
     * y el RestExceptionHandler devuelve los mensajes en el body.
     */
    @Test
    fun `si se intenta actualizar una tarea con datos incorrectos, el sistema rechaza la operacion`() {
        val tareaInvalida = buildTarea().apply {
            id = tarea.id
            descripcion = ""
        }

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put("/tareas/" + tarea.id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tareaInvalida))
            )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Debe ingresar descripcion"))
    }

    @Test
    fun `si se intenta actualizar una tarea sin fecha, el sistema rechaza la operacion`() {
        val tareaSinFecha = """
            {
                "id": ${tarea.id},
                "descripcion":  "Resolver testeo unitario de tarea",
                "fecha": null,
                "iteracion": "Iteracion 1",
                "asignadoA": "Guillermo Bianchi",
                "porcentajeCumplimiento": 40
            }
        """.trimIndent()

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put("/tareas/" + tarea.id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tareaSinFecha)
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        // Este error ocurre a nivel deserialización
        // Aplicamos split para ignorar la parte inicial, "JSON parse error: <nuestro mensaje>"
        assertEquals(errorMessage?.split(": ")?.last(), "Debe ingresar una fecha")
    }

    /**
     * Mismo caso que fecha null: error de usuario a nivel deserialización.
     */
    @Test
    fun `si se intenta actualizar una tarea con formato de fecha invalido, el sistema rechaza la operacion`() {
        val tareaFechaInvalida = """
            {
                "id": ${tarea.id},
                "descripcion":  "Resolver testeo unitario de tarea",
                "fecha": "no-es-fecha",
                "iteracion": "Iteracion 1",
                "asignadoA": "Guillermo Bianchi",
                "porcentajeCumplimiento": 40
            }
        """.trimIndent()

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put("/tareas/" + tarea.id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tareaFechaInvalida)
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage?.split(": ")?.last(), "Formato de fecha inválido, esperado dd/MM/yyyy")
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

    // endregion

    // region [crear] POST /tareas
    @Test
    fun `crear una tarea a un valor valido actualiza correctamente`() {
        val descripcionNuevaTarea = "Implementar un servicio REST para crear una tarea"
        val tareaValida = buildTarea().apply {
            descripcion = descripcionNuevaTarea
        }
        val nuevaTareaResponse = mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/tareas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tareaValida))
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andReturn().response.contentAsString

        val nuevaTareaObject = objectMapper.readValue(nuevaTareaResponse, Tarea::class.java)
        val nuevaTarea = tareasRepository.searchById(nuevaTareaObject.id!!)
        assertEquals(nuevaTarea!!.descripcion, descripcionNuevaTarea)
    }

    @Test
    fun `crear una tarea con asignatario lo agrega a su lista`() {
        val cantidadInicial = usuario.tareasAsignadas.size
        val descripcionNuevaTarea = "Tarea nueva asignada a Juan"
        val cuerpo = """
            {
                "descripcion":  "$descripcionNuevaTarea",
                "fecha": "21/05/2021",
                "iteracion": "Iteracion 1",
                "asignadoA": "${usuario.nombre}",
                "porcentajeCumplimiento": 10
            }
        """.trimIndent()

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/tareas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(cuerpo)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.asignadoA").value(usuario.nombre))

        assertEquals(cantidadInicial + 1, usuario.tareasAsignadas.size)
        assertEquals(
            descripcionNuevaTarea,
            usuario.tareasAsignadas.find { it.descripcion == descripcionNuevaTarea }?.descripcion
        )
    }

    /**
     * Se usa JSON crudo a propósito, porque buildTarea() asigna
     * la tarea al usuario y contaminaría la lista con una instancia transitoria.
     */
    @Test
    fun `actualizar con datos invalidos no altera la asignacion existente`() {
        val cantidadInicial = usuario.tareasAsignadas.size
        val tareaInvalida = """
            {
                "descripcion":  "",
                "fecha": "21/05/2021",
                "iteracion": "Iteracion 1",
                "asignadoA": "${usuario.nombre}",
                "porcentajeCumplimiento": 40
            }
        """.trimIndent()

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put("/tareas/" + tarea.id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tareaInvalida)
            )
            .andExpect(status().isBadRequest)

        assertEquals(tarea.id, usuario.tareasAsignadas.find { it.id == tarea.id }?.id)
        assertEquals(cantidadInicial, usuario.tareasAsignadas.size)
    }

    @Test
    fun `si se intenta crear una tarea con datos incorrectos, el sistema rechaza la operacion`() {
        val tareaInvalida = buildTarea().apply {
            descripcion = ""
        }

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/tareas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tareaInvalida))
            )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Debe ingresar descripcion"))
    }

    /**
     * Descripción válida a propósito: con @Valid, una descripción vacía
     * sería rechazada antes de llegar al chequeo del id en el service.
     */
    @Test
    fun `si se intenta crear una tarea pasando un id, el sistema rechaza la operacion`() {
        val tareaInvalida = buildTarea().apply {
            id = 100
            descripcion = "Tarea con id pasado por parámetro"
        }

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/tareas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tareaInvalida))
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

    @Test
    fun `reasignar una tarea a otro usuario actualiza ambas listas`() {
        val rodrigo = usuariosRepository.create(Usuario("Rodrigo Grisolia"))
        val tareaReasignada = """
            {
                "descripcion":  "${tarea.descripcion}",
                "fecha": "21/05/2021",
                "iteracion": "Iteracion 1",
                "asignadoA": "Rodrigo Grisolia",
                "porcentajeCumplimiento": 40
            }
        """.trimIndent()

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put("/tareas/${tarea.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tareaReasignada)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.asignadoA").value("Rodrigo Grisolia"))

        assertEquals(null, usuario.tareasAsignadas.find { it.id == tarea.id })
        assertEquals(tarea.id, rodrigo.tareasAsignadas.find { it.id == tarea.id }?.id)
    }

    @Test
    fun `reasignar una tarea al mismo usuario no la duplica en su lista`() {
        val cantidadInicial = usuario.tareasAsignadas.size
        val tareaReasignada = """
            {
                "descripcion":  "${tarea.descripcion}",
                "fecha": "21/05/2021",
                "iteracion": "Iteracion 1",
                "asignadoA": "${usuario.nombre}",
                "porcentajeCumplimiento": 40
            }
        """.trimIndent()

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put("/tareas/${tarea.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tareaReasignada)
            )
            .andExpect(status().isOk)

        assertEquals(cantidadInicial, usuario.tareasAsignadas.size)
        assertEquals(1, usuario.tareasAsignadas.filter { it.id == tarea.id }.size)
    }

    @Test
    fun `si se intenta actualizar una tarea con porcentaje fuera de rango, el sistema rechaza la operacion`() {
        val tareaInvalida = """
            {
                "descripcion":  "${tarea.descripcion}",
                "fecha": "21/05/2021",
                "iteracion": "Iteracion 1",
                "asignadoA": "${usuario.nombre}",
                "porcentajeCumplimiento": 150
            }
        """.trimIndent()

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .put("/tareas/" + tarea.id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tareaInvalida)
            )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Porcentaje de cumplimiento debe estar entre 0 y 100"))
    }

    @Test
    fun `si se intenta crear una tarea con porcentaje negativo, el sistema rechaza la operacion`() {
        val tareaInvalida = """
            {
                "descripcion":  "Tarea con porcentaje negativo",
                "fecha": "21/05/2021",
                "iteracion": "Iteracion 1",
                "porcentajeCumplimiento": -5
            }
        """.trimIndent()

        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/tareas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(tareaInvalida)
            )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Porcentaje de cumplimiento debe estar entre 0 y 100"))
    }

    // endregion

    // region DELETE /tarea/{id}
    @Test
    fun `se puede eliminar una tarea existente en forma exitosa`() {
        val tarea = buildTarea()
        tareasRepository.create(tarea)

        mockMvc.delete("/tareas/${tarea.id}")
            .andExpect { status { isOk() } }

        assertEquals(null, tareasRepository.searchById(tarea.id!!))
    }

    @Test
    fun `eliminar una tarea la quita de la lista del asignatario`() {
        val cantidadInicial = usuario.tareasAsignadas.size

        mockMvc.delete("/tareas/${tarea.id}")
            .andExpect { status { isOk() } }

        assertEquals(cantidadInicial - 1, usuario.tareasAsignadas.size)
        assertEquals(null, usuario.tareasAsignadas.find { it.id == tarea.id })
    }

    @Test
    fun `si se intenta eliminar una tarea con id inexistente se produce un error`() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tareas/$ID_INEXISTENTE"))
            .andExpect { status().isNotFound }
    }
    // endregion

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
