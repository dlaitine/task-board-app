import { TaskListContent } from '../task/TaskListContent';
import { NewTaskForm } from '../task/form/NewTaskForm';
import LoadingSpinner from '../common/LoadingSpinner';
import { ChatWindow } from '../chat/ChatWindow';
import { BoardContext } from '../context/BoardContext';
import { useContext } from 'react';

export const BoardPage = () => {
  const { boardInitComplete, } = useContext(BoardContext);

  return (
    !boardInitComplete ?
    <LoadingSpinner /> :
    <>
      <NewTaskForm />
      <TaskListContent />
      <ChatWindow />
    </>
  ); 
};