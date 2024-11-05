import { Divider, Stack, Typography } from '@mui/material';
import { DragDropContext, OnDragEndResponder } from '@hello-pangea/dnd';
import { isEqual } from 'lodash';
import { useContext, useEffect, useState } from 'react';
import { getTasksByStatus, statuses, TasksByStatus } from './statuses';
import { TaskColumn } from './TaskColumn';
import { Task } from './task';
import { BoardContext } from '../context/BoardContext';

export const TaskListContent = () => {
  const [tasksByStatus, setTasksByStatus] = useState<TasksByStatus>(
    getTasksByStatus([]),
  );

  const { boardName, tasks, updateTask } = useContext(BoardContext);

  useEffect(() => {
    const newTasksByStatus = getTasksByStatus(tasks);
    if (!isEqual(newTasksByStatus, tasksByStatus)) {
      setTasksByStatus(newTasksByStatus);
    }
  }, [tasks]);

  const updateTaskStatusLocal = (
    sourceTask: Task,
    source: { status: Task['status']; index: number },
    destination: {
      status: Task['status'];
      index?: number;
    },
    tasksByStatus: TasksByStatus,
  ) => {
    if (source.status === destination.status) {
      const column = tasksByStatus[source.status];
      column.splice(source.index, 1);
      column.splice(destination.index ?? column.length + 1, 0, sourceTask);
      return {
        ...tasksByStatus,
        [destination.status]: column,
      };
    } else {
      const sourceColumn = tasksByStatus[source.status];
      const destinationColumn = tasksByStatus[destination.status];
      sourceColumn.splice(source.index, 1);
      destinationColumn.splice(
        destination.index ?? destinationColumn.length + 1,
        0,
        sourceTask,
      );
      return {
        ...tasksByStatus,
        [source.status]: sourceColumn,
        [destination.status]: destinationColumn,
      };
    }
  };

  const onDragEnd: OnDragEndResponder = (result) => {
    const { destination, source } = result;

    if (!destination) {
      return;
    }

    if (
      destination.droppableId === source.droppableId &&
      destination.index === source.index
    ) {
      return;
    }

    const sourceStatus = source.droppableId as Task['status'];
    const destinationStatus = destination.droppableId as Task['status'];
    const sourceTask = tasksByStatus[sourceStatus][source.index]!;

    setTasksByStatus(
      updateTaskStatusLocal(
        sourceTask,
        { status: sourceStatus, index: source.index },
        { status: destinationStatus, index: destination.index },
        tasksByStatus,
      ),
    );

    const task: Task = {
      ...sourceTask,
      index: destination.index,
      status: destinationStatus,
    };

    updateTask(sourceTask.id, task);
  };

  return (
    <DragDropContext onDragEnd={onDragEnd}>
      <Typography variant="h4" align="center" paddingTop="10px" width="100%">
        {boardName}
      </Typography>
      <Stack
        direction="row"
        divider={<Divider orientation="vertical" flexItem />}
        display="flex"
        sx={{ paddingTop: 3 }}
      >
        {statuses.map((status) => (
          <TaskColumn
            tasks={tasksByStatus[status]}
            status={status}
            key={status}
          />
        ))}
      </Stack>
    </DragDropContext>
  );
};
