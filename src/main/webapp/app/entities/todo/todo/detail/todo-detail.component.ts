import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { ITodo } from '../todo.model';

@Component({
  selector: 'jhi-todo-detail',
  templateUrl: './todo-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class TodoDetailComponent {
  todo = input<ITodo | null>(null);

  previousState(): void {
    window.history.back();
  }
}
