import dayjs from 'dayjs/esm';

import { ITask, NewTask } from './task.model';

export const sampleWithRequiredData: ITask = {
  id: 9181,
  title: 'pourvu que de peur que',
  dueDate: dayjs('2025-01-16'),
  status: 'COMPLETED',
};

export const sampleWithPartialData: ITask = {
  id: 20483,
  title: 'hier puis bè',
  description: 'du moment que',
  dueDate: dayjs('2025-01-16'),
  status: 'IN_PROGRESS',
};

export const sampleWithFullData: ITask = {
  id: 13396,
  title: 'aussitôt que sitôt que',
  description: 'de manière à ce que au point que',
  dueDate: dayjs('2025-01-16'),
  status: 'PENDING',
};

export const sampleWithNewData: NewTask = {
  title: 'dénoncer',
  dueDate: dayjs('2025-01-15'),
  status: 'IN_PROGRESS',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
