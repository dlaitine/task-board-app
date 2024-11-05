import { Task } from './task';

export const statusNames: Record<Task['status'], string> = {
  BACKLOG: 'Backlog',
  TODO: 'To Do',
  IN_PROGRESS: 'In Progress',
  IN_REVIEW: 'In Review',
  DONE: 'Done',
};

export const statuses: Task['status'][] = [
  'BACKLOG',
  'TODO',
  'IN_PROGRESS',
  'IN_REVIEW',
  'DONE',
];

export type TasksByStatus = Record<Task['status'], Task[]>;

export const getTasksByStatus = (unorderedTasks: Task[]) => {
  const tasksByStatus: TasksByStatus = unorderedTasks.reduce(
    (acc, task) => {
      acc[task.status].push(task);
      return acc;
    },
    statuses.reduce(
      (obj, status) => ({ ...obj, [status]: [] }),
      {} as TasksByStatus,
    ),
  );

  const sortByIndex = (a: number | undefined, b: number | undefined) => {
    if (a === b) {
      return 0;
    }

    if (a === undefined) {
      return 1;
    }
    if (b === undefined) {
      return -1;
    }

    return a < b ? -1 : 1;
  };

  statuses.forEach((status) => {
    tasksByStatus[status] = tasksByStatus[status].sort(
      (recordA: Task, recordB: Task) =>
        sortByIndex(recordA.index, recordB.index),
    );
  });

  return tasksByStatus;
};
