package org.jhipster.blog.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TodoCriteriaTest {

    @Test
    void newTodoCriteriaHasAllFiltersNullTest() {
        var todoCriteria = new TodoCriteria();
        assertThat(todoCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void todoCriteriaFluentMethodsCreatesFiltersTest() {
        var todoCriteria = new TodoCriteria();

        setAllFilters(todoCriteria);

        assertThat(todoCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void todoCriteriaCopyCreatesNullFilterTest() {
        var todoCriteria = new TodoCriteria();
        var copy = todoCriteria.copy();

        assertThat(todoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(todoCriteria)
        );
    }

    @Test
    void todoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var todoCriteria = new TodoCriteria();
        setAllFilters(todoCriteria);

        var copy = todoCriteria.copy();

        assertThat(todoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(todoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var todoCriteria = new TodoCriteria();

        assertThat(todoCriteria).hasToString("TodoCriteria{}");
    }

    private static void setAllFilters(TodoCriteria todoCriteria) {
        todoCriteria.id();
        todoCriteria.name();
        todoCriteria.createdAt();
        todoCriteria.userId();
        todoCriteria.distinct();
    }

    private static Condition<TodoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TodoCriteria> copyFiltersAre(TodoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
