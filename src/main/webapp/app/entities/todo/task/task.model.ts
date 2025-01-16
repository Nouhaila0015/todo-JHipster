import dayjs from 'dayjs/esm';
import { ITodo } from 'app/entities/todo/todo/todo.model';
import { TaskStatus } from 'app/entities/enumerations/task-status.model';

export interface ITask {
  id: number;
  title?: string | null;
  description?: string | null;
  dueDate?: dayjs.Dayjs | null;
  status?: keyof typeof TaskStatus | null;
  todo?: Pick<ITodo, 'id' | 'name'> | null;
}

export type NewTask = Omit<ITask, 'id'> & { id: null };
