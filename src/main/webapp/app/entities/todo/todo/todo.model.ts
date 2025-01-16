import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface ITodo {
  id: number;
  name?: string | null;
  createdAt?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewTodo = Omit<ITodo, 'id'> & { id: null };
