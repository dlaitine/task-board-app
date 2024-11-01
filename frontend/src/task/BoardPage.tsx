import { TaskListContent } from './TaskListContent';
import { NewTaskForm } from './form/NewTaskForm';
import ChatWindow from './chat/ChatWindow';
import { useContext, useEffect, useState } from 'react';
import { Message } from './chat/chat';
import { Task } from './task';
import { useNavigate, useParams } from 'react-router-dom';
import { NotificationContext } from './context/NotificationContext';
import LoadingSpinner from './LoadingSpinner';

const baseUrl = import.meta.env.VITE_TASK_BACKEND_BASE_URL;

export const BoardPage = () => {
  const navigate = useNavigate();

  const { boardId, } = useParams() as { boardId: string; };

  const { setMessage, setSeverity, } = useContext(NotificationContext);

  const [ boardName, setBoardName, ] = useState<string>('');
  const [ initialTasks, setInitialTasks, ] = useState<Task[]>([]);
  const [ chatHistory, setChatHistory, ] = useState<Message[]>([]);
  const [ loading, setLoading, ] = useState<boolean>(false);

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    fetch(`${baseUrl}/boards/${boardId}?includeTasks=true&includeChatMessages=true`)
      .then((response) => {
        if (response.ok) {
          return response.json();
        }
        else {
          navigate('/not-found');
          throw Error('Board ' + boardId + ' not found');
        }
      })
      .then((data) => {
        setBoardName(data.name);
        setInitialTasks(data.tasks);
        setChatHistory(data.chat_messages);
      })
      .finally(() => {
        setLoading(false);
      })
      .catch((error) => {
        if (!controller.signal.aborted) {
          setSeverity('error');
          setMessage(error.message);
        }
      });

      return () => {
        controller.abort(); // Cancel the fetch request when the component is unmounted
      };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [ boardId, ]);

  return (
    loading ?
    <LoadingSpinner /> :
    <>
      <NewTaskForm boardId={boardId} />
      <TaskListContent boardId={boardId} boardName={boardName} initialTasks={initialTasks} />
      <ChatWindow boardId={boardId} boardName={boardName} chatHistory={chatHistory} />
    </>
  ); 
};