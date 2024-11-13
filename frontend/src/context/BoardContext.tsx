import React, { createContext, useContext, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { NotificationContext } from './NotificationContext';
import { NewTask, Task, UpdateTask } from '../task/task';
import { Message, NewMessage } from '../chat/chat';
import { useStompClient, useSubscription } from 'react-stomp-hooks';
import { baseUrl } from '../common/constants';
import { LoginContext } from './LoginContext';

interface BoardContextProps {
  boardInitComplete: boolean;
  boardName: string;
  tasks: Task[];
  messages: Message[];
  newMessages: boolean;
  setNewMessages: (seen: boolean) => void;
  createTask: (newTask: NewTask) => void;
  updateTask: (taskId: number, task: UpdateTask) => void;
  sendMessage: (newMessage: string) => void;
}

export const BoardContext = createContext<BoardContextProps>({
  boardInitComplete: false,
  boardName: '',
  tasks: [],
  messages: [],
  newMessages: false,
  setNewMessages: () => {},
  createTask: () => {},
  updateTask: () => {},
  sendMessage: () => {},
});

interface BoardProviderProps {
  children: React.ReactNode;
}

export const BoardProvider = ({ children }: BoardProviderProps) => {
  const { boardId } = useParams() as { boardId: string };

  const navigate = useNavigate();
  const stompClient = useStompClient();

  const { username } = useContext(LoginContext);
  const { setMessage, setSeverity } = useContext(NotificationContext);

  const [boardName, setBoardName] = useState<string>('');
  const [tasks, setTasks] = useState<Task[]>([]);
  const [messages, setMessages] = useState<Message[]>([]);
  const [newMessages, setNewMessages] = useState<boolean>(false);
  const [boardInitComplete, setBoardInitComplete] = useState<boolean>(false);

  useSubscription(`/topic/${boardId}/new-task`, (message) => {
    const task: Task = JSON.parse(message.body);
    setTasks((prevTasks) => [...prevTasks, task]);
  });

  useSubscription(`/topic/${boardId}/update-tasks`, (message) => {
    const updatedTasks: Task[] = JSON.parse(message.body);
    setTasks((prevTasks) =>
      prevTasks.map((prevTask) => {
        const updatedTask = updatedTasks.find(
          (task) => task.id === prevTask.id,
        );
        return updatedTask ? updatedTask : prevTask;
      }),
    );
  });

  useSubscription(`/topic/${boardId}/new-chat-message`, (message) => {
    const chatMessage: Message = JSON.parse(message.body);
    setMessages((prevMessages) => [...prevMessages, chatMessage]);
    setNewMessages(true);
  });

  useEffect(() => {
    const controller = new AbortController();
    const signal = controller.signal;

    fetch(
      `${baseUrl}/api/v1/boards/${boardId}?includeTasks=true&includeChatMessages=true`,
      { signal },
    )
      .then((response) => {
        if (response.ok) {
          return response.json();
        } else {
          navigate('/not-found');
          throw Error('Board ' + boardId + ' not found');
        }
      })
      .then((data) => {
        setBoardName(data.name);
        setTasks((prevTasks) => {
          const taskIds = new Set();
          const allTasks = [...data.tasks, ...prevTasks];
          return allTasks.filter(({ id }) => {
            if (taskIds.has(id)) {
              return false;
            }
            taskIds.add(id);
            return true;
          });
        });
        setMessages((prevMessages) => {
          const messageIds = new Set();
          const allMessages = [...data.chat_messages, ...prevMessages];
          return allMessages.filter(({ id }) => {
            if (messageIds.has(id)) {
              return false;
            }
            messageIds.add(id);
            return true;
          });
        });
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          setBoardInitComplete(true);
        }
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
  }, [boardId]);

  const sendMessage = (newMessage: string) => {
    if (newMessage.trim() !== '') {
      const message: NewMessage = {
        username: username ?? 'anonymous',
        content: newMessage,
      };

      stompClient?.publish({
        destination: `/app/${boardId}/new-chat-message`,
        body: JSON.stringify(message),
      });
    }
  };

  const createTask = (newTask: NewTask) => {
    stompClient?.publish({
      destination: `/app/${boardId}/new-task`,
      body: JSON.stringify(newTask),
    });
  };

  const updateTask = (taskId: number, task: UpdateTask) => {
    stompClient?.publish({
      destination: `/app/${boardId}/update-task/${taskId}`,
      body: JSON.stringify(task),
    });
  };

  const boardContextValue: BoardContextProps = {
    boardInitComplete,
    boardName,
    tasks,
    messages,
    newMessages,
    setNewMessages,
    createTask,
    updateTask,
    sendMessage,
  };

  return (
    <BoardContext.Provider value={boardContextValue}>
      {children}
    </BoardContext.Provider>
  );
};
