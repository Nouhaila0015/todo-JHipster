import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { TodoService } from '../entities/todo/todo/service/todo.service';
import { TaskService } from '../entities/todo/task/service/task.service';
import { AccountService } from '../core/auth/account.service';
import { ITodo } from '../entities/todo/todo/todo.model';
import { ITask } from '../entities/todo/task/task.model';
import { Account } from '../core/auth/account.model';
import SharedModule from 'app/shared/shared.module';
import dayjs from 'dayjs/esm';
import { TaskStatus } from '../entities/enumerations/task-status.model';
import { LoginService } from '../login/login.service';

@Component({
  selector: 'jhi-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  standalone: true,
  imports: [RouterModule, SharedModule],
})
export default class DashboardComponent implements OnInit {
  account = signal<Account | null>(null);
  todos = signal<ITodo[]>([]);
  selectedTodo = signal<ITodo | null>(null);
  tasks = signal<ITask[]>([]);
  today = dayjs();

  private accountService = inject(AccountService);
  private todoService = inject(TodoService);
  private taskService = inject(TaskService);
  private router = inject(Router);
  private loginService = inject(LoginService);

  ngOnInit(): void {
    // Cacher la navbar
    document.body.classList.add('hide-navbar');
    this.accountService.identity().subscribe(account => {
      this.account.set(account);
      this.loadTodos();
    });
  }
  ngOnDestroy(): void {
    // Restaurer la navbar quand on quitte le dashboard
    document.body.classList.remove('hide-navbar');
  }

  loadTodos(): void {
    this.todoService.query().subscribe({
      next: response => {
        const userTodos = response.body?.filter(todo => todo.user?.login === this.account()?.login) ?? [];
        this.todos.set(userTodos);
      },
    });
  }

  selectTodo(todo: ITodo): void {
    this.selectedTodo.set(todo);
    this.loadTasks(todo.id);
  }

  loadTasks(todoId: number): void {
    this.taskService.query({ 'todoId.equals': todoId }).subscribe({
      next: response => {
        this.tasks.set(response.body ?? []);
      },
    });
  }

  updateTaskStatus(task: ITask): void {
    const updatedTask: ITask = {
      ...task,
      status: (task.status === 'COMPLETED' ? 'PENDING' : 'COMPLETED') as TaskStatus,
    };

    this.taskService.update(updatedTask).subscribe(() => {
      this.loadTasks(this.selectedTodo()?.id ?? 0);
    });
  }

  // Navigation vers les pages CRUD existantes
  createTodo(): void {
    this.router.navigate(['/todo/new']);
  }

  editTodo(todo: ITodo): void {
    this.router.navigate(['/todo', todo.id, 'edit']);
  }

  deleteTodo(todo: ITodo): void {
    this.router.navigate(['/todo', todo.id, 'delete']);
  }

  createTask(): void {
    if (this.selectedTodo()) {
      this.router.navigate(['/task/new'], {
        queryParams: { todoId: this.selectedTodo()?.id },
      });
    }
  }

  editTask(task: ITask): void {
    this.router.navigate(['/task', task.id, 'edit']);
  }

  deleteTask(task: ITask): void {
    this.router.navigate(['/task', task.id, 'delete']);
  }

  logout(): void {
    this.loginService.logout();
    this.router.navigate(['/']);
  }
}
