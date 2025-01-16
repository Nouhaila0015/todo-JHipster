package org.jhipster.blog.service;

import jakarta.persistence.criteria.JoinType;
import org.jhipster.blog.domain.*; // for static metamodels
import org.jhipster.blog.domain.Todo;
import org.jhipster.blog.repository.TodoRepository;
import org.jhipster.blog.service.criteria.TodoCriteria;
import org.jhipster.blog.service.dto.TodoDTO;
import org.jhipster.blog.service.mapper.TodoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Todo} entities in the database.
 * The main input is a {@link TodoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TodoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TodoQueryService extends QueryService<Todo> {

    private static final Logger LOG = LoggerFactory.getLogger(TodoQueryService.class);

    private final TodoRepository todoRepository;

    private final TodoMapper todoMapper;

    public TodoQueryService(TodoRepository todoRepository, TodoMapper todoMapper) {
        this.todoRepository = todoRepository;
        this.todoMapper = todoMapper;
    }

    /**
     * Return a {@link Page} of {@link TodoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TodoDTO> findByCriteria(TodoCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Todo> specification = createSpecification(criteria);
        return todoRepository.findAll(specification, page).map(todoMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TodoCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Todo> specification = createSpecification(criteria);
        return todoRepository.count(specification);
    }

    /**
     * Function to convert {@link TodoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Todo> createSpecification(TodoCriteria criteria) {
        Specification<Todo> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Todo_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Todo_.name));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Todo_.createdAt));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(Todo_.user, JoinType.LEFT).get(User_.id))
                );
            }
        }
        return specification;
    }
}
