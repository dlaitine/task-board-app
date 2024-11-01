import { useContext } from 'react';
import { LoginContext } from './task/context/LoginContext';
import { LoginForm } from './task/form/LoginForm';
import { Route, Routes } from 'react-router-dom';
import { BoardPage } from './task/BoardPage';
import { HomePage } from './task/HomePage';
import { ResponsiveAppBar } from './task/ResponsiveAppBar';
import { ErrorPopUp } from './task/error/ErrorPopUp';
import { useSubscription } from 'react-stomp-hooks';
import { NotificationContext } from './task/context/NotificationContext';
import { NotFoundPage } from './task/error/NotFoundPage';

const App = () => {
  const { username, }  = useContext(LoginContext);
  const { setMessage, setSeverity, } = useContext(NotificationContext);

  useSubscription('/user/topic/error', (error) => {
    setMessage(error.body);
    setSeverity('error');
  });

  if (!username) {
    return <LoginForm />;
  }

  return (
    <>
      <ResponsiveAppBar />
      <ErrorPopUp />
      <Routes>
        <Route path='/' element={<HomePage />}></Route>
        <Route path='/:boardId' element={<BoardPage />}></Route>
        <Route path='/not-found' element={<NotFoundPage />}></Route>
      </Routes>
    </>
  );
};

export default App;