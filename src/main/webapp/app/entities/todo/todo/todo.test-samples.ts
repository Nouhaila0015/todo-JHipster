import dayjs from 'dayjs/esm';

import { ITodo, NewTodo } from './todo.model';

export const sampleWithRequiredData: ITodo = {
  id: 22333,
  name: 'hors de blablabla',
  createdAt: dayjs('2025-01-16'),
};

export const sampleWithPartialData: ITodo = {
  id: 2003,
  name: 'camarade tendre tellement',
  createdAt: dayjs('2025-01-15'),
};

export const sampleWithFullData: ITodo = {
  id: 4806,
  name: 'désigner',
  createdAt: dayjs('2025-01-15'),
};

export const sampleWithNewData: NewTodo = {
  name: 'oh crac ouin',
  createdAt: dayjs('2025-01-15'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
