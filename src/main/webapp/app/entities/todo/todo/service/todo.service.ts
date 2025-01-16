import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITodo, NewTodo } from '../todo.model';

export type PartialUpdateTodo = Partial<ITodo> & Pick<ITodo, 'id'>;

type RestOf<T extends ITodo | NewTodo> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestTodo = RestOf<ITodo>;

export type NewRestTodo = RestOf<NewTodo>;

export type PartialUpdateRestTodo = RestOf<PartialUpdateTodo>;

export type EntityResponseType = HttpResponse<ITodo>;
export type EntityArrayResponseType = HttpResponse<ITodo[]>;

@Injectable({ providedIn: 'root' })
export class TodoService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/todos');

  create(todo: NewTodo): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(todo);
    return this.http.post<RestTodo>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(todo: ITodo): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(todo);
    return this.http
      .put<RestTodo>(`${this.resourceUrl}/${this.getTodoIdentifier(todo)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(todo: PartialUpdateTodo): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(todo);
    return this.http
      .patch<RestTodo>(`${this.resourceUrl}/${this.getTodoIdentifier(todo)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTodo>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTodo[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTodoIdentifier(todo: Pick<ITodo, 'id'>): number {
    return todo.id;
  }

  compareTodo(o1: Pick<ITodo, 'id'> | null, o2: Pick<ITodo, 'id'> | null): boolean {
    return o1 && o2 ? this.getTodoIdentifier(o1) === this.getTodoIdentifier(o2) : o1 === o2;
  }

  addTodoToCollectionIfMissing<Type extends Pick<ITodo, 'id'>>(
    todoCollection: Type[],
    ...todosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const todos: Type[] = todosToCheck.filter(isPresent);
    if (todos.length > 0) {
      const todoCollectionIdentifiers = todoCollection.map(todoItem => this.getTodoIdentifier(todoItem));
      const todosToAdd = todos.filter(todoItem => {
        const todoIdentifier = this.getTodoIdentifier(todoItem);
        if (todoCollectionIdentifiers.includes(todoIdentifier)) {
          return false;
        }
        todoCollectionIdentifiers.push(todoIdentifier);
        return true;
      });
      return [...todosToAdd, ...todoCollection];
    }
    return todoCollection;
  }

  protected convertDateFromClient<T extends ITodo | NewTodo | PartialUpdateTodo>(todo: T): RestOf<T> {
    return {
      ...todo,
      createdAt: todo.createdAt?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restTodo: RestTodo): ITodo {
    return {
      ...restTodo,
      createdAt: restTodo.createdAt ? dayjs(restTodo.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTodo>): HttpResponse<ITodo> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTodo[]>): HttpResponse<ITodo[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
