package com.pe.cmsystem.api.commond.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.cmsystem.api.commond.controllers.CMSystemPageable;
import com.pe.cmsystem.api.commond.controllers.CMSystemRequestLOV;
import com.pe.cmsystem.api.commond.controllers.CMSystemResponseRest;
import com.pe.cmsystem.api.commond.eo.CMSystemEntityID;
import com.pe.cmsystem.api.commond.usuario.eo.UsuarioEO;
import com.pe.cmsystem.api.commond.usuario.service.TokenCMSystemService;
import com.pe.cmsystem.api.commond.usuario.service.TokenCMSystemServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test para verificar la lógica de la creación del token
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
public abstract class CMSystemControllerTest<T extends CMSystemEntityID> {

    private static final String USER_SECRET = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";
    private static final String USER_APP = "CMSystemTest";
    private static final String FORMAT_BEARER = "Bearer %s";
    private static final String LABEL_ID = "id";
    private static final String MGS_EL_MENSAJE_DE_ERROR_NO_DEBE_SER_NULL = "El mensaje de error no debe ser null";
    private static final String MGS_EL_MENSAJE_DE_ERROR_DEBE_SER_OPERACION_DE_NEGOCIO_NO_ENCONTRO_RESULTADOS = "El mensaje de error debe ser 'Operación de negocio no encontró resultados'";
    private static final String MGS_OPERACION_DE_NEGOCIO_NO_ENCONTRO_RESULTADOS = "Operación de negocio no encontró resultados";


    /**
     * MockMvc para realizar las pruebas a los Controllers
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Convierte un objeto a un tipo específico
     *
     * @param f       campo
     * @param entidad entidad
     * @param <T>     tipo de entidad
     * @return objeto convertido
     * @throws IllegalAccessException si ocurre un error al acceder al campo
     */
    private static <T extends CMSystemEntityID> Object toConvertObject(Field f, T entidad) throws IllegalAccessException {
        return Optional.ofNullable(f.get(entidad))
                .map(v -> toConvertObject(v))
                .orElse(null);
    }

    private static Object toConvertObject(Object v) {
        if (v instanceof Long) {
            return ((Long) v).intValue();
        } else if (v instanceof Enum<?>) {
            return v.toString();
        }
        return v;
    }


    /**
     * Obtiene un UsuarioEO
     *
     * @return UsuarioEO con datos de prueba
     */
    protected abstract UsuarioEO getTokenUsuarioEO();

    /**
     * Obtiene el contexto del Controller
     *
     * @return String con el contexto del Controller
     */
    protected abstract String getContext();

    /**
     * Obtiene un Map con los valores esperados para el test FindById
     *
     * @return Map con los valores esperados
     */
    protected abstract Map<String, Object> getEsperadoTestFindById();

    /**
     * Obtiene un CMSystemRequestLOV con los valores esperados para el test FindLOV
     *
     * @return CMSystemRequestLOV con los valores esperados
     */
    protected abstract CMSystemPageable getEsperadoTestFindAll();

    /**
     * Obtiene un CMSystemRequestLOV con los valores esperados para el test FindLOV
     *
     * @return CMSystemRequestLOV con los valores esperados
     */
    protected abstract CMSystemRequestLOV getEsperadoTestFindLOV();

    /**
     * Obtiene el ID esperado para el test DeleteById
     *
     * @return ID a eliminar
     */
    protected abstract Long getEsperadoDeleteById();

    /**
     * Obtiene la entidad esperada para el test Delete
     *
     * @return entidad a eliminar
     */
    protected abstract T getEsperadoDeleteEntity();

    /**
     * Obtiene la entidad esperada para el test Update
     *
     * @return
     */
    protected abstract T getEsperadoUpdateEntity();

    /**
     * Obtiene la entidad esperada para el test Crear
     *
     * @return entidad a crear
     */
    protected abstract T getEsperadoCrearEntity();


    /**
     * Genera una token JWT
     *
     * @return token JWT (String)
     */
    private String getToken() {
        TokenCMSystemService tokenCMSystemService = new TokenCMSystemServiceImpl();
        return tokenCMSystemService.generateToken(getTokenUsuarioEO(), 100000, USER_SECRET, USER_APP);
    }

