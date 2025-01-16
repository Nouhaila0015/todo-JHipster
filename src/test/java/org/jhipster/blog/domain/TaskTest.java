package org.jhipster.blog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jhipster.blog.domain.TaskTestSamples.*;
import static org.jhipster.blog.domain.TodoTestSamples.*;

import org.jhipster.blog.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Task.class);
        Task task1 = getTaskSample1();
        Task task2 = new Task();
        assertThat(task1).isNotEqualTo(task2);

        task2.setId(task1.getId());
        assertThat(task1).isEqualTo(task2);

        task2 = getTaskSample2();
        assertThat(task1).isNotEqualTo(task2);
    }

    @Test
    void todoTest() {
        Task task = getTaskRandomSampleGenerator();
        Todo todoBack = getTodoRandomSampleGenerator();

        task.setTodo(todoBack);
        assertThat(task.getTodo()).isEqualTo(todoBack);

        task.todo(null);
        assertThat(task.getTodo()).isNull();
    }
}
