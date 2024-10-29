import { StompSessionProvider } from 'react-stomp-hooks';
import { TaskListContent } from './task/TaskListContent';
import { SocketErrorPopUp } from './task/error/SocketErrorPopUp';
import { NewTaskForm } from './task/form/NewTaskForm';
import { createTheme, ThemeProvider, } from '@mui/material';
import ChatWindow from './task/chat/ChatWindow';
import  '@fontsource/neucha';
import { ResponsiveAppBar } from './task/ResponsiveAppBar';
import { useContext } from 'react';
import { LoginContext } from './task/context/LoginContext';
import { LoginForm } from './task/form/LoginForm';

const theme = createTheme({
  typography: {
    fontFamily: 'Neucha',
    allVariants: {
      color: '#0077B6',
    }
  },
});

const App = () => {
  const { username, }  = useContext(LoginContext);

  return (
      <ThemeProvider theme={theme}>
        { username ?
          <StompSessionProvider
            url={'http://localhost:8080/ws'}>
            <ResponsiveAppBar />
            <SocketErrorPopUp />
            <NewTaskForm />
            <TaskListContent />
            <ChatWindow />
          </StompSessionProvider>
          : <LoginForm />
        }
      </ThemeProvider>
      
  );
};

export default App;