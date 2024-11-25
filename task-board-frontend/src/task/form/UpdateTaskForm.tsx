import { TaskForm } from './TaskForm';
import { Task, UpdateTask } from '../task';
import { Button, Typography } from '@mui/material';
import { useContext, useState } from 'react';
import { BoardContext } from '../../context/BoardContext';

interface UpdateTaskFormProps {
  task: Task;
}
export const UpdateTaskForm = ({ task }: UpdateTaskFormProps) => {
  const { updateTask } = useContext(BoardContext);

  const [isOpen, setIsOpen] = useState<boolean>(false);

  const toggleForm = () => {
    setIsOpen((prev) => !prev);
  };

  const submitUpdateTask = (title: string, description: string) => {
    const updatedTask: UpdateTask = { ...task, title, description };

    updateTask(task.id, updatedTask);
  };

  return (
    <>
      <Button variant="outlined" onClick={toggleForm}>
        <Typography>View task</Typography>
      </Button>
      <TaskForm
        dialogTitle="View task"
        isOpen={isOpen}
        onClose={toggleForm}
        onSubmit={submitUpdateTask}
        defaultTitle={task.title}
        defaultDescription={task.description}
      />
    </>
  );
};
