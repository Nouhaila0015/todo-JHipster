package org.jhipster.blog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jhipster.blog.domain.TodoTestSamples.*;

import org.jhipster.blog.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TodoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Todo.class);
        Todo todo1 = getTodoSample1();
        Todo todo2 = new Todo();
        assertThat(todo1).isNotEqualTo(todo2);

        todo2.setId(todo1.getId());
        assertThat(todo1).isEqualTo(todo2);

        todo2 = getTodoSample2();
        assertThat(todo1).isNotEqualTo(todo2);
    }
}
