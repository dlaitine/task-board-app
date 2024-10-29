import { TaskForm } from './TaskForm';
import { NewTask } from '../task';
import { useStompClient } from 'react-stomp-hooks';
import { Fab } from '@mui/material';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import { useState } from 'react';

export const NewTaskForm = () => {
  const stompClient = useStompClient();

  const [ isOpen, setIsOpen, ] = useState<boolean>(false);

  const toggleForm = () => {
    setIsOpen(prev => !prev);
  };

  const submitNewTask = (title: string, description: string) => {
    const newTask: NewTask = { title, description, };

    stompClient?.publish({
      destination: '/app/new-task',
      body: JSON.stringify(newTask)
    });
  }

  return (
    <>
      <Fab
        onClick={toggleForm}
        color="primary"
        aria-label="add-task-button"
        style={{
          position: 'fixed',
          bottom: '30px',
          left: '30px',
          background: '#0077B6'
      }}>
        <AddCircleIcon />
      </Fab>
    <TaskForm isOpen={isOpen} onClose={toggleForm} dialogTitle='New task' onSubmit={submitNewTask} />
    </>
  );

};
