import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITodo, NewTodo } from '../todo.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITodo for edit and NewTodoFormGroupInput for create.
 */
type TodoFormGroupInput = ITodo | PartialWithRequiredKeyOf<NewTodo>;

type TodoFormDefaults = Pick<NewTodo, 'id'>;

type TodoFormGroupContent = {
  id: FormControl<ITodo['id'] | NewTodo['id']>;
  name: FormControl<ITodo['name']>;
  createdAt: FormControl<ITodo['createdAt']>;
  user: FormControl<ITodo['user']>;
};

export type TodoFormGroup = FormGroup<TodoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TodoFormService {
  createTodoFormGroup(todo: TodoFormGroupInput = { id: null }): TodoFormGroup {
    const todoRawValue = {
      ...this.getFormDefaults(),
      ...todo,
    };
    return new FormGroup<TodoFormGroupContent>({
      id: new FormControl(
        { value: todoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(todoRawValue.name, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(todoRawValue.createdAt, {
        validators: [Validators.required],
      }),
      user: new FormControl(todoRawValue.user),
    });
  }

  getTodo(form: TodoFormGroup): ITodo | NewTodo {
    return form.getRawValue() as ITodo | NewTodo;
  }

  resetForm(form: TodoFormGroup, todo: TodoFormGroupInput): void {
    const todoRawValue = { ...this.getFormDefaults(), ...todo };
    form.reset(
      {
        ...todoRawValue,
        id: { value: todoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TodoFormDefaults {
    return {
      id: null,
    };
  }
}
