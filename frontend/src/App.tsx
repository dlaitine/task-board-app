import { StompSessionProvider } from 'react-stomp-hooks';
import { TaskListContent } from './task/TaskListContent';
import { SocketErrorPopUp } from './task/error/SocketErrorPopUp';
import { NewTaskForm } from './task/form/NewTaskForm';
import { createTheme, ThemeProvider, } from '@mui/material';
import ChatWindow from './task/chat/ChatWindow';
import  '@fontsource/neucha';
import { ResponsiveAppBar } from './task/ResponsiveAppBar';

const theme = createTheme({
  typography: {
    fontFamily: 'Neucha',
    allVariants: {
      color: '#0077B6',
    }
  },
});

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <StompSessionProvider
        url={'http://localhost:8080/ws'}>
        <ResponsiveAppBar />
        <SocketErrorPopUp />
        <NewTaskForm />
        <TaskListContent />
        <ChatWindow />
      </StompSessionProvider>
    </ThemeProvider>
  );
};

export default App;