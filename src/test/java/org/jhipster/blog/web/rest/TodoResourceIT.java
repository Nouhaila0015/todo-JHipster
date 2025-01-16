package org.jhipster.blog.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.jhipster.blog.domain.TodoAsserts.*;
import static org.jhipster.blog.web.rest.TestUtil.createUpdateProxyForBean;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.jhipster.blog.IntegrationTest;
import org.jhipster.blog.domain.Todo;
import org.jhipster.blog.domain.User;
import org.jhipster.blog.repository.TodoRepository;
import org.jhipster.blog.repository.UserRepository;
import org.jhipster.blog.service.TodoService;
import org.jhipster.blog.service.dto.TodoDTO;
import org.jhipster.blog.service.mapper.TodoMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TodoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TodoResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_AT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATED_AT = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/todos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private TodoRepository todoRepositoryMock;

    @Autowired
    private TodoMapper todoMapper;

    @Mock
    private TodoService todoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTodoMockMvc;

    private Todo todo;

    private Todo insertedTodo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Todo createEntity() {
        return new Todo().name(DEFAULT_NAME).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Todo createUpdatedEntity() {
        return new Todo().name(UPDATED_NAME).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        todo = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTodo != null) {
            todoRepository.delete(insertedTodo);
            insertedTodo = null;
        }
    }

    @Test
    @Transactional
    void createTodo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);
        var returnedTodoDTO = om.readValue(
            restTodoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(todoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TodoDTO.class
        );

        // Validate the Todo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTodo = todoMapper.toEntity(returnedTodoDTO);
        assertTodoUpdatableFieldsEquals(returnedTodo, getPersistedTodo(returnedTodo));

        insertedTodo = returnedTodo;
    }

    @Test
    @Transactional
    void createTodoWithExistingId() throws Exception {
        // Create the Todo with an existing ID
        todo.setId(1L);
        TodoDTO todoDTO = todoMapper.toDto(todo);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTodoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(todoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Todo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        todo.setName(null);

        // Create the Todo, which fails.
        TodoDTO todoDTO = todoMapper.toDto(todo);

        restTodoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(todoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        todo.setCreatedAt(null);

        // Create the Todo, which fails.
        TodoDTO todoDTO = todoMapper.toDto(todo);

        restTodoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(todoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTodos() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList
        restTodoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(todo.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTodosWithEagerRelationshipsIsEnabled() throws Exception {
        when(todoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTodoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(todoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTodosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(todoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTodoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(todoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTodo() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get the todo
        restTodoMockMvc
            .perform(get(ENTITY_API_URL_ID, todo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(todo.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getTodosByIdFiltering() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        Long id = todo.getId();

        defaultTodoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTodoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTodoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTodosByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList where name equals to
        defaultTodoFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTodosByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList where name in
        defaultTodoFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTodosByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList where name is not null
        defaultTodoFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllTodosByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList where name contains
        defaultTodoFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTodosByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList where name does not contain
        defaultTodoFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllTodosByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList where createdAt equals to
        defaultTodoFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTodosByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList where createdAt in
        defaultTodoFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTodosByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList where createdAt is not null
        defaultTodoFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTodosByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList where createdAt is greater than or equal to
        defaultTodoFiltering("createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT, "createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTodosByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList where createdAt is less than or equal to
        defaultTodoFiltering("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT, "createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTodosByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList where createdAt is less than
        defaultTodoFiltering("createdAt.lessThan=" + UPDATED_CREATED_AT, "createdAt.lessThan=" + DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTodosByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        // Get all the todoList where createdAt is greater than
        defaultTodoFiltering("createdAt.greaterThan=" + SMALLER_CREATED_AT, "createdAt.greaterThan=" + DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTodosByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            todoRepository.saveAndFlush(todo);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        todo.setUser(user);
        todoRepository.saveAndFlush(todo);
        Long userId = user.getId();
        // Get all the todoList where user equals to userId
        defaultTodoShouldBeFound("userId.equals=" + userId);

        // Get all the todoList where user equals to (userId + 1)
        defaultTodoShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultTodoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTodoShouldBeFound(shouldBeFound);
        defaultTodoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTodoShouldBeFound(String filter) throws Exception {
        restTodoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(todo.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restTodoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTodoShouldNotBeFound(String filter) throws Exception {
        restTodoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTodoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTodo() throws Exception {
        // Get the todo
        restTodoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTodo() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the todo
        Todo updatedTodo = todoRepository.findById(todo.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTodo are not directly saved in db
        em.detach(updatedTodo);
        updatedTodo.name(UPDATED_NAME).createdAt(UPDATED_CREATED_AT);
        TodoDTO todoDTO = todoMapper.toDto(updatedTodo);

        restTodoMockMvc
            .perform(put(ENTITY_API_URL_ID, todoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(todoDTO)))
            .andExpect(status().isOk());

        // Validate the Todo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTodoToMatchAllProperties(updatedTodo);
    }

    @Test
    @Transactional
    void putNonExistingTodo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        todo.setId(longCount.incrementAndGet());

        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTodoMockMvc
            .perform(put(ENTITY_API_URL_ID, todoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(todoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Todo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTodo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        todo.setId(longCount.incrementAndGet());

        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTodoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(todoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Todo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTodo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        todo.setId(longCount.incrementAndGet());

        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTodoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(todoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Todo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTodoWithPatch() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the todo using partial update
        Todo partialUpdatedTodo = new Todo();
        partialUpdatedTodo.setId(todo.getId());

        restTodoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTodo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTodo))
            )
            .andExpect(status().isOk());

        // Validate the Todo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTodoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTodo, todo), getPersistedTodo(todo));
    }

    @Test
    @Transactional
    void fullUpdateTodoWithPatch() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the todo using partial update
        Todo partialUpdatedTodo = new Todo();
        partialUpdatedTodo.setId(todo.getId());

        partialUpdatedTodo.name(UPDATED_NAME).createdAt(UPDATED_CREATED_AT);

        restTodoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTodo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTodo))
            )
            .andExpect(status().isOk());

        // Validate the Todo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTodoUpdatableFieldsEquals(partialUpdatedTodo, getPersistedTodo(partialUpdatedTodo));
    }

    @Test
    @Transactional
    void patchNonExistingTodo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        todo.setId(longCount.incrementAndGet());

        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTodoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, todoDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(todoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Todo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTodo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        todo.setId(longCount.incrementAndGet());

        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTodoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(todoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Todo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTodo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        todo.setId(longCount.incrementAndGet());

        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTodoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(todoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Todo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTodo() throws Exception {
        // Initialize the database
        insertedTodo = todoRepository.saveAndFlush(todo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the todo
        restTodoMockMvc
            .perform(delete(ENTITY_API_URL_ID, todo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return todoRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Todo getPersistedTodo(Todo todo) {
        return todoRepository.findById(todo.getId()).orElseThrow();
    }

    protected void assertPersistedTodoToMatchAllProperties(Todo expectedTodo) {
        assertTodoAllPropertiesEquals(expectedTodo, getPersistedTodo(expectedTodo));
    }

    protected void assertPersistedTodoToMatchUpdatableProperties(Todo expectedTodo) {
        assertTodoAllUpdatablePropertiesEquals(expectedTodo, getPersistedTodo(expectedTodo));
    }
}