    /**
     * Test para verificar la lógica de la búsqueda por ID
     *
     * @throws Exception sí ocurre un error al realizar la petición
     */
    @Test
    @DisplayName("Test 001 CRUD FindById")
    synchronized void test001CrudFindById() throws Exception {
        assertTrue(getEsperadoTestFindById().containsKey(LABEL_ID));
        MvcResult requestResult = this.mockMvc.perform(get(getContext() + "/findById/{id}", getEsperadoTestFindById().get(LABEL_ID))
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var rest = new ObjectMapper().readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(rest)
                .isNotNull()
                .extracting(CMSystemResponseRest::getData)
                .isNotNull();
        Optional.ofNullable(getEsperadoTestFindById())
                .ifPresent(mapEsperado -> mapEsperado.forEach((key, value) -> {
                    Optional.ofNullable(value).ifPresentOrElse(v -> assertThat(rest.getData())
                                    .as("El valor del atributo %s debe ser %s", key, value)
                                    .extracting(key)
                                    .isEqualTo(value instanceof Long ? ((Long) value).intValue() : value),
                            () -> assertThat(rest.getData())
                                    .as("El valor del atributo %s debe ser ser null", key)
                                    .extracting(key)
                                    .isNull());
                }));
    }

    /**
     * Test para verificar la lógica de buscar todos los registros con paginación
     *
     * @throws Exception si ocurre un error al realizar la petición
     */
    @Test
    @DisplayName("Test 002 CRUD FindAll")
    synchronized void test002CrudFindAll() throws Exception {
        CMSystemPageable pageable = getEsperadoTestFindAll();
        ObjectMapper mapper = new ObjectMapper();
        String jsonPageable = mapper.writeValueAsString(pageable);
        MvcResult requestResult = this.mockMvc.perform(post(getContext() + "/findAll")
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPageable))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var rest = new ObjectMapper().readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(rest)
                .isNotNull()
                .extracting(CMSystemResponseRest::getData)
                .isNotNull();
        assertThat((List) rest.getData())
                .isNotNull()
                .as("El tamaño de la lista debe ser mayor igual %s", pageable.getSize())
                .hasSizeGreaterThanOrEqualTo(pageable.getSize());
    }

