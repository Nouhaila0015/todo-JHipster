package org.jhipster.blog.service.mapper;

import org.jhipster.blog.domain.Task;
import org.jhipster.blog.domain.Todo;
import org.jhipster.blog.service.dto.TaskDTO;
import org.jhipster.blog.service.dto.TodoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    @Mapping(target = "todo", source = "todo", qualifiedByName = "todoName")
    TaskDTO toDto(Task s);

    @Named("todoName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TodoDTO toDtoTodoName(Todo todo);
}
