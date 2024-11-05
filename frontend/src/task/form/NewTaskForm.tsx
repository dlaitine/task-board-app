import { TaskForm } from './TaskForm';
import { NewTask } from '../task';
import { Fab } from '@mui/material';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import { useContext, useState } from 'react';
import { BoardContext } from '../../context/BoardContext';

export const NewTaskForm = () => {

  const { createTask, } = useContext(BoardContext);

  const [ isOpen, setIsOpen, ] = useState<boolean>(false);

  const toggleForm = () => {
    setIsOpen(prev => !prev);
  };

  const submitNewTask = (title: string, description: string) => {
    const newTask: NewTask = { title, description, };
    
    createTask(newTask);
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
