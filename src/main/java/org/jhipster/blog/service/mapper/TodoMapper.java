package org.jhipster.blog.service.mapper;

import org.jhipster.blog.domain.Todo;
import org.jhipster.blog.domain.User;
import org.jhipster.blog.service.dto.TodoDTO;
import org.jhipster.blog.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Todo} and its DTO {@link TodoDTO}.
 */
@Mapper(componentModel = "spring")
public interface TodoMapper extends EntityMapper<TodoDTO, Todo> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    TodoDTO toDto(Todo s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
