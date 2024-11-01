import { TaskForm } from './TaskForm';
import { Task } from '../task/task';
import { useStompClient } from 'react-stomp-hooks';
import { Button, Typography } from '@mui/material';
import { useState } from 'react';

interface UpdateTaskFormProps {
  boardId: string;
  task: Task
}
export const UpdateTaskForm = ({ boardId, task }: UpdateTaskFormProps) => {
  const stompClient = useStompClient();

  const [ isOpen, setIsOpen, ] = useState<boolean>(false);

  const toggleForm = () => {
    setIsOpen(prev => !prev);
  };

  const submitUpdateTask = (title: string, description: string) => {
    const updatedTask: Task = { ...task, title, description, };

    stompClient?.publish({
      destination: `/app/${boardId}/update-task/${task.id}`,
      body: JSON.stringify(updatedTask)
    });
  }

  return (
    <>
      <Button variant='outlined' onClick={toggleForm}>
        <Typography>
          View task
        </Typography>
      </Button>
      <TaskForm dialogTitle='View task'
        isOpen={isOpen}
        onClose={toggleForm}
        onSubmit={submitUpdateTask}
        defaultTitle={task.title}
        defaultDescription={task.description}
      />
    </>
  );

};
