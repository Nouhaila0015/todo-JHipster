package org.jhipster.blog.service.mapper;

import static org.jhipster.blog.domain.TaskAsserts.*;
import static org.jhipster.blog.domain.TaskTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskMapperTest {

    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTaskSample1();
        var actual = taskMapper.toEntity(taskMapper.toDto(expected));
        assertTaskAllPropertiesEquals(expected, actual);
    }
}
