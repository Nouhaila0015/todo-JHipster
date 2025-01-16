import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'todoApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'todo',
    data: { pageTitle: 'todoApp.todoTodo.home.title' },
    loadChildren: () => import('./todo/todo/todo.routes'),
  },
  {
    path: 'task',
    data: { pageTitle: 'todoApp.todoTask.home.title' },
    loadChildren: () => import('./todo/task/task.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