    /**
     * Test para verificar la lógica de buscar en formato LOV
     *
     * @throws Exception si ocurre un error al realizar la petición
     */
    @Test
    @DisplayName("Test 003 CRUD FindLOV")
    synchronized void test003CrudFindLOV() throws Exception {
        CMSystemRequestLOV configLOV = getEsperadoTestFindLOV();
        ObjectMapper mapper = new ObjectMapper();
        String jsonConfigLOV = mapper.writeValueAsString(configLOV);
        MvcResult requestResult = this.mockMvc.perform(post(getContext() + "/findLOV")
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonConfigLOV))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var rest = new ObjectMapper().readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(rest)
                .isNotNull()
                .extracting(CMSystemResponseRest::getData)
                .isNotNull();
        assertThat((List) rest.getData())
                .satisfies(data -> {
                    List<Map<String, Object>> list = (ArrayList<Map<String, Object>>) data;
                    list.forEach(map -> {
                        Optional.ofNullable(configLOV.getCampos())
                                .ifPresent(keys -> keys.forEach(key -> {
                                    assertThat(map)
                                            .as("El atrubuto %s debe existir", key)
                                            .containsKey(key);
                                }));
                    });
                });
    }

    /**
     * Test para verificar la lógica de eliminar por ID
     *
     * @throws Exception si ocurre un error al realizar la petición
     */
    @Test
    @DisplayName("Test 004 CRUD DeleteById")
    synchronized void test004CrudDeleteById() throws Exception {
        Long id = getEsperadoDeleteById();
        MvcResult requestResult = this.mockMvc.perform(delete(getContext() + "/deleteById/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var rest = new ObjectMapper().readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(rest)
                .isNotNull()
                .extracting(CMSystemResponseRest::getData)
                .isEqualTo(Boolean.TRUE);

        MvcResult requestResultVal = this.mockMvc.perform(get(getContext() + "/findById/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
        var restVal = new ObjectMapper().readValue(requestResultVal.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(restVal)
                .isNotNull()
                .extracting(CMSystemResponseRest::getMessage)
                .as(MGS_EL_MENSAJE_DE_ERROR_NO_DEBE_SER_NULL)
                .isNotNull()
                .as(MGS_EL_MENSAJE_DE_ERROR_DEBE_SER_OPERACION_DE_NEGOCIO_NO_ENCONTRO_RESULTADOS)
                .isEqualTo(MGS_OPERACION_DE_NEGOCIO_NO_ENCONTRO_RESULTADOS);
    }

    /**
     * Test para verificar la lógica de eliminar por ID
     *
     * @throws Exception si ocurre un error al realizar la petición
     */
    @Test
    @DisplayName("Test 005 CRUD Delete")
    synchronized void test005CrudDelete() throws Exception {
        T entidad = getEsperadoDeleteEntity();
        ObjectMapper mapper = new ObjectMapper();
        String jsonEntidad = mapper.writeValueAsString(entidad);
        MvcResult requestResult = this.mockMvc.perform(delete(getContext() + "/delete")
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEntidad))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var rest = new ObjectMapper().readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(rest)
                .isNotNull()
                .extracting(CMSystemResponseRest::getData)
                .isEqualTo(Boolean.TRUE);
        MvcResult requestResultVal = this.mockMvc.perform(get(getContext() + "/findById/{id}", entidad.getId())
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
        var restVal = new ObjectMapper().readValue(requestResultVal.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(restVal)
                .isNotNull()
                .extracting(CMSystemResponseRest::getMessage)
                .as(MGS_EL_MENSAJE_DE_ERROR_NO_DEBE_SER_NULL)
                .isNotNull()
                .as(MGS_EL_MENSAJE_DE_ERROR_DEBE_SER_OPERACION_DE_NEGOCIO_NO_ENCONTRO_RESULTADOS)
                .isEqualTo(MGS_OPERACION_DE_NEGOCIO_NO_ENCONTRO_RESULTADOS);
    }

    /**
     * Test para verificar la lógica de la modificación de un registro
     *
     * @throws Exception si ocurre un error al realizar la petición
     */
    @Test
    @DisplayName("Test 006 CRUD Update")
    synchronized void test006CrudUpdate() throws Exception {
        UsuarioEO usuarioToken = getTokenUsuarioEO();
        T entidad = getEsperadoUpdateEntity();
        List<Field> campos = Arrays.stream(entidad.getClass().getDeclaredFields())
                .filter(f -> !f.getName().equals("id") && !f.getName().equals("usuins") && !f.getName().equals("datins") && !f.getName().equals("usumod") && !f.getName().equals("datmod") && !f.getName().equals("flgact"))
                .collect(Collectors.toList());
        assertThat(campos).isNotEmpty();
        ObjectMapper mapper = new ObjectMapper();
        String jsonEntidad = mapper.writeValueAsString(entidad);
        MvcResult requestResult = this.mockMvc.perform(put(getContext() + "/update")
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEntidad))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var rest = new ObjectMapper().readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(rest)
                .isNotNull()
                .extracting(CMSystemResponseRest::getData)
                .isNotNull();
        Optional.ofNullable((Map<String, Object>) rest.getData())
                .ifPresent(map -> map.forEach((key, value) -> {
                    Optional.ofNullable(value).ifPresent(v -> campos
                            .stream().filter(f -> f.getName().equals(key))
                            .forEach(f -> {
                                f.setAccessible(true);
                                try {
                                    assertThat(toConvertObject(f, entidad))
                                            .as("El valor del atributo %s debe ser %s", key, v)
                                            .isEqualTo(v);
                                } catch (IllegalAccessException e) {
                                    fail("El atributo no existe " + key);
                                }
                            })
                    );
                    if (key.equals(LABEL_ID)) {
                        assertThat(value)
                                .as("El valor del atributo %s debe ser %s", key, entidad.getId())
                                .isEqualTo(toConvertObject(entidad.getId()));
                    }
                    if (key.equals("usumod")) {
                        assertThat(value)
                                .as("El valor del atributo %s debe ser %s", key, entidad.getUsuins())
                                .isEqualTo(toConvertObject(usuarioToken.getId()));
                    }
                }));

        MvcResult requestResultVal = this.mockMvc.perform(get(getContext() + "/findById/{id}", entidad.getId())
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var restVal = new ObjectMapper().readValue(requestResultVal.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(restVal)
                .isNotNull()
                .extracting(CMSystemResponseRest::getData)
                .isNotNull();
        Optional.ofNullable((Map<String, Object>) restVal.getData())
                .ifPresent(map -> map.forEach((key, value) -> {
                    Optional.ofNullable(value).ifPresent(v -> campos
                            .stream().filter(f -> f.getName().equals(key))
                            .forEach(f -> {
                                f.setAccessible(true);
                                try {
                                    assertThat(toConvertObject(f, entidad))
                                            .as("El valor del atributo %s debe ser %s", key, v)
                                            .isEqualTo(v);
                                } catch (IllegalAccessException e) {
                                    fail("El atributo no existe " + key);
                                }
                            })
                    );
                    if (key.equals(LABEL_ID)) {
                        assertThat(value)
                                .as("El valor del atributo %s debe ser %s", key, entidad.getId())
                                .isEqualTo(toConvertObject(entidad.getId()));
                    }
                    if (key.equals("usumod")) {
                        assertThat(value)
                                .as("El valor del atributo %s debe ser %s", key, entidad.getUsuins())
                                .isEqualTo(toConvertObject(usuarioToken.getId()));
                    }
                }));
    }

    /**
     * Test para verificar la lógica de la modificación de un registro
     *
     * @throws Exception si ocurre un error al realizar la petición
     */
    @Test
    @DisplayName("Test 007 CRUD Crear")
    synchronized void test007CrudCrear() throws Exception {
        UsuarioEO usuarioToken = getTokenUsuarioEO();
        T entidad = getEsperadoCrearEntity();
        List<Field> campos = Arrays.stream(entidad.getClass().getDeclaredFields())
                .filter(f -> !f.getName().equals("id") && !f.getName().equals("usuins") && !f.getName().equals("datins") && !f.getName().equals("usumod") && !f.getName().equals("datmod") && !f.getName().equals("flgact"))
                .collect(Collectors.toList());
        assertThat(campos).isNotEmpty();
        ObjectMapper mapper = new ObjectMapper();
        String jsonEntidad = mapper.writeValueAsString(entidad);
        MvcResult requestResult = this.mockMvc.perform(post(getContext() + "/create")
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonEntidad))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var rest = new ObjectMapper().readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(rest)
                .isNotNull()
                .extracting(CMSystemResponseRest::getData)
                .isNotNull();
        Optional.ofNullable((Map<String, Object>) rest.getData())
                .ifPresent(map -> map.forEach((key, value) -> {
                    Optional.ofNullable(value).ifPresent(v -> campos
                            .stream().filter(f -> f.getName().equals(key))
                            .forEach(f -> {
                                f.setAccessible(true);
                                try {
                                    assertThat(toConvertObject(f, entidad))
                                            .as("El valor del atributo %s debe ser %s", key, v)
                                            .isEqualTo(v);
                                } catch (IllegalAccessException e) {
                                    fail("El atributo no existe " + key);
                                }
                            })
                    );
                    if (key.equals(LABEL_ID)) {
                        assertThat(value)
                                .as("El valor del atributo %s no puede ser null", key)
                                .isNotNull();
                        entidad.setId(Long.parseLong(value.toString()));
                    }
                    if (key.equals("usuins")) {
                        assertThat(value)
                                .as("El valor del atributo %s debe ser %s", key, entidad.getUsuins())
                                .isEqualTo(toConvertObject(usuarioToken.getId()));
                    }
                    if (key.equals("usumod")) {
                        assertThat(value)
                                .as("El valor del atributo %s debe ser no null y igual %s", key, usuarioToken.getId())
                                .isNotNull()
                                .isEqualTo(toConvertObject(usuarioToken.getId()));
                    }
                }));

        MvcResult requestResultVal = this.mockMvc.perform(get(getContext() + "/findById/{id}", entidad.getId())
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var restVal = new ObjectMapper().readValue(requestResultVal.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(restVal)
                .isNotNull()
                .extracting(CMSystemResponseRest::getData)
                .isNotNull();
        Optional.ofNullable((Map<String, Object>) restVal.getData())
                .ifPresent(map -> map.forEach((key, value) -> {
                    Optional.ofNullable(value).ifPresent(v -> campos
                            .stream().filter(f -> f.getName().equals(key))
                            .forEach(f -> {
                                f.setAccessible(true);
                                try {
                                    assertThat(toConvertObject(f, entidad))
                                            .as("El valor del atributo %s debe ser %s", key, v)
                                            .isEqualTo(v);
                                } catch (IllegalAccessException e) {
                                    fail("El atributo no existe " + key);
                                }
                            })
                    );
                    if (key.equals(LABEL_ID)) {
                        assertThat(value)
                                .as("El valor del atributo %s debe ser %s", key, entidad.getId())
                                .isEqualTo(toConvertObject(entidad.getId()));
                    }
                    if (key.equals("usuins")) {
                        assertThat(value)
                                .as("El valor del atributo %s debe ser %s", key, entidad.getUsuins())
                                .isEqualTo(toConvertObject(usuarioToken.getId()));
                    }
                    if (key.equals("usumod")) {
                        assertThat(value)
                                .as("El valor del atributo %s debe ser %s", key, entidad.getUsumod())
                                .isEqualTo(toConvertObject(usuarioToken.getId()));
                    }
                }));
    }

    @Test
    @DisplayName("Test 008 CRUD NoResourceFoundException")
    synchronized void test008CRUDNoResourceFoundException() throws Exception {
        MvcResult requestResult = this.mockMvc.perform(get(getContext() + "/findNoResourceFoundException/{id}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
        var rest = new ObjectMapper().readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(rest)
                .isNotNull()
                .extracting(CMSystemResponseRest::getData)
                .isNull();
    }

    @Test
    @DisplayName("Test 009 CRUD Exception")
    synchronized void test009CRUDException() throws Exception {
        assertTrue(getEsperadoTestFindById().containsKey(LABEL_ID));
        MvcResult requestResult = this.mockMvc.perform(get(getContext() + "/findById/{id}", "A")
                        .header(HttpHeaders.AUTHORIZATION, String.format(FORMAT_BEARER, getToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        var rest = new ObjectMapper().readValue(requestResult.getResponse().getContentAsString(StandardCharsets.UTF_8), CMSystemResponseRest.class);
        assertThat(rest)
                .isNotNull()
                .extracting(CMSystemResponseRest::getData)
                .isNull();
    }
}

