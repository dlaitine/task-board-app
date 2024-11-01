import { TaskListContent } from './TaskListContent';
import { NewTaskForm } from './form/NewTaskForm';
import ChatWindow from './chat/ChatWindow';
import { useEffect, useState } from 'react';
import { Message } from './chat/chat';
import { Task } from './task';
import { useParams } from 'react-router-dom';

const baseUrl = import.meta.env.VITE_TASK_BACKEND_BASE_URL;

export const BoardPage = () => {
  const { boardId, } = useParams() as { boardId: string };

  const [ boardName, setBoardName, ] = useState<string>('');
  const [ initialTasks, setInitialTasks, ] = useState<Task[]>([]);
  const [ chatHistory, setChatHistory, ] = useState<Message[]>([]);

  useEffect(() => {
    fetch(`${baseUrl}/boards/${boardId}?includeTasks=true&includeChatMessages=true`)
      .then((response) => response.json())
      .then((data) => {
        setBoardName(data.name);
        setInitialTasks(data.tasks);
        setChatHistory(data.chat_messages);
    });
  }, [ boardId, ]);

  return (
    <>
      <NewTaskForm boardId={boardId} />
      <TaskListContent boardId={boardId} boardName={boardName} initialTasks={initialTasks} />
      <ChatWindow boardId={boardId} boardName={boardName} chatHistory={chatHistory} />
    </>
  );
};