import { useContext } from 'react';
import { LoginContext } from './context/LoginContext';
import { LoginForm } from './form/LoginForm';
import { Route, Routes } from 'react-router-dom';
import { BoardPage } from './board/BoardPage';
import { ResponsiveAppBar } from './ResponsiveAppBar';
import { ErrorPopUp } from './error/ErrorPopUp';
import { useSubscription } from 'react-stomp-hooks';
import { NotificationContext } from './context/NotificationContext';
import { NotFoundPage } from './error/NotFoundPage';
import { HomePage } from './HomePage';

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